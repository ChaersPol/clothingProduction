<template>
  <div class="app-container garment-page">
    <div class="stat-grid">
      <div class="stat-card red">
        <div class="stat-label">裁片库存（红）</div>
        <div class="stat-value">{{ summary.CUT }}</div>
        <div class="stat-sub">未领用裁片</div>
      </div>
      <div class="stat-card blue">
        <div class="stat-label">产线库存（蓝）</div>
        <div class="stat-value">{{ summary.LINE }}</div>
        <div class="stat-sub">车间在制</div>
      </div>
      <div class="stat-card black">
        <div class="stat-label">成品库存（黑）</div>
        <div class="stat-value">{{ summary.FINISH }}</div>
        <div class="stat-sub">可出库成品</div>
      </div>
      <div class="stat-card green">
        <div class="stat-label">库存总货值</div>
        <div class="stat-value">¥{{ summary.value }}</div>
        <div class="stat-sub">按成本价核算</div>
      </div>
    </div>

    <el-form v-show="showSearch" class="search-form" :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item label="库存类型">
        <el-radio-group v-model="queryParams.stockType" @change="handleQuery">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="CUT">裁片</el-radio-button>
          <el-radio-button value="LINE">产线</el-radio-button>
          <el-radio-button value="FINISH">成品</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="款号" prop="styleNo">
        <el-input v-model="queryParams.styleNo" placeholder="款号" clearable style="width: 120px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="品名" prop="cnName">
        <el-input v-model="queryParams.cnName" placeholder="品名" clearable style="width: 130px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="条码" prop="barcode">
        <el-input v-model="queryParams.barcode" placeholder="条码" clearable style="width: 160px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button plain icon="Download" @click="handleExport">导出库存</el-button>
      </el-col>
      <el-col :span="1.5" v-if="canAccess('/warehouse/warning')">
        <el-button plain type="warning" @click="$router.push('/warehouse/warning')">库存预警</el-button>
      </el-col>
      <el-col :span="6" style="display: flex; align-items: center; gap: 6px">
        <el-tag type="danger">裁片</el-tag>
        <el-tag type="primary">产线</el-tag>
        <el-tag type="info" effect="dark">成品</el-tag>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <div class="table-wrap">
      <el-table v-loading="loading" :data="inventoryList" border show-summary :summary-method="getSummary">
        <el-table-column label="款号" prop="styleNo" width="100" />
        <el-table-column label="品名" prop="cnName" min-width="140" show-overflow-tooltip />
        <el-table-column label="颜色" prop="color" width="100" />
        <el-table-column label="尺码" prop="size" width="65" align="center" />
        <el-table-column label="条码" prop="barcode" min-width="160" show-overflow-tooltip />
        <el-table-column label="库存类型" prop="stockType" width="90" align="center">
          <template #default="s">
            <el-tag size="small" :type="typeTag(s.row.stockType)">{{ typeText(s.row.stockType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="数量" prop="qty" width="80" align="right" />
        <el-table-column label="成本价" prop="costPrice" width="90" align="right" />
        <el-table-column label="货值" prop="stockValue" width="100" align="right" />
        <el-table-column label="库龄(天)" prop="ageDays" width="100" align="center">
          <template #default="s">
            <span :class="{ 'money': (s.row.ageDays || 0) > 90 }">{{ s.row.ageDays || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="更新时间" prop="updateTime" width="170" />
      </el-table>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup name="Inventory">
import { listInventory } from '@/api/garment'

const showSearch = ref(true)

const { proxy } = getCurrentInstance()
const router = useRouter()
// 当前账号是否已注册该路由（未授权菜单不在动态路由中，避免点击进入 404）
function canAccess(path) {
  const resolved = router.resolve({ path })
  return !!resolved.name && !String(resolved.name).startsWith('404')
}
const inventoryList = ref([])
const loading = ref(false)
const total = ref(0)
const summary = ref({ CUT: 0, LINE: 0, FINISH: 0, value: '0.00' })
const queryParams = ref({ pageNum: 1, pageSize: 10, stockType: '', styleNo: undefined, cnName: undefined, barcode: undefined })
const route = useRoute()

// 支持从消息提醒深链过来，直接定位到指定 SKU
watch(() => route.query, q => {
  queryParams.value.stockType = q.stockType || ''
  queryParams.value.cnName = undefined
  queryParams.value.styleNo = q.styleNo || undefined
  queryParams.value.barcode = q.barcode || undefined
  queryParams.value.pageNum = 1
  getList()
}, { immediate: true })

function typeText(t) { return t === 'CUT' ? '裁片' : t === 'LINE' ? '产线' : '成品' }
function typeTag(t) { return t === 'CUT' ? 'danger' : t === 'LINE' ? 'primary' : 'info' }
function getList() {
  loading.value = true
  listInventory(queryParams.value).then(res => {
    inventoryList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}
function loadSummary() {
  listInventory({ pageNum: 1, pageSize: 1000 }).then(res => {
    const rows = res.rows || []
    const result = { CUT: 0, LINE: 0, FINISH: 0, value: '0.00' }
    let value = 0
    rows.forEach(i => {
      result[i.stockType] = (result[i.stockType] || 0) + Number(i.qty || 0)
      value += Number(i.stockValue || 0)
    })
    result.value = value.toFixed(2)
    summary.value = result
  })
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() {
  proxy.resetForm('queryRef')
  queryParams.value.stockType = ''
  queryParams.value.styleNo = undefined
  queryParams.value.barcode = undefined
  handleQuery()
}
function getSummary({ columns, data }) {
  return columns.map((col, i) => {
    if (i === 0) return '合计'
    if (col.property === 'qty') return data.reduce((s, r) => s + Number(r.qty || 0), 0)
    if (col.property === 'stockValue') return data.reduce((s, r) => s + Number(r.stockValue || 0), 0).toFixed(2)
    return ''
  })
}
function handleExport() {
  proxy.download('garment/inventory/export', { ...queryParams.value }, `inventory_${Date.now()}.xlsx`)
}

loadSummary()
</script>
