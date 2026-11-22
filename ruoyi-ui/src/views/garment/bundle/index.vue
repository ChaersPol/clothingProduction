<template>
  <div class="app-container garment-page">
    <el-card class="section-card" shadow="never">
      <template #header><span>扫码操作</span></template>
      <el-form class="scan-bar" :inline="true" @submit.prevent>
        <el-form-item label="扎卡条码" class="scan-input">
          <el-input ref="scanRef" v-model="scanCode" placeholder="扫描扎卡条码或扎号后回车" clearable @keyup.enter="doStockIn" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Upload" v-hasPermi="['garment:bundle:edit']" @click="doStockIn">裁片入库</el-button>
          <el-button icon="MagicStick" v-hasPermi="['garment:bundle:add']" @click="autoOpen = true">自动分扎</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-form v-show="showSearch" class="search-form" :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item label="裁货单号" prop="cutNo">
        <el-input v-model="queryParams.cutNo" placeholder="单号" clearable style="width: 150px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="款号" prop="styleNo">
        <el-input v-model="queryParams.styleNo" placeholder="款号" clearable style="width: 120px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="扎号" prop="bundleNo">
        <el-input v-model="queryParams.bundleNo" placeholder="扎号" clearable style="width: 120px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
          <el-option v-for="(v, k) in statusMap" :key="k" :label="v" :value="k" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Printer" v-hasPermi="['garment:bundle:print']" :disabled="multiple" @click="printCards">打印扎卡</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" v-hasPermi="['garment:bundle:remove']" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <div class="table-wrap">
      <el-table v-loading="loading" :data="bundleList" border @selection-change="handleSelectionChange">
        <el-table-column v-if="checkPermi(['garment:bundle:remove'])" type="selection" width="50" align="center" />
        <el-table-column label="扎号" prop="bundleNo" width="150" fixed="left" />
        <el-table-column label="裁货单号" prop="cutNo" width="150" />
        <el-table-column label="款号" prop="styleNo" min-width="100" />
        <el-table-column label="颜色" prop="color" min-width="100" />
        <el-table-column label="尺码" prop="size" width="65" align="center" />
        <el-table-column label="数量" prop="qty" width="70" align="right" />
        <el-table-column label="已交后道" prop="deliveredQty" width="85" align="right" />
        <el-table-column label="已包装" prop="packedQty" width="75" align="right" />
        <el-table-column label="次品" prop="defectQty" width="65" align="right" />
        <el-table-column label="状态" prop="status" width="115" align="center">
          <template #default="s">
            <el-tag size="small" :type="statusType(s.row.status)">{{ statusMap[s.row.status] || s.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="领片人" prop="pickupUser" min-width="100" />
        <el-table-column v-if="checkPermi(['garment:bundle:edit','garment:bundle:patch','garment:bundle:remove'])" label="操作" class-name="op-cell" width="220" align="center" fixed="right">
          <template #default="s">
            <el-button link type="primary" v-hasPermi="['garment:bundle:edit']" :disabled="s.row.status !== '0'" @click="stockInRow(s.row)">入库</el-button>
            <el-button link type="warning" v-hasPermi="['garment:bundle:patch']" @click="openPatch(s.row)">补片/报废</el-button>
            <el-button link type="danger" v-hasPermi="['garment:bundle:remove']" @click="handleDelete(s.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 打印区域：挂到 body 下，打印时隐藏其余内容并自动分页 -->
    <teleport to="body">
      <div id="bundle-print-area">
      <div v-for="b in printRows" :key="b.bundleId" class="bundle-card">
        <div class="card-title">服装厂扎卡</div>
        <div class="card-no">{{ b.bundleNo }}</div>
        <svg class="card-barcode" :data-code="b.bundleNo"></svg>
        <div class="card-row">{{ b.styleNo }} / {{ b.color }} / {{ b.size }}</div>
        <div class="card-row">数量：{{ b.qty }} 件</div>
        <div class="card-row">裁货单：{{ b.cutNo }}</div>
      </div>
      </div>
    </teleport>

    <el-dialog title="自动分扎" v-model="autoOpen" width="520px" append-to-body>
      <el-form label-width="100px">
        <el-form-item label="裁货单" required>
          <el-select v-model="autoForm.cutId" placeholder="选择裁货单" filterable style="width: 100%">
            <el-option v-for="c in cutOrders" :key="c.cutId" :label="`${c.cutNo} ${c.styleNo}（${c.totalQty}件）`" :value="c.cutId" />
          </el-select>
        </el-form-item>
        <el-form-item label="每扎件数">
          <el-input-number v-model="autoForm.perBundle" :min="1" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-alert type="info" :closable="false" show-icon title="系统按裁货单颜色尺码明细自动拆分扎包，余数自动单独成扎。" />
      </el-form>
      <template #footer>
        <el-button type="primary" v-hasPermi="['garment:bundle:add']" @click="submitAuto">开始分扎</el-button>
        <el-button @click="autoOpen = false">取 消</el-button>
      </template>
    </el-dialog>

    <el-dialog title="补片 / 报废" v-model="patchOpen" width="560px" append-to-body>
      <el-form label-width="90px">
        <el-form-item label="扎号">{{ patchForm.bundleNo }}</el-form-item>
        <el-form-item label="处理方式">
          <el-radio-group v-model="patchForm.defectType">
            <el-radio value="补片">补片（增加裁片库存）</el-radio>
            <el-radio value="报废">报废（扣减裁片库存）</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="数量"><el-input-number v-model="patchForm.qty" :min="1" controls-position="right" style="width: 100%" /></el-form-item>
        <el-form-item label="处理人">
          <el-input :model-value="operatorName" disabled style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="patchForm.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" v-hasPermi="['garment:bundle:patch']" @click="submitPatch">确 定</el-button>
        <el-button @click="patchOpen = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Bundle">
import { useRoute } from 'vue-router'
import JsBarcode from 'jsbarcode'
import { listBundle, listCutOrder, autoBundle, bundleStockIn, patchBundle, delBundle } from '@/api/garment'
import { currentOperatorName } from '@/utils/garment'

const showSearch = ref(true)

const ids = ref([])
const multiple = ref(true)
const selectedRows = ref([])
const printRows = ref([])

function handleSelectionChange(selection) {
  selectedRows.value = selection
  ids.value = selection.map(item => item.bundleId)
  multiple.value = !selection.length
}

function printCards() {
  printRows.value = [...selectedRows.value]
  nextTick(() => {
    document.querySelectorAll('#bundle-print-area .card-barcode').forEach(svg => {
      JsBarcode(svg, svg.dataset.code, { format: 'CODE128', width: 1.6, height: 46, displayValue: false, margin: 0 })
    })
    setTimeout(() => window.print(), 300)
  })
}

const { proxy } = getCurrentInstance()
const route = useRoute()
const statusMap = { '0': '已开扎', '1': '裁片在库', '2': '产线中', '3': '已完工' }
const bundleList = ref([])
const cutOrders = ref([])
// 补片 / 报废处理人固定为当前登录用户
const operatorName = currentOperatorName()
const loading = ref(false)
const total = ref(0)
const scanCode = ref('')
const scanRef = ref()
const autoOpen = ref(false)
const patchOpen = ref(false)
const autoForm = ref({ cutId: undefined, perBundle: 17 })
const patchForm = ref({})
const queryParams = ref({ pageNum: 1, pageSize: 10, cutNo: undefined, styleNo: undefined, bundleNo: undefined, status: undefined })

// 支持从消息提醒深链过来，直接定位到指定状态的扎
watch(() => route.query, q => {
  queryParams.value.status = q.status || undefined
  queryParams.value.bundleNo = q.bundleNo || undefined
  if (q.pendingPack) {
    queryParams.value.params = { pendingPack: 1 }
  } else {
    delete queryParams.value.params
  }
  queryParams.value.pageNum = 1
  getList()
}, { immediate: true })

function statusType(s) {
  return s === '3' ? 'success' : s === '2' ? 'primary' : s === '1' ? 'warning' : 'info'
}
function getList() {
  loading.value = true
  listBundle(queryParams.value).then(res => {
    bundleList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}
function loadCuts() {
  return listCutOrder({ pageNum: 1, pageSize: 100 }).then(res => { cutOrders.value = res.rows || [] })
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
// 扫码枪：页面进入/操作完成后自动聚焦扫码框，支持连续扫描
function focusScan() {
  nextTick(() => { if (scanRef.value) scanRef.value.focus() })
}
function doStockIn() {
  if (!scanCode.value) {
    proxy.$modal.msgWarning('请输入或扫描扎卡条码')
    return
  }
  bundleStockIn(scanCode.value.trim()).then(() => {
    proxy.$modal.msgSuccess('裁片入库成功')
    scanCode.value = ''
    focusScan()
    getList()
  })
}
function stockInRow(row) {
  bundleStockIn(row.barcode || row.bundleNo).then(() => {
    proxy.$modal.msgSuccess('裁片入库成功')
    getList()
  })
}
function submitAuto() {
  if (!autoForm.value.cutId) {
    proxy.$modal.msgWarning('请选择裁货单')
    return
  }
  autoBundle(autoForm.value).then(() => {
    proxy.$modal.msgSuccess('分扎完成')
    autoOpen.value = false
    getList()
    loadCuts()
  })
}
function openPatch(row) {
  patchForm.value = { bundleId: row.bundleId, bundleNo: row.bundleNo, defectType: '补片', qty: 1, handler: operatorName, remark: '' }
  patchOpen.value = true
}
function submitPatch() {
  patchBundle(patchForm.value).then(() => {
    proxy.$modal.msgSuccess('处理成功')
    patchOpen.value = false
    getList()
  })
}
function handleDelete(row) {
  const _ids = row ? [row.bundleId] : ids.value
  if (!_ids.length) return
  proxy.$modal.confirm(row ? `确认删除扎包「${row.bundleNo}」？` : `是否确认删除选中的 ${_ids.length} 条数据？`).then(() => delBundle(_ids)).then(() => {
    proxy.$modal.msgSuccess('删除成功')
    getList()
    ids.value = []
    multiple.value = true
  }).catch(() => {})
}

loadCuts().then(() => {
  if (route.query.cutId) {
    autoForm.value.cutId = Number(route.query.cutId)
    autoOpen.value = true
  }
})

// 从标签页切回本页时自动刷新数据，避免展示缓存中的旧列表
let _pageActivated = false
onActivated(() => {
  if (_pageActivated) {
    getList()
    loadCuts()
  } else {
    _pageActivated = true
  }
  focusScan()
})
onMounted(focusScan)

</script>

<style scoped>
#bundle-print-area {
  display: none;
}

@media print {
  #bundle-print-area {
    display: block;
  }
  .bundle-card {
    display: inline-block;
    vertical-align: top;
    width: 46%;
    margin: 0 2% 10px 0;
    box-sizing: border-box;
    border: 1px solid #000;
    border-radius: 6px;
    padding: 10px 12px;
    text-align: center;
    page-break-inside: avoid;
    break-inside: avoid;
  }
  .bundle-card .card-title {
    font-size: 14px;
    font-weight: 700;
  }
  .bundle-card .card-no {
    font-size: 18px;
    font-weight: 700;
    letter-spacing: 1px;
    margin: 6px 0;
  }
  .bundle-card .card-barcode {
    width: 100%;
    height: 52px;
  }
  .bundle-card .card-row {
    font-size: 13px;
    line-height: 1.7;
  }
}
</style>
