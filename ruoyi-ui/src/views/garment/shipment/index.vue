<template>
  <div class="app-container garment-page">
    <el-form v-show="showSearch" class="search-form" :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item label="客户" prop="customer">
        <el-input v-model="queryParams.customer" placeholder="客户" clearable style="width: 140px" @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="款号" prop="styleNo">
        <el-input v-model="queryParams.styleNo" placeholder="款号" clearable style="width: 120px" @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="出货日期">
        <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD" start-placeholder="开始" end-placeholder="结束" style="width: 240px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" v-hasPermi="['garment:shipment:add']" @click="handleAdd">新增出货</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" v-hasPermi="['garment:shipment:remove']" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Download" v-hasPermi="['garment:shipment:export']" @click="handleExport">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <div class="stat-grid">
      <div class="stat-card blue"><div class="stat-label">出货数量</div><div class="stat-value">{{ summary.qty || 0 }}</div></div>
      <div class="stat-card red"><div class="stat-label">出货金额</div><div class="stat-value">¥{{ summary.amount || 0 }}</div></div>
      <div class="stat-card orange"><div class="stat-label">海运费</div><div class="stat-value">¥{{ summary.seaFreight || 0 }}</div></div>
      <div class="stat-card"><div class="stat-label">出货单数</div><div class="stat-value">{{ summary.times || 0 }}</div></div>
    </div>



    <div class="table-wrap">
      <el-table v-loading="loading" :data="shipmentList" border show-summary :summary-method="getSummary" @selection-change="handleSelectionChange">
            <el-table-column v-if="checkPermi(['garment:shipment:remove'])" type="selection" width="50" align="center" />
            
        <el-table-column label="出货日期" prop="shipDate" width="110" />
        <el-table-column label="客户" prop="customer" min-width="130" show-overflow-tooltip />
        <el-table-column label="款号" prop="styleNo" width="100" />
        <el-table-column label="颜色" prop="color" width="90" />
        <el-table-column label="尺码" prop="size" width="65" align="center" />
        <el-table-column label="数量" prop="qty" width="80" align="right" />
        <el-table-column label="单价" prop="price" width="80" align="right" />
        <el-table-column label="金额" prop="amount" width="100" align="right" />
        <el-table-column label="海运费" prop="seaFreight" width="95" align="right" />
        <el-table-column label="自动出库单" prop="outboundNo" width="160">
          <template #default="s">
            <el-tag v-if="s.row.outboundNo" type="success" size="small">{{ s.row.outboundNo }}</el-tag>
            <el-tag v-else type="info" size="small">未出库</el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="checkPermi(['garment:shipment:edit','garment:shipment:remove'])" label="操作" class-name="op-cell" width="130" align="center" fixed="right">
          <template #default="s">
            <el-button link type="primary" v-hasPermi="['garment:shipment:edit']" @click="handleUpdate(s.row)">修改</el-button>
            <el-button link type="danger" v-hasPermi="['garment:shipment:remove']" @click="handleDelete(s.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="form.shipId ? '修改出货' : '新增出货'" v-model="open" width="620px" append-to-body>
      <el-alert type="info" :closable="false" show-icon style="margin-bottom: 12px"
        title="保存后将按 款号 + 颜色 + 尺码 自动匹配商品，生成成品出库单并扣减成品库存；修改或删除会自动回滚库存。" />
      <el-form ref="shipRef" :model="form" label-width="90px">
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="出货日期"><el-date-picker v-model="form.shipDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="客户">
              <el-select v-model="form.customer" placeholder="选择客户" filterable clearable style="width: 100%">
                <el-option v-for="c in customers" :key="c.customerId" :label="c.customerName" :value="c.customerName" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="款号">
              <el-select v-model="form.styleNo" placeholder="选择款号" filterable style="width: 100%" @change="onStyleChange">
                <el-option v-for="s in styles" :key="s.styleNo" :label="s.styleNo + ' ' + (s.cnName || '')" :value="s.styleNo" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="颜色">
              <el-select v-model="form.color" placeholder="选择颜色" filterable style="width: 100%" @change="onColorChange">
                <el-option v-for="c in styleColors" :key="c" :label="c" :value="c" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="尺码">
              <el-select v-model="form.size" placeholder="选择尺码" filterable style="width: 100%">
                <el-option v-for="sz in styleSizes" :key="sz" :label="sz" :value="sz" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="数量"><el-input-number v-model="form.qty" :min="0" controls-position="right" style="width: 100%" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="单价"><el-input-number v-model="form.price" :min="0" :precision="2" controls-position="right" style="width: 100%" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="海运费"><el-input-number v-model="form.seaFreight" :min="0" :precision="2" controls-position="right" style="width: 100%" /></el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button type="primary" v-hasPermi="['garment:shipment:add','garment:shipment:edit']" @click="submitForm">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Shipment">
import { listShipment, addShipment, updateShipment, delShipment, shipmentSummary, listSku, commonCustomers, commonStyles } from '@/api/garment'

const showSearch = ref(true)

const ids = ref([])
const multiple = ref(true)

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.shipId)
  multiple.value = !selection.length
}

const { proxy } = getCurrentInstance()
const shipmentList = ref([])
const sumRows = ref([])
const summary = ref({})
const loading = ref(false)
const total = ref(0)
const open = ref(false)
const dateRange = ref([])
const form = ref({})
const customers = ref([])
const styles = ref([])
const allSkus = ref([])
// 款号 → 颜色 → 尺码 全部来自已生成的商品，避免手填导致匹配不到库存
const styleColors = computed(() => [...new Set(allSkus.value.filter(k => k.styleNo === form.value.styleNo).map(k => k.color))])
const styleSizes = computed(() => [...new Set(allSkus.value
  .filter(k => k.styleNo === form.value.styleNo && (!form.value.color || k.color === form.value.color))
  .map(k => k.size))])
function onStyleChange() { form.value.color = ''; form.value.size = '' }
function onColorChange() { form.value.size = '' }
const queryParams = ref({ pageNum: 1, pageSize: 10, customer: undefined, styleNo: undefined })

function params() {
  const p = { ...queryParams.value }
  if (dateRange.value && dateRange.value.length === 2) {
    p.params = { beginDate: dateRange.value[0], endDate: dateRange.value[1] }
  }
  return p
}
function getList() {
  loading.value = true
  listShipment(params()).then(res => {
    shipmentList.value = res.rows
    total.value = res.total
    loading.value = false
    // 合计按全部筛选结果计算，不受分页影响
    listShipment({ ...params(), pageNum: 1, pageSize: 100000 }).then(r => { sumRows.value = r.rows || [] }).catch(() => {})
  }).catch(() => { loading.value = false })
  shipmentSummary(params()).then(res => { summary.value = res.data || {} })
}
function resetQuery() { proxy.resetForm('queryRef'); dateRange.value = []; getList() }
function handleExport() {
  const params = { ...queryParams.value }
  if (dateRange.value && dateRange.value.length === 2) {
    params.params = { beginDate: dateRange.value[0], endDate: dateRange.value[1] }
  }
  proxy.download('garment/shipment/export', params, `shipment_${Date.now()}.xlsx`)
}
function today() {
  const d = new Date()
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}
function handleAdd() {
  form.value = { shipDate: today(), customer: '', styleNo: '', color: '', size: '', qty: 0, price: 0, seaFreight: 0 }
  open.value = true
}
function handleUpdate(row) { form.value = { ...row }; open.value = true }
function submitForm() {
  const fn = form.value.shipId ? updateShipment : addShipment
  fn(form.value).then(() => {
    proxy.$modal.msgSuccess('保存成功')
    open.value = false
    getList()
  })
}
function handleDelete(row) {
  const _ids = row ? [row.shipId] : ids.value
  if (!_ids.length) return
  proxy.$modal.confirm(row ? '确认删除该出货记录？' : `是否确认删除选中的 ${_ids.length} 条数据？`).then(() => delShipment(_ids)).then(() => {
    proxy.$modal.msgSuccess('删除成功')
    getList()
    ids.value = []
    multiple.value = true
  }).catch(() => {})
}
function getSummary({ columns, data }) {
  const rows = sumRows.value.length ? sumRows.value : data
  return columns.map(col => {
    if (col.property === 'shipDate') return '合计'
    if (['qty', 'amount', 'seaFreight'].includes(col.property)) {
      const v = rows.reduce((s, r) => s + Number(r[col.property] || 0), 0)
      return col.property === 'qty' ? String(v) : v.toFixed(2)
    }
    return ''
  })
}

getList()

// 从标签页切回本页时自动刷新数据，避免展示缓存中的旧列表
let _pageActivated = false
onActivated(() => {
  if (_pageActivated) {
    getList()
  } else {
    _pageActivated = true
  }
})

commonCustomers().then(res => { customers.value = res.data || [] })
commonStyles().then(res => { styles.value = res.data || [] })
listSku({ pageNum: 1, pageSize: 5000 }).then(res => { allSkus.value = res.rows || [] })

</script>
