<template>
  <div class="app-container garment-page">
    <el-form v-show="showSearch" class="search-form" :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item label="客户" prop="customer">
        <el-input v-model="queryParams.customer" placeholder="客户" clearable style="width: 130px" @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="款号" prop="styleNo">
        <el-input v-model="queryParams.styleNo" placeholder="款号" clearable style="width: 120px" @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="退货日期">
        <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD" start-placeholder="开始" end-placeholder="结束" style="width: 240px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" v-hasPermi="['garment:return:add']" @click="handleAdd">退货入库</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" v-hasPermi="['garment:return:remove']" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <div class="table-wrap">
      <el-table v-loading="loading" :data="returnList" border show-summary :summary-method="getSummary" @selection-change="handleSelectionChange">
            <el-table-column v-if="checkPermi(['garment:return:remove'])" type="selection" width="50" align="center" />
            
        <el-table-column label="退货单号" prop="returnNo" width="150" />
        <el-table-column label="客户" prop="customer" min-width="120" />
        <el-table-column label="款号" prop="styleNo" width="100" />
        <el-table-column label="颜色" prop="color" width="90" />
        <el-table-column label="尺码" prop="size" width="65" align="center" />
        <el-table-column label="退货数量" prop="qty" width="90" align="right" />
        <el-table-column label="好货" prop="goodQty" width="80" align="right" />
        <el-table-column label="次品" prop="badQty" width="80" align="right" />
        <el-table-column label="退货原因" prop="reason" min-width="140" show-overflow-tooltip />
        <el-table-column label="退货日期" prop="returnDate" width="110" />
        <el-table-column label="操作人" prop="operator" width="100" />
        <el-table-column v-if="checkPermi(['garment:return:remove'])" label="操作" class-name="op-cell" width="90" align="center" fixed="right">
          <template #default="s">
            <el-button link type="danger" v-hasPermi="['garment:return:remove']" @click="handleDelete(s.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="退货入库" v-model="open" width="600px" append-to-body>
      <el-form label-width="90px">
        <el-form-item label="商品条码" required>
          <el-input v-model="form.barcode" placeholder="扫描或输入条码后回车" @keyup.enter="fetchSku" />
        </el-form-item>
        <el-form-item label="商品信息">
          <span v-if="form.skuId">{{ form.styleNo }} · {{ form.color }}/{{ form.size }}</span>
          <span v-else class="muted">请先扫描条码</span>
        </el-form-item>
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="客户"><el-input v-model="form.customer" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="退货日期"><el-date-picker v-model="form.returnDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="退货数量">
              <el-input-number v-model="form.qty" :min="1" controls-position="right" style="width: 100%" @change="syncGood" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="次品数量">
              <el-input-number v-model="form.badQty" :min="0" :max="form.qty" controls-position="right" style="width: 100%" @change="syncGood" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="好货数量">
              <el-input-number v-model="form.goodQty" :min="0" controls-position="right" style="width: 100%" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="退货原因"><el-input v-model="form.reason" type="textarea" :rows="2" /></el-form-item>
        <el-alert type="info" :closable="false" show-icon title="仅好货数量入库到成品库，次品不入库，请单独走次品处理流程。" />
      </el-form>
      <template #footer>
        <el-button type="primary" v-hasPermi="['garment:return:add']" @click="submitForm">确认入库</el-button>
        <el-button @click="open = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ReturnGoods">
import { listReturn, addReturn, delReturn, skuByBarcode } from '@/api/garment'

const showSearch = ref(true)

const ids = ref([])
const multiple = ref(true)

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.returnId)
  multiple.value = !selection.length
}

const { proxy } = getCurrentInstance()
const returnList = ref([])
const sumRows = ref([])
const loading = ref(false)
const total = ref(0)
const open = ref(false)
const dateRange = ref([])
const form = ref({})
const queryParams = ref({ pageNum: 1, pageSize: 10, customer: undefined, styleNo: undefined })

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
  listReturn(params).then(res => {
    returnList.value = res.rows
    total.value = res.total
    loading.value = false
    // 合计按全部筛选结果计算，不受分页影响
    listReturn({ ...params, pageNum: 1, pageSize: 100000 }).then(r => { sumRows.value = r.rows || [] }).catch(() => {})
  }).catch(() => { loading.value = false })
}
function resetQuery() { proxy.resetForm('queryRef'); dateRange.value = []; getList() }
function handleAdd() {
  form.value = { barcode: '', skuId: null, customer: '', returnDate: today(), qty: 1, goodQty: 1, badQty: 0, reason: '' }
  open.value = true
}
function fetchSku() {
  if (!form.value.barcode) return
  skuByBarcode(form.value.barcode.trim()).then(res => {
    const sku = res.data
    if (!sku) { proxy.$modal.msgWarning('条码不存在'); return }
    form.value.skuId = sku.skuId
    form.value.styleNo = sku.styleNo
    form.value.color = sku.color
    form.value.size = sku.size
    proxy.$modal.msgSuccess('已读取商品信息')
  })
}
function syncGood() {
  form.value.badQty = Math.min(form.value.badQty || 0, form.value.qty || 0)
  form.value.goodQty = Math.max(0, (form.value.qty || 0) - (form.value.badQty || 0))
}
function submitForm() {
  if (!form.value.skuId) { proxy.$modal.msgWarning('请先扫描商品条码'); return }
  addReturn(form.value).then(() => {
    proxy.$modal.msgSuccess('退货入库成功')
    open.value = false
    getList()
  })
}
function handleDelete(row) {
  const _ids = row ? [row.returnId] : ids.value
  if (!_ids.length) return
  proxy.$modal.confirm(row ? '删除退货单将回滚已入库的好货数量，是否继续？' : `是否确认删除选中的 ${_ids.length} 条数据？`).then(() => delReturn(_ids)).then(() => {
    proxy.$modal.msgSuccess('删除成功')
    getList()
    ids.value = []
    multiple.value = true
  }).catch(() => {})
}
function getSummary({ columns, data }) {
  const rows = sumRows.value.length ? sumRows.value : data
  return columns.map(col => {
    if (col.property === 'returnNo') return '合计'
    if (['qty', 'goodQty', 'badQty'].includes(col.property)) {
      return rows.reduce((s, r) => s + Number(r[col.property] || 0), 0)
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

</script>
