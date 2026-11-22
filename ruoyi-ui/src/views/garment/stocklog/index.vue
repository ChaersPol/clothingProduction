<template>
  <div class="app-container garment-page">
    <el-form v-show="showSearch" class="search-form" :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item label="款号" prop="styleNo">
        <el-input v-model="queryParams.styleNo" placeholder="款号" clearable style="width: 120px" @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="库存类型" prop="stockType">
        <el-select v-model="queryParams.stockType" placeholder="全部" clearable style="width: 110px">
          <el-option label="裁片" value="CUT" />
          <el-option label="产线" value="LINE" />
          <el-option label="成品" value="FINISH" />
        </el-select>
      </el-form-item>
      <el-form-item label="业务类型" prop="bizType">
        <el-select v-model="queryParams.bizType" placeholder="全部" clearable filterable style="width: 140px">
          <el-option v-for="t in bizTypes" :key="t" :label="t" :value="t" />
        </el-select>
      </el-form-item>
      <el-form-item label="扎号" prop="bundleNo">
        <el-input v-model="queryParams.bundleNo" placeholder="扎号" clearable style="width: 120px" @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="操作时间">
        <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD" start-placeholder="开始" end-placeholder="结束" style="width: 240px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button plain icon="Download" @click="handleExport">导出流水</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <div class="table-wrap">
      <el-table v-loading="loading" :data="logList" border>
        <el-table-column label="操作时间" prop="operateTime" width="170" />
        <el-table-column label="款号" prop="styleNo" width="100" />
        <el-table-column label="颜色/尺码" width="100">
          <template #default="s">{{ s.row.color }}/{{ s.row.size }}</template>
        </el-table-column>
        <el-table-column label="库存类型" prop="stockType" width="90" align="center">
          <template #default="s">
            <el-tag size="small" :type="s.row.stockType === 'CUT' ? 'danger' : s.row.stockType === 'LINE' ? 'primary' : 'info'">
              {{ s.row.stockType === 'CUT' ? '裁片' : s.row.stockType === 'LINE' ? '产线' : '成品' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="变动" prop="changeQty" width="80" align="right">
          <template #default="s">
            <span :class="s.row.changeQty > 0 ? 'tag-blue' : 'tag-red'">{{ s.row.changeQty > 0 ? '+' : '' }}{{ s.row.changeQty }}</span>
          </template>
        </el-table-column>
        <el-table-column label="变动前" prop="beforeQty" width="80" align="right" />
        <el-table-column label="变动后" prop="afterQty" width="80" align="right" />
        <el-table-column label="业务类型" prop="bizType" width="110" />
        <el-table-column label="业务单号" prop="bizNo" width="140" />
        <el-table-column label="扎号" prop="bundleNo" width="150" />
        <el-table-column label="操作人" prop="operator" width="100" />
        <el-table-column label="备注" prop="remark" min-width="140" show-overflow-tooltip />
      </el-table>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup name="StockLog">
import { listStockLogs } from '@/api/garment'

const showSearch = ref(true)

const { proxy } = getCurrentInstance()
const bizTypes = ['初始库存', '裁片入库', '车间领片', '补片', '报废', '包装出产线', '成品入库', '成品出库', '出库作废', '退货入库', '盘点调整']
const logList = ref([])
const loading = ref(false)
const total = ref(0)
const dateRange = ref([])
const queryParams = ref({ pageNum: 1, pageSize: 20, styleNo: undefined, stockType: undefined, bizType: undefined, bundleNo: undefined })
const route = useRoute()

// 支持从首页卡片深链过来，按业务类型和日期过滤流水
watch(() => route.query, q => {
  queryParams.value.bizType = q.bizType || undefined
  dateRange.value = q.beginDate ? [q.beginDate, q.endDate || q.beginDate] : []
  queryParams.value.pageNum = 1
  getList()
}, { immediate: true })

function getList() {
  loading.value = true
  const params = { ...queryParams.value }
  if (dateRange.value && dateRange.value.length === 2) {
    params.params = { beginDate: dateRange.value[0], endDate: dateRange.value[1] }
  }
  listStockLogs(params).then(res => {
    logList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}
function resetQuery() { proxy.resetForm('queryRef'); dateRange.value = []; getList() }
function handleExport() {
  proxy.download('garment/stocklog/export', { ...queryParams.value }, `stocklog_${Date.now()}.xlsx`)
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
