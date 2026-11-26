<template>
  <div class="app-container garment-page">
    <el-form v-show="showSearch" class="search-form" :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item label="裁货单号" prop="cutNo">
        <el-input v-model="queryParams.cutNo" placeholder="单号" clearable style="width: 160px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="款号" prop="styleNo">
        <el-input v-model="queryParams.styleNo" placeholder="款号" clearable style="width: 130px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="裁工" prop="cutter">
        <el-select v-model="queryParams.cutter" placeholder="裁工" clearable filterable style="width: 120px">
          <el-option v-for="e in employees" :key="e.userId" :label="e.displayName" :value="e.displayName" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="state">
        <el-select v-model="queryParams.state" placeholder="全部" clearable style="width: 150px">
          <el-option label="待分扎" value="pending" />
          <el-option label="分扎中" value="splitting" />
          <el-option label="已分完待入库" value="split_done" />
          <el-option label="已入库" value="stored" />
          <el-option label="已完成" value="done" />
        </el-select>
      </el-form-item>
      <el-form-item label="裁货日期">
        <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD" start-placeholder="开始" end-placeholder="结束" style="width: 240px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['garment:cutorder:add']">裁货开单</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button plain icon="Download" v-hasPermi="['garment:cutorder:export']" @click="handleExport">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['garment:cutorder:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <div class="table-wrap">
      <el-table v-loading="loading" :data="orderList" border @selection-change="handleSelectionChange">
            <el-table-column v-if="checkPermi(['garment:cutorder:remove'])" type="selection" width="50" align="center" />
            
        <el-table-column label="裁货单号" prop="cutNo" width="150" fixed="left" />
        <el-table-column label="款号" prop="styleNo" width="100" />
        <el-table-column label="品名" prop="cnName" min-width="140" show-overflow-tooltip />
        <el-table-column label="客户" prop="customer" width="110" />
        <el-table-column label="面料" prop="fabric" min-width="110" show-overflow-tooltip />
        <el-table-column label="总件数" prop="totalQty" width="85" align="right" />
        <el-table-column label="层数" prop="layerCount" width="70" align="center" />
        <el-table-column label="裁工" prop="cutter" width="90" />
        <el-table-column label="裁货日期" prop="cutDate" width="110" />
        <el-table-column label="扎数" prop="bundleCount" width="70" align="center" />
        <el-table-column label="状态" prop="status" width="120" align="center">
          <template #default="s">
            <el-tag size="small" :type="statusInfo(s.row).type">{{ statusInfo(s.row).text }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="checkPermi(['garment:cutorder:edit','garment:cutorder:remove'])" label="操作" class-name="op-cell" width="295" align="center" fixed="right">
          <template #default="s">
            <el-button link type="primary" v-hasPermi="['garment:cutorder:list']" @click="viewDetail(s.row)">明细</el-button>
            <el-button link type="success" v-hasPermi="['garment:bundle:list']" v-if="canBundle(s.row)" @click="goBundle(s.row)">去分扎</el-button>
            <el-button link type="primary" @click="handleUpdate(s.row)" v-hasPermi="['garment:cutorder:edit']" :disabled="s.row.status !== '0'">修改</el-button>
            <el-button link type="warning" @click="handleVoid(s.row)" v-hasPermi="['garment:cutorder:edit']" v-if="s.row.status === '0'">作废</el-button>
            <el-button link type="danger" @click="handleDelete(s.row)" v-hasPermi="['garment:cutorder:remove']" :disabled="s.row.status !== '0'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="form.cutId ? '修改裁货单' : '裁货开单'" v-model="open" width="900px" append-to-body>
      <el-form ref="cutRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="12">
          <el-col :xs="24" :sm="8">
            <el-form-item label="款号" prop="styleNo">
              <el-select v-model="form.styleNo" placeholder="选择款号" filterable style="width: 100%" @change="onStyleChange">
                <el-option v-for="s in styles" :key="s.styleNo" :label="s.styleNo + ' ' + s.cnName" :value="s.styleNo" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="品名"><el-input v-model="form.cnName" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="客户">
              <el-select v-model="form.customer" placeholder="选择客户" filterable clearable style="width: 100%">
                <el-option v-for="c in customers" :key="c.customerId" :label="c.customerName" :value="c.customerName" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="面料"><el-input v-model="form.fabric" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="里布"><el-input v-model="form.lining" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="层数"><el-input-number v-model="form.layerCount" :min="0" controls-position="right" style="width: 100%" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="裁工">
              <el-select v-model="form.cutter" placeholder="选择裁工" filterable style="width: 100%">
                <el-option v-for="e in employees" :key="e.userId" :label="e.displayName" :value="e.displayName" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="裁货日期"><el-date-picker v-model="form.cutDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="总件数"><el-input v-model="form.totalQty" disabled /></el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">裁片明细</el-divider>
        <div class="toolbar">
          <el-select v-model="gen.colors" multiple collapse-tags placeholder="选择颜色" size="small" style="width: 200px">
            <el-option v-for="c in colors" :key="c.cnName" :label="c.cnName" :value="c.cnName">
              <div class="color-opt"><span>{{ c.cnName }}</span><span class="color-opt-chip" :style="{ background: c.colorCode }"></span></div>
            </el-option>
          </el-select>
          <el-select v-model="gen.sizes" multiple collapse-tags placeholder="选择尺码" size="small" style="width: 180px">
            <el-option v-for="s in sizes" :key="s.sizeName" :label="s.sizeName" :value="s.sizeName" />
          </el-select>
          <el-input-number v-model="gen.qty" :min="1" controls-position="right" size="small" style="width: 110px" />
          <el-button type="primary" plain size="small" v-hasPermi="['garment:cutorder:edit']" @click="generateMatrix">生成明细</el-button>
          <el-button size="small" icon="Plus" v-hasPermi="['garment:cutorder:edit']" @click="addRow">手动添加</el-button>
        </div>
        <el-table :data="form.items" border size="small" max-height="280">
          <el-table-column label="层次" width="80">
            <template #default="s"><el-input-number v-model="s.row.layerNo" :min="1" size="small" controls-position="right" style="width: 100%" /></template>
          </el-table-column>
          <el-table-column label="颜色" min-width="150">
            <template #default="s">
              <el-select v-model="s.row.color" size="small" filterable placeholder="颜色" style="width: 100%">
                <el-option v-for="c in colors" :key="c.cnName" :label="c.cnName" :value="c.cnName" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="尺码" min-width="110">
            <template #default="s">
              <el-select v-model="s.row.size" size="small" filterable placeholder="尺码" style="width: 100%">
                <el-option v-for="sz in sizeOptions" :key="sz" :label="sz" :value="sz" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="数量" width="130">
            <template #default="s"><el-input-number v-model="s.row.qty" :min="0" size="small" controls-position="right" style="width: 100%" /></template>
          </el-table-column>
          <el-table-column label="操作" class-name="op-cell" width="80" align="center">
            <template #default="s"><el-button link type="danger" @click="form.items.splice(s.$index, 1)">删除</el-button></template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <el-button type="primary" v-hasPermi="['garment:cutorder:add','garment:cutorder:edit']" @click="submitForm">保存裁货单</el-button>
        <el-button @click="open = false">取 消</el-button>
      </template>
    </el-dialog>

    <el-dialog title="物资库存不足" v-model="overdraftOpen" width="480px" append-to-body>
      <el-alert type="warning" :closable="false" show-icon :title="overdraftMsg" />
      <el-form label-width="90px" style="margin-top: 14px">
        <el-form-item label="超领原因" required>
          <el-input v-model="overdraftReason" type="textarea" :rows="2" placeholder="如 采购在途、紧急插单、客户催货" />
        </el-form-item>
      </el-form>
      <div class="muted" style="font-size: 12px; color: #909399; padding-left: 4px">
        确认后将按 BOM 用量照常领料，库存可能变为负数，并在物资流水中记录本次超领原因。
      </div>
      <template #footer>
        <el-button type="primary" v-hasPermi="['garment:cutorder:add','garment:cutorder:edit']" :disabled="!overdraftReason" @click="confirmOverdraft">确认超领并保存</el-button>
        <el-button @click="overdraftOpen = false">取 消</el-button>
      </template>
    </el-dialog>

    <el-dialog title="裁货明细" v-model="detailOpen" width="720px" append-to-body>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="裁货单号">{{ detail.cutNo }}</el-descriptions-item>
        <el-descriptions-item label="款号">{{ detail.styleNo }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ detail.customer }}</el-descriptions-item>
        <el-descriptions-item label="总件数">{{ detail.totalQty }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="detail.items || []" border size="small" style="margin-top: 12px">
        <el-table-column label="层次" prop="layerNo" min-width="80" />
        <el-table-column label="颜色" prop="color" width="140" />
        <el-table-column label="尺码" prop="size" width="100" />
        <el-table-column label="数量" prop="qty" width="100" align="right" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup name="CutOrder">
import { useRouter } from 'vue-router'
import { listCutOrder, getCutOrder, addCutOrder, updateCutOrder, delCutOrder, voidCut, commonStyles, commonColors, commonSizes, commonUsers, commonCustomers } from '@/api/garment'

const showSearch = ref(true)

const ids = ref([])
const multiple = ref(true)

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.cutId)
  multiple.value = !selection.length
}

const { proxy } = getCurrentInstance()
const router = useRouter()
const orderList = ref([])
const styles = ref([])
const colors = ref([])
const sizes = ref([])
const customers = ref([])
const styleSizes = ref([])
// 尺码优先取款号尺码表，未配置时退回基础资料全部尺码
const sizeOptions = computed(() => styleSizes.value.length ? styleSizes.value : sizes.value.map(i => i.sizeName))
const employees = ref([])
const loading = ref(false)
const total = ref(0)
const open = ref(false)
const detailOpen = ref(false)
const detail = ref({})
const dateRange = ref([])
const form = ref({})
const gen = ref({ colors: [], sizes: [], qty: 1 })
const queryParams = ref({ pageNum: 1, pageSize: 10, cutNo: undefined, styleNo: undefined, cutter: undefined, status: undefined, state: undefined })
const route = useRoute()
const overdraftOpen = ref(false)
const overdraftMsg = ref('')
const overdraftReason = ref('')
const pendingAdd = ref(null)

// 支持从消息提醒/首页卡片深链过来，直接定位到指定状态、单号或日期的裁货单
watch(() => route.query, q => {
  queryParams.value.state = q.status === '1' ? 'stored' : q.status === '2' ? 'done' : undefined
  queryParams.value.status = q.status === '0' ? '0' : undefined
  queryParams.value.cutNo = q.cutNo || undefined
  dateRange.value = q.beginDate ? [q.beginDate, q.endDate || q.beginDate] : []
  queryParams.value.pageNum = 1
  getList()
  if (q.action === 'add') {
    pendingAdd.value = { ...q }
    tryPrefill()
  }
}, { immediate: true })

// 从库存预警「去补货」跳转过来时，自动打开开单弹窗并预填缺货款色码
function tryPrefill() {
  if (!pendingAdd.value || !styles.value.length) return
  const q = pendingAdd.value
  pendingAdd.value = null
  handleAdd()
  form.value.styleNo = q.styleNo
  onStyleChange(q.styleNo)
  form.value.items = [{ layerNo: 1, color: q.color, size: q.size, qty: Number(q.qty) || 1 }]
}
const rules = { styleNo: [{ required: true, message: '请选择款号', trigger: 'change' }] }

const totalQty = computed(() => (form.value.items || []).reduce((s, i) => s + Number(i.qty || 0), 0))
watch(totalQty, v => { form.value.totalQty = v })

// 状态按「已分件数 vs 总件数」细分，避免分完扎仍显示「待分扎」的歧义
function statusInfo(row) {
  const s = row.status
  if (s === '9') return { text: '已作废', type: 'info' }
  if (s === '2') return { text: '已完成', type: 'success' }
  if (s === '1') return { text: '已入库', type: 'warning' }
  const done = Number(row.bundleQty || 0)
  const total = Number(row.totalQty || 0)
  if (done <= 0) return { text: '待分扎', type: 'danger' }
  if (done < total) return { text: '分扎中', type: 'warning' }
  return { text: '已分完待入库', type: 'success' }
}
// 仅当裁货单未作废/未完成、且仍有未分扎数量时才显示「去分扎」
function canBundle(row) {
  return row.status !== '9' && row.status !== '2' && Number(row.bundleQty || 0) < Number(row.totalQty || 0)
}
function handleVoid(row) {
  proxy.$modal.prompt(`作废原因（${row.cutNo} 作废后原单保留，已领物资按流水退回）：`).then(({ value }) => {
    if (!value || !value.trim()) {
      proxy.$modal.msgWarning('请填写作废原因')
      return
    }
    voidCut(row.cutId, value.trim()).then(() => {
      proxy.$modal.msgSuccess('已作废，原单已保留')
      getList()
    })
  }).catch(() => {})
}
function today() {
  const d = new Date()
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}
function getList() {
  loading.value = true
  const params = { ...queryParams.value }
  const state = params.state
  delete params.state
  if (state === 'pending' || state === 'splitting' || state === 'split_done') {
    params.progress = state
    delete params.status
  } else if (state === 'stored') {
    params.status = '1'
  } else if (state === 'done') {
    params.status = '2'
  }
  if (dateRange.value && dateRange.value.length === 2) {
    params.params = { beginDate: dateRange.value[0], endDate: dateRange.value[1] }
  }
  listCutOrder(params).then(res => {
    orderList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); queryParams.value.state = undefined; queryParams.value.status = undefined; dateRange.value = []; handleQuery() }
function onStyleChange(styleNo) {
  const s = styles.value.find(i => i.styleNo === styleNo)
  if (s) {
    form.value.cnName = s.cnName
    styleSizes.value = (s.sizeTable || '').split(',').map(i => i.trim()).filter(Boolean)
  } else {
    styleSizes.value = []
  }
}
function addRow() {
  if (!form.value.items) form.value.items = []
  form.value.items.push({ layerNo: 1, color: '', size: '', qty: 0 })
}
function generateMatrix() {
  if (!gen.value.colors.length || !gen.value.sizes.length) {
    proxy.$modal.msgWarning('请先选择颜色和尺码')
    return
  }
  const items = []
  gen.value.colors.forEach(c => {
    gen.value.sizes.forEach(s => {
      items.push({ layerNo: 1, color: c, size: s, qty: gen.value.qty })
    })
  })
  form.value.items = items
}
function handleAdd() {
  form.value = { styleNo: undefined, cnName: '', customer: '', fabric: '', lining: '', layerCount: 1, cutter: '', cutDate: today(), totalQty: 0, items: [] }
  gen.value = { colors: [], sizes: [], qty: 1 }
  open.value = true
}
function handleUpdate(row) {
  getCutOrder(row.cutId).then(res => {
    form.value = res.data
    if (!form.value.items) form.value.items = []
    open.value = true
  })
}
function submitForm() {
  proxy.$refs['cutRef'].validate(valid => {
    if (!valid) return
    if (!form.value.items || !form.value.items.length) {
      proxy.$modal.msgWarning('请录入裁片明细')
      return
    }
    form.value.totalQty = totalQty.value
    const fn = form.value.cutId ? updateCutOrder : addCutOrder
    fn(form.value).then(() => {
      proxy.$modal.msgSuccess('保存成功')
      open.value = false
      getList()
    }).catch(err => {
      const msg = (err && (err.message || err.msg)) || ''
      if (msg.includes('库存不足')) {
        overdraftMsg.value = msg
        overdraftReason.value = ''
        overdraftOpen.value = true
      }
    })
  })
}
// 确认超领：带原因重新提交
function confirmOverdraft() {
  if (!overdraftReason.value) {
    proxy.$modal.msgWarning('请填写超领原因')
    return
  }
  form.value.allowOverdraft = true
  form.value.overdraftReason = overdraftReason.value
  addCutOrder(form.value).then(() => {
    proxy.$modal.msgSuccess('已超领保存，并记录超领原因')
    overdraftOpen.value = false
    open.value = false
    getList()
  })
}
function viewDetail(row) {
  getCutOrder(row.cutId).then(res => { detail.value = res.data; detailOpen.value = true })
}
// 未授权菜单不在动态路由中，跳转前校验，避免进入 404
function canAccess(path) {
  const resolved = router.resolve({ path })
  return !!resolved.name && !String(resolved.name).startsWith('404')
}
function goBundle(row) {
  if (!canAccess('/cut/bundle')) {
    proxy.$modal.msgWarning('当前账号没有「分扎」模块的访问权限')
    return
  }
  router.push({ path: '/cut/bundle', query: { cutId: row.cutId } })
}
function handleDelete(row) {
  const isRow = !!(row && row.cutId)
  const _ids = isRow ? [row.cutId] : ids.value
  if (!_ids.length) return
  proxy.$modal.confirm(isRow ? `确认删除裁货单「${row.cutNo}」？关联扎包也会一并清理。` : `是否确认删除选中的 ${_ids.length} 条数据？`).then(() => delCutOrder(_ids)).then(() => {
    proxy.$modal.msgSuccess('删除成功')
    getList()
    ids.value = []
    multiple.value = true
  }).catch(() => {})
}
function handleExport() {
  proxy.download('garment/cutorder/export', { ...queryParams.value }, `cutorder_${Date.now()}.xlsx`)
}

commonStyles().then(res => { styles.value = res.data || []; tryPrefill() })
commonColors().then(res => { colors.value = res.data || [] })
commonSizes().then(res => { sizes.value = res.data || [] })
commonUsers('裁床').then(res => { employees.value = res.data || [] })
commonCustomers().then(res => { customers.value = res.data || [] })

// 从标签页切回本页时自动刷新数据，避免展示缓存中的旧列表
let _pageActivated = false
onActivated(() => {
  if (_pageActivated) {
    getList()
  } else {
    _pageActivated = true
  }
})

</script>
