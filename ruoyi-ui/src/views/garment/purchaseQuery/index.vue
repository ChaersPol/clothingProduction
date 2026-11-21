<template>
  <div class="app-container garment-page">
    <el-form v-show="showSearch" class="search-form" :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item label="采购单号" prop="purchaseNo">
        <el-input v-model="queryParams.purchaseNo" placeholder="单号" clearable style="width: 150px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="物料名称" prop="materialName">
        <el-input v-model="queryParams.materialName" placeholder="物料名称" clearable style="width: 150px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="类型" prop="purchaseType">
        <el-select v-model="queryParams.purchaseType" placeholder="全部" clearable style="width: 130px">
          <el-option label="公用耗材" value="COMMON" />
          <el-option label="按款物料" value="MATERIAL" />
        </el-select>
      </el-form-item>
      <el-form-item label="日期">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          value-format="YYYY-MM-DD"
          start-placeholder="开始"
          end-placeholder="结束"
          style="width: 240px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Download" @click="handleExport">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <div class="stat-grid">
      <div class="stat-card"><div class="stat-label">当前页采购金额</div><div class="stat-value">¥{{ pageAmount }}</div></div>
      <div class="stat-card green"><div class="stat-label">当前页已入库数量</div><div class="stat-value">{{ pageReceived }}</div></div>
    </div>

    <div class="table-wrap">
      <el-table v-loading="loading" :data="itemList" border show-summary :summary-method="getSummary">
        <el-table-column label="采购单号" prop="purchase_no" width="150" />
        <el-table-column label="类型" prop="purchase_type" width="100" align="center">
          <template #default="s">{{ s.row.purchase_type === 'MATERIAL' ? '按款物料' : '公用耗材' }}</template>
        </el-table-column>
        <el-table-column label="供应商" prop="supplier_name" min-width="150" show-overflow-tooltip />
        <el-table-column label="物料类型" prop="material_type" width="95" />
        <el-table-column label="名称" prop="material_name" min-width="150" show-overflow-tooltip />
        <el-table-column label="规格" prop="spec" width="110" />
        <el-table-column label="单位" prop="unit" width="70" align="center" />
        <el-table-column label="单价" prop="price" width="90" align="right" />
        <el-table-column label="数量" prop="qty" width="90" align="right" />
        <el-table-column label="已入库" prop="received_qty" width="90" align="right" />
        <el-table-column label="金额" prop="amount" width="100" align="right" />
      </el-table>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup name="PurchaseQuery">
import { listPurchaseItems } from '@/api/garment'

const showSearch = ref(true)

const { proxy } = getCurrentInstance()
const itemList = ref([])
const loading = ref(false)
const total = ref(0)
const dateRange = ref([])
const queryParams = ref({ pageNum: 1, pageSize: 20, purchaseNo: undefined, materialName: undefined, purchaseType: undefined })

const pageAmount = computed(() => itemList.value.reduce((s, i) => s + Number(i.amount || 0), 0).toFixed(2))
const pageReceived = computed(() => itemList.value.reduce((s, i) => s + Number(i.received_qty || 0), 0))

function getList() {
  loading.value = true
  const params = { ...queryParams.value }
  if (dateRange.value && dateRange.value.length === 2) {
    params.params = { beginDate: dateRange.value[0], endDate: dateRange.value[1] }
  }
  listPurchaseItems(params).then(res => {
    itemList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function handleExport() {
  const params = { ...queryParams.value }
  if (dateRange.value && dateRange.value.length === 2) {
    params.params = { beginDate: dateRange.value[0], endDate: dateRange.value[1] }
  }
  proxy.download('garment/purchase/items/export', params, `purchase_items_${Date.now()}.xlsx`)
}
function resetQuery() {
  proxy.resetForm('queryRef')
  dateRange.value = []
  handleQuery()
}
function getSummary({ columns, data }) {
  return columns.map((col, i) => {
    if (i === 0) return '合计'
    if (col.property === 'amount') return data.reduce((s, r) => s + Number(r.amount || 0), 0).toFixed(2)
    if (col.property === 'qty') return data.reduce((s, r) => s + Number(r.qty || 0), 0).toFixed(2)
    if (col.property === 'received_qty') return data.reduce((s, r) => s + Number(r.received_qty || 0), 0).toFixed(2)
    return ''
  })
}

getList()
</script>
