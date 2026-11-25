<template>
  <div class="app-container garment-page">
    <el-form v-show="showSearch" class="search-form" :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item label="出库单号" prop="outboundNo">
        <el-input v-model="queryParams.outboundNo" placeholder="单号" clearable style="width: 150px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="客户" prop="customer">
        <el-input v-model="queryParams.customer" placeholder="客户" clearable style="width: 130px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
          <el-option label="正常" value="0" />
          <el-option label="已作废" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="出库日期">
        <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD" start-placeholder="开始" end-placeholder="结束" style="width: 240px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" v-hasPermi="['garment:outbound:add']" @click="handleAdd">成品出库</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button plain icon="Download" v-hasPermi="['garment:outbound:export']" @click="handleExport">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <div class="table-wrap">
      <el-table v-loading="loading" :data="outboundList" border show-summary :summary-method="getSummary">
        <el-table-column label="出库单号" prop="outboundNo" width="150" fixed="left" />
        <el-table-column label="客户" prop="customer" min-width="130" show-overflow-tooltip />
        <el-table-column label="出库日期" prop="outboundDate" width="110" />
        <el-table-column label="总数量" prop="totalQty" width="90" align="right" />
        <el-table-column label="总金额" prop="totalAmount" width="110" align="right" />
        <el-table-column label="操作人" prop="operator" width="100" />
        <el-table-column label="状态" prop="status" width="105" align="center">
          <template #default="s">
            <el-tag size="small" :type="s.row.status === '1' ? 'danger' : 'success'">{{ s.row.status === '1' ? '已作废' : '正常' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" class-name="op-cell" width="140" align="center" fixed="right">
          <template #default="s">
            <el-button link type="primary" v-hasPermi="['garment:outbound:list']" @click="viewDetail(s.row)">明细</el-button>
            <el-button link type="danger" v-hasPermi="['garment:outbound:edit']" :disabled="s.row.status === '1'" @click="handleCancel(s.row)">作废</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="成品出库开单" v-model="open" width="940px" append-to-body>
      <el-form label-width="90px">
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="客户">
              <el-select v-model="form.customer" placeholder="选择客户" filterable clearable style="width: 100%">
                <el-option v-for="c in customers" :key="c.customerId" :label="c.customerName" :value="c.customerName" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="出库日期"><el-date-picker v-model="form.outboundDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" /></el-form-item>
          </el-col>
        </el-row>
        <div class="toolbar">
          <el-input ref="scanRef" v-model="scanBarcode" placeholder="扫描或输入商品条码自动添加" style="width: 260px" @input="onBarcodeInput" @keyup.enter="onBarcodeEnter(scanBarcode)" />
          <GarmentScan size="small" v-hasPermi="['garment:outbound:add']" @scan="onScanBarcode" @error="m => proxy.$modal.msgWarning(m)" />
          <el-button size="small" icon="Plus" v-hasPermi="['garment:outbound:add']" @click="addEmptyItem">手动添加</el-button>
        </div>
        <el-table :data="form.items" border size="small" max-height="300">
          <el-table-column label="条码" min-width="160">
            <template #default="s">
              <el-input v-if="s.row._manual" v-model="s.row.barcode" size="small" placeholder="扫码或输入条码" @change="resolveRowBarcode(s.row)" />
              <span v-else>{{ s.row.barcode }}</span>
            </template>
          </el-table-column>
          <el-table-column label="款号 / 颜色 / 尺码" width="180">
            <template #default="s">
              <el-select v-if="s.row._manual" v-model="s.row.skuId" size="small" filterable clearable
                placeholder="选择商品" style="width: 100%" @change="id => onRowSkuChange(s.row, id)">
                <el-option v-for="k in skuOptions" :key="k.skuId" :label="`${k.styleNo} ${k.color} ${k.size}`" :value="k.skuId" />
              </el-select>
              <span v-else>{{ s.row.styleNo }} / {{ s.row.color }} / {{ s.row.size }}</span>
            </template>
          </el-table-column>
          <el-table-column label="单价" width="100">
            <template #default="s"><el-input-number v-model="s.row.price" :min="0" :precision="2" size="small" controls-position="right" style="width: 100%" /></template>
          </el-table-column>
          <el-table-column label="数量" width="100">
            <template #default="s"><el-input-number v-model="s.row.qty" :min="1" size="small" controls-position="right" style="width: 100%" /></template>
          </el-table-column>
          <el-table-column label="箱号" width="100">
            <template #default="s"><el-input v-model="s.row.boxNo" size="small" placeholder="整箱填写" /></template>
          </el-table-column>
          <el-table-column label="金额" width="85" align="right">
            <template #default="s">{{ ((s.row.price || 0) * (s.row.qty || 0)).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column label="操作" class-name="op-cell" width="70" align="center">
            <template #default="s"><el-button link type="danger" @click="form.items.splice(s.$index, 1)">删除</el-button></template>
          </el-table-column>
        </el-table>
        <div class="muted" style="text-align: right; margin-top: 8px">合计：<span class="money">¥{{ formTotal }}</span></div>
      </el-form>
      <template #footer>
        <el-button type="primary" v-hasPermi="['garment:outbound:add']" @click="submitForm">确认出库</el-button>
        <el-button @click="open = false">取 消</el-button>
      </template>
    </el-dialog>

    <el-dialog title="出库明细" v-model="detailOpen" width="760px" append-to-body>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="出库单号">{{ detail.outboundNo }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ detail.customer }}</el-descriptions-item>
        <el-descriptions-item label="出库日期">{{ detail.outboundDate }}</el-descriptions-item>
        <el-descriptions-item label="总金额">¥{{ detail.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="作废原因" :span="2">{{ detail.cancelReason || '无' }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="detail.items || []" border size="small" style="margin-top: 12px">
        <el-table-column label="条码" prop="barcode" min-width="160" />
        <el-table-column label="款号" prop="styleNo" width="100" />
        <el-table-column label="颜色" prop="color" width="90" />
        <el-table-column label="尺码" prop="size" width="70" align="center" />
        <el-table-column label="数量" prop="qty" width="80" align="right" />
        <el-table-column label="单价" prop="price" width="90" align="right" />
        <el-table-column label="箱号" prop="boxNo" width="110" />
        <el-table-column label="金额" prop="amount" width="100" align="right" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup name="Outbound">
import { listOutbound, getOutbound, addOutbound, cancelOutbound, skuByBarcode, commonCustomers, commonSkuOptions } from '@/api/garment'
import { checkPermi } from '@/utils/permission'
import GarmentScan from '@/components/GarmentScan/index.vue'
import { useAutoScan } from '@/utils/autoScan'

const showSearch = ref(true)

const { proxy } = getCurrentInstance()
const outboundList = ref([])
const sumList = ref([])
const loading = ref(false)
const total = ref(0)
const open = ref(false)
const detailOpen = ref(false)
const detail = ref({})
const dateRange = ref([])
const form = ref({})
const customers = ref([])
commonCustomers().then(res => { customers.value = res.data || [] })
const scanBarcode = ref('')
const scanRef = ref()
const queryParams = ref({ pageNum: 1, pageSize: 10, outboundNo: undefined, customer: undefined, status: undefined })
const route = useRoute()

// 支持从首页卡片深链过来，按日期过滤出库单
watch(() => route.query, q => {
  dateRange.value = q.beginDate ? [q.beginDate, q.endDate || q.beginDate] : []
  queryParams.value.pageNum = 1
  getList()
}, { immediate: true })

// 手机相机扫商品标签二维码后打开本页：自动开单并加入该条码，草稿 2 小时内有效，可连续扫码累加
const DRAFT_KEY = 'garment:outbound-draft'
function loadDraft() {
  try {
    const d = JSON.parse(localStorage.getItem(DRAFT_KEY))
    if (d && d.time && Date.now() - d.time < 2 * 60 * 60 * 1000 && Array.isArray(d.form?.items)) return d.form
  } catch (e) { /* 忽略损坏草稿 */ }
  return null
}
function saveDraft() {
  localStorage.setItem(DRAFT_KEY, JSON.stringify({ time: Date.now(), form: form.value }))
}
function clearDraft() {
  localStorage.removeItem(DRAFT_KEY)
}
watch(() => route.query.barcode, code => {
  if (!code) return
  if (!checkPermi(['garment:outbound:add'])) { proxy.$modal.msgWarning('没有成品出库开单权限'); return }
  form.value = loadDraft() || { customer: '', outboundDate: today(), items: [] }
  scanBarcode.value = String(code)
  open.value = true
  setTimeout(async () => {
    const ok = await addByBarcode()
    if (ok) saveDraft()
    const q = { ...route.query }
    delete q.barcode
    proxy.$router.replace({ path: route.path, query: q })
  }, 250)
}, { immediate: true })

const formTotal = computed(() => (form.value.items || []).reduce((s, i) => s + Number(i.price || 0) * Number(i.qty || 0), 0).toFixed(2))

function today() {
  const d = new Date()
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}
function getList() {
  loading.value = true
  const params = { ...queryParams.value }
  if (dateRange.value && dateRange.value.length === 2) {
    params.params = { beginDate: dateRange.value[0], endDate: dateRange.value[1] }
  }
  listOutbound(params).then(res => {
    outboundList.value = res.rows
    total.value = res.total
    loading.value = false
    // 合计按全部筛选结果计算，不受分页影响
    listOutbound({ ...params, pageNum: 1, pageSize: 100000 }).then(r => { sumList.value = r.rows || [] }).catch(() => {})
  }).catch(() => { loading.value = false })
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); dateRange.value = []; handleQuery() }
function handleAdd() {
  form.value = { customer: '', outboundDate: today(), items: [] }
  scanBarcode.value = ''
  open.value = true
  // 扫码枪：弹窗打开后自动聚焦条码输入框
  setTimeout(focusScan, 200)
}
function addEmptyItem() {
  // 手动添加的行允许在行内扫码或直接下拉选商品，选中后回填款号/颜色/尺码/单价
  form.value.items.push({ barcode: '', styleNo: '', color: '', size: '', qty: 1, price: 0, _manual: true })
  loadSkuOptions()
}
// 手动添加行的商品下拉数据，按需加载一次
const skuOptions = ref([])
let skuOptionsLoaded = false
function loadSkuOptions() {
  if (skuOptionsLoaded) return
  skuOptionsLoaded = true
  commonSkuOptions().then(res => { skuOptions.value = res.data || [] }).catch(() => { skuOptionsLoaded = false })
}
function fillRow(row, sku) {
  row.skuId = sku.skuId
  row.barcode = sku.barcode
  row.styleNo = sku.styleNo
  row.color = sku.color
  row.size = sku.size
  if (!row.price) row.price = Number(sku.salePrice || 0)
}
// 手动行：输入或扫码枪录入条码后自动匹配商品
function resolveRowBarcode(row) {
  const code = (row.barcode || '').trim()
  if (!code) return
  skuByBarcode(code).then(res => {
    const sku = res.data
    if (!sku) {
      row.skuId = undefined
      row.styleNo = ''
      row.color = ''
      row.size = ''
      proxy.$modal.msgWarning(`条码不存在：${code}`)
      return
    }
    fillRow(row, sku)
  })
}
// 手动行：从下拉里选商品后回填
function onRowSkuChange(row, skuId) {
  const sku = skuOptions.value.find(k => k.skuId === skuId)
  if (sku) fillRow(row, sku)
}
// 扫码枪：添加成功后保持焦点，支持连续扫描
function focusScan() {
  nextTick(() => { if (scanRef.value) scanRef.value.focus() })
}
// 手机拍照扫码：识别条码后直接加入出库明细
function onScanBarcode(code) {
  scanBarcode.value = code
  addByBarcode().then(ok => { if (ok) saveDraft() })
}
function addByBarcode(silent = false) {
  if (!scanBarcode.value) return Promise.resolve(false)
  return skuByBarcode(scanBarcode.value.trim()).then(res => {
    const sku = res.data
    if (!sku) { if (!silent) proxy.$modal.msgWarning('条码不存在'); return false }
    const exist = form.value.items.find(i => i.skuId === sku.skuId)
    if (exist) {
      exist.qty += 1
    } else {
      form.value.items.push({
        skuId: sku.skuId, barcode: sku.barcode, styleNo: sku.styleNo,
        color: sku.color, size: sku.size, qty: 1, price: sku.salePrice || 0
      })
    }
    scanBarcode.value = ''
    focusScan()
    return true
  })
}
// 手动输入或扫码枪：输入停顿后自动添加明细，无需回车
const { onInput: onBarcodeInput, cancel: cancelBarcode } = useAutoScan(code => {
  scanBarcode.value = code
  addByBarcode(true)
}, { minLength: 6 })
function onBarcodeEnter(code) {
  cancelBarcode()
  scanBarcode.value = code
  addByBarcode()
}
function submitForm() {
  if (!form.value.items.length) { proxy.$modal.msgWarning('请录入出库明细'); return }
  const bad = form.value.items.findIndex(i => !i.skuId)
  if (bad >= 0) { proxy.$modal.msgWarning(`第 ${bad + 1} 行还未选定商品，请扫码或从下拉中选择`); return }
  // 去掉行内临时标记，避免提交多余字段
  const items = form.value.items.map(({ _manual, ...rest }) => rest)
  addOutbound({ ...form.value, items }).then(() => {
    proxy.$modal.msgSuccess('出库成功')
    clearDraft()
    open.value = false
    getList()
  })
}
function viewDetail(row) {
  getOutbound(row.outboundId).then(res => { detail.value = res.data; detailOpen.value = true })
}
function handleCancel(row) {
  proxy.$prompt('请输入作废原因', '作废出库单', { inputPlaceholder: '如：录错客户/数量' }).then(({ value }) => {
    return cancelOutbound(row.outboundId, value)
  }).then(() => {
    proxy.$modal.msgSuccess('已作废，库存已回滚')
    getList()
  }).catch(() => {})
}
function getSummary({ columns, data }) {
  const rows = sumList.value.length ? sumList.value : data
  return columns.map((col, i) => {
    if (i === 0) return '合计（不含作废）'
    if (col.property === 'totalQty') return rows.filter(r => r.status !== '1').reduce((s, r) => s + Number(r.totalQty || 0), 0)
    if (col.property === 'totalAmount') return rows.filter(r => r.status !== '1').reduce((s, r) => s + Number(r.totalAmount || 0), 0).toFixed(2)
    return ''
  })
}
function handleExport() {
  proxy.download('garment/outbound/export', { ...queryParams.value }, `outbound_${Date.now()}.xlsx`)
}

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
