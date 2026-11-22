<template>
  <div class="app-container garment-page">
    <el-form v-show="showSearch" class="search-form" :inline="true">
      <el-form-item label="员工" prop="employee">
        <el-select v-model="queryParams.employee" placeholder="员工" clearable filterable style="width: 130px">
          <el-option v-for="e in employees" :key="e.employeeId" :label="e.name" :value="e.name" />
        </el-select>
      </el-form-item>
      <el-form-item label="扎号" prop="bundleNo">
        <el-input v-model="queryParams.bundleNo" placeholder="扎号" clearable style="width: 120px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="交后道日期">
        <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD" start-placeholder="开始" end-placeholder="结束" style="width: 240px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-row :gutter="14">
      <el-col :xs="24" :md="15">
        <el-card shadow="never" class="section-card">
          <template #header><span>交后道记录</span></template>
          <div class="table-wrap">
            <el-table v-loading="loading" :data="deliveryList" border size="small">
              <el-table-column label="交后道时间" prop="deliveryTime" width="170" />
              <el-table-column label="扎号" prop="bundleNo" width="150" />
              <el-table-column label="款号" prop="styleNo" width="95" />
              <el-table-column label="颜色/尺码" min-width="100">
                <template #default="s">{{ s.row.color }}/{{ s.row.size }}</template>
              </el-table-column>
              <el-table-column label="完工数" prop="qty" width="75" align="right" />
              <el-table-column label="次品" prop="defectQty" width="65" align="right" />
              <el-table-column label="员工" prop="employee" min-width="100" />
            </el-table>
          </div>
          <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
        </el-card>
      </el-col>
      <el-col :xs="24" :md="9">
        <el-card shadow="never" class="section-card">
          <template #header>
            <div class="toolbar" style="margin: 0">
              <span>员工产量排行</span>
              <el-date-picker v-model="statDate" type="date" value-format="YYYY-MM-DD" size="small" style="width: 140px" @change="loadStat" />
            </div>
          </template>
          <div class="table-wrap">
            <el-table :data="statList" border size="small">
              <el-table-column label="员工" prop="employee" min-width="100" />
              <el-table-column label="完工件数" prop="qty" width="90" align="right" />
              <el-table-column label="次品数" prop="defectQty" width="80" align="right" />
              <el-table-column label="交付次数" prop="times" width="90" align="center" />
            </el-table>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Delivery">
import { listDeliveries, deliveryStat, commonEmployees } from '@/api/garment'

const showSearch = ref(true)

const { proxy } = getCurrentInstance()
const deliveryList = ref([])
const statList = ref([])
const employees = ref([])
const loading = ref(false)
const total = ref(0)
const dateRange = ref([])
const statDate = ref(new Date().toISOString().slice(0, 10))
const queryParams = ref({ pageNum: 1, pageSize: 10, employee: undefined, bundleNo: undefined })

function getList() {
  loading.value = true
  const params = { ...queryParams.value }
  if (dateRange.value && dateRange.value.length === 2) {
    params.params = { beginDate: dateRange.value[0], endDate: dateRange.value[1] }
  }
  listDeliveries(params).then(res => {
    deliveryList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}
function loadStat() {
  deliveryStat(statDate.value).then(res => { statList.value = res.data || [] })
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { queryParams.value.employee = undefined; queryParams.value.bundleNo = undefined; dateRange.value = []; handleQuery() }

getList()
loadStat()
commonEmployees().then(res => { employees.value = res.data || [] })

// 从标签页切回本页时自动刷新数据，避免展示缓存中的旧列表
let _pageActivated = false
onActivated(() => {
  if (_pageActivated) {
    getList()
    loadStat()
  } else {
    _pageActivated = true
  }
})

</script>
