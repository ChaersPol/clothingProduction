<template>
  <div class="app-container garment-page">
    <el-form v-show="showSearch" class="search-form" :inline="true">
      <el-form-item label="统计日期">
        <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD" start-placeholder="开始" end-placeholder="结束" style="width: 240px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="loadData">统计</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar v-model:showSearch="showSearch" @queryTable="loadData"></right-toolbar>
    </el-row>

    <div class="stat-grid">
      <div class="stat-card"><div class="stat-label">裁货总单数</div><div class="stat-value">{{ totalOrders }}</div></div>
      <div class="stat-card orange"><div class="stat-label">裁货总件数</div><div class="stat-value">{{ totalQty }}</div></div>
      <div class="stat-card blue"><div class="stat-label">裁工人数</div><div class="stat-value">{{ cutterList.length }}</div></div>
      <div class="stat-card green"><div class="stat-label">平均每单件数</div><div class="stat-value">{{ avgQty }}</div></div>
    </div>

    <el-row :gutter="14">
      <el-col :xs="24" :md="12">
        <el-card shadow="never" class="section-card">
          <template #header><span>裁工产量排行</span></template>
          <div class="table-wrap">
            <el-table :data="cutterList" border size="small">
              <el-table-column label="裁工" prop="cutter" min-width="110" />
              <el-table-column label="裁货单数" prop="orders" width="100" align="center" />
              <el-table-column label="裁货件数" prop="qty" width="110" align="right" />
              <el-table-column label="层数合计" prop="layers" width="100" align="right" />
            </el-table>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card shadow="never" class="section-card">
          <template #header><span>每日裁货产量</span></template>
          <div class="table-wrap">
            <el-table :data="dayList" border size="small">
              <el-table-column label="日期" prop="day" min-width="120" />
              <el-table-column label="裁货单数" prop="beds" width="100" align="center" />
              <el-table-column label="裁货件数" prop="qty" width="110" align="right" />
            </el-table>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="CutStat">
import { cutByCutter, cutByDay } from '@/api/garment'

const showSearch = ref(true)

const { proxy } = getCurrentInstance()
const cutterList = ref([])
const dayList = ref([])
const dateRange = ref([])

const totalOrders = computed(() => cutterList.value.reduce((s, i) => s + Number(i.orders || 0), 0))
const totalQty = computed(() => cutterList.value.reduce((s, i) => s + Number(i.qty || 0), 0))
const avgQty = computed(() => totalOrders.value ? (totalQty.value / totalOrders.value).toFixed(0) : 0)

function params() {
  const p = {}
  if (dateRange.value && dateRange.value.length === 2) {
    p.beginDate = dateRange.value[0]
    p.endDate = dateRange.value[1]
  }
  return p
}
function loadData() {
  cutByCutter(params()).then(res => { cutterList.value = res.data || [] })
  cutByDay(params()).then(res => { dayList.value = res.data || [] })
}
function resetQuery() { dateRange.value = []; loadData() }

loadData()
</script>
