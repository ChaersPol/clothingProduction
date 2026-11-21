<template>
  <div class="app-container garment-page">
    <el-card class="section-card" shadow="never">
      <template #header><span>扫码领片</span></template>
      <el-form class="scan-bar" :inline="true" @submit.prevent>
        <el-form-item label="员工" required>
          <el-select v-model="employee" placeholder="选择领片员工" filterable style="width: 180px">
            <el-option v-for="e in employees" :key="e.userName" :label="e.nickName || e.userName" :value="e.nickName || e.userName" />
          </el-select>
        </el-form-item>
        <el-form-item label="扎卡条码" class="scan-input">
          <el-input v-model="scanCode" placeholder="扫描扎卡条码后回车领片（裁片 → 产线）" clearable @keyup.enter="doPick" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="doPick">确认领片</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-form v-show="showSearch" class="search-form" :inline="true">
      <el-form-item label="员工" prop="employee">
        <el-input v-model="queryParams.employee" placeholder="员工" clearable style="width: 130px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="扎号" prop="bundleNo">
        <el-input v-model="queryParams.bundleNo" placeholder="扎号" clearable style="width: 120px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="领片日期">
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

    <div class="table-wrap">
      <el-table v-loading="loading" :data="pickList" border>
        <el-table-column label="领片时间" prop="pickTime" width="170" />
        <el-table-column label="扎号" prop="bundleNo" width="150" />
        <el-table-column label="款号" prop="styleNo" width="100" />
        <el-table-column label="颜色" prop="color" width="100" />
        <el-table-column label="尺码" prop="size" width="70" align="center" />
        <el-table-column label="数量" prop="qty" width="80" align="right" />
        <el-table-column label="员工" prop="employee" min-width="110" />
      </el-table>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup name="Pick">
import { pickBundle, listPicks, commonEmployees } from '@/api/garment'

const showSearch = ref(true)

const { proxy } = getCurrentInstance()
const employees = ref([])
const employee = ref('')
const scanCode = ref('')
const pickList = ref([])
const loading = ref(false)
const total = ref(0)
const dateRange = ref([])
const queryParams = ref({ pageNum: 1, pageSize: 10, employee: undefined, bundleNo: undefined })

function getList() {
  loading.value = true
  const params = { ...queryParams.value }
  if (dateRange.value && dateRange.value.length === 2) {
    params.params = { beginDate: dateRange.value[0], endDate: dateRange.value[1] }
  }
  listPicks(params).then(res => {
    pickList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { queryParams.value.employee = undefined; queryParams.value.bundleNo = undefined; dateRange.value = []; handleQuery() }
function doPick() {
  if (!employee.value) { proxy.$modal.msgWarning('请先选择员工'); return }
  if (!scanCode.value) { proxy.$modal.msgWarning('请扫描扎卡条码'); return }
  pickBundle({ code: scanCode.value.trim(), employee: employee.value }).then(res => {
    const b = res.data || {}
    proxy.$modal.msgSuccess(`领片成功：${b.bundleNo || ''} ${b.styleNo || ''} ${b.color || ''}/${b.size || ''} ${b.qty || ''}件`)
    scanCode.value = ''
    getList()
  })
}

getList()
commonEmployees().then(res => {
  employees.value = res.data || []
  if (employees.value.length) employee.value = employees.value[0].nickName || employees.value[0].userName
})
</script>
