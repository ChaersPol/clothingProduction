<template>
  <div class="app-container garment-page dashboard">
    <div class="stat-grid">
      <div v-if="canAccess('/goods/style')" class="stat-card clickable" @click="go('/goods/style')">
        <div class="stat-label">款号总数</div>
        <div class="stat-value">{{ data.styleCount || 0 }}</div>
        <div class="stat-sub">在售 SKU {{ data.skuCount || 0 }} 个</div>
      </div>
      <div v-if="canAccess('/cut/cutorder')" class="stat-card orange clickable" @click="goToday('/cut/cutorder')">
        <div class="stat-label">今日裁货</div>
        <div class="stat-value">{{ data.todayCutQty || 0 }}</div>
        <div class="stat-sub">件</div>
      </div>
      <div v-if="canAccess('/warehouse/stocklog')" class="stat-card green clickable" @click="goFinishIn">
        <div class="stat-label">今日成品入库</div>
        <div class="stat-value">{{ data.todayInQty || 0 }}</div>
        <div class="stat-sub">件</div>
      </div>
      <div v-if="canAccess('/warehouse/outbound')" class="stat-card red clickable" @click="goToday('/warehouse/outbound')">
        <div class="stat-label">今日出库</div>
        <div class="stat-value">{{ data.todayOutQty || 0 }}</div>
        <div class="stat-sub">金额 ¥{{ data.todayOutAmount || 0 }}</div>
      </div>
      <div v-if="canAccess('/workshop/linestock')" class="stat-card blue clickable" @click="go('/workshop/linestock')">
        <div class="stat-label">产线在制扎数</div>
        <div class="stat-value">{{ data.onlineBundles || 0 }}</div>
        <div class="stat-sub">裁片在库 {{ data.cutStockBundles || 0 }} 扎</div>
      </div>
      <div v-if="canAccess('/workshop/linestock')" class="stat-card red clickable" @click="go('/workshop/linestock', { overdue: 1 })">
        <div class="stat-label">产线超期</div>
        <div class="stat-value">{{ data.overdueBundles || 0 }}</div>
        <div class="stat-sub">停留超过 3 天</div>
      </div>
    </div>

    <el-row :gutter="14">
      <el-col v-if="canAccess('/warehouse/inventory')" :xs="24" :md="10">
        <el-card class="section-card" shadow="never">
          <template #header>
            <div class="card-head">
              <span>三色库存概览</span>
              <div class="color-legend">
                <span class="legend-item" @click="goInventory('CUT')"><i class="dot cut"></i>裁片</span>
                <span class="legend-item" @click="goInventory('LINE')"><i class="dot line"></i>产线</span>
                <span class="legend-item" @click="goInventory('FINISH')"><i class="dot finish"></i>成品</span>
              </div>
            </div>
          </template>
          <div ref="pieRef" class="chart chart-pie"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="canAccess('/warehouse/inventory') ? 14 : 24">
        <el-card class="section-card" shadow="never">
          <template #header><span>近 7 天出库趋势</span></template>
          <div ref="barRef" class="chart chart-bar"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="14">
      <el-col v-if="canAccess('/warehouse/warning')" :xs="24" :md="canAccess('/msg') ? 14 : 24">
        <el-card class="section-card" shadow="never">
          <template #header>
            <div class="card-head">
              <span>库存预警</span>
              <el-button link type="primary" @click="go('/warehouse/warning')">查看全部</el-button>
            </div>
          </template>
          <div class="table-wrap">
            <el-table :data="data.warnings || []" size="small" max-height="320" row-class-name="clickable-row" @row-click="goWarning">
              <el-table-column label="款号" prop="style_no" min-width="110" show-overflow-tooltip />
              <el-table-column label="颜色/尺码" min-width="110" show-overflow-tooltip>
                <template #default="s">{{ s.row.color }} / {{ s.row.size }}</template>
              </el-table-column>
              <el-table-column label="成品库存" prop="finish_qty" width="90" align="center" />
              <el-table-column label="最低库存" prop="min_stock" width="90" align="center" />
              <el-table-column label="差额" width="80" align="center">
                <template #default="s"><span class="money">{{ (s.row.min_stock || 0) - (s.row.finish_qty || 0) }}</span></template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>
      <el-col v-if="canAccess('/msg')" :xs="24" :md="canAccess('/warehouse/warning') ? 10 : 24">
        <el-card class="section-card" shadow="never">
          <template #header>
            <div class="card-head">
              <span>消息提醒</span>
              <el-button link type="primary" @click="go('/msg')">更多</el-button>
            </div>
          </template>
          <div class="msg-list">
            <div v-for="(m, i) in visibleMessages" :key="i" class="msg-item" :class="{ clickable: !!m.page }" @click="goMessage(m)">
              <el-tag :type="m.type === 'warning' ? 'danger' : m.type === 'task' ? 'warning' : 'info'" size="small">
                {{ m.type === 'warning' ? '预警' : m.type === 'task' ? '待办' : '提示' }}
              </el-tag>
              <div class="msg-body">
                <div class="msg-title">{{ m.title }}</div>
                <div class="msg-content">{{ m.content }}</div>
              </div>
            </div>
            <el-empty v-if="!visibleMessages.length" description="暂无消息" :image-size="60" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Index">
import echarts from '@/utils/echarts'
import { ElMessage } from 'element-plus'
import { getDashboard, listMessages } from '@/api/garment'

const data = ref({})
const messages = ref([])
const pieRef = ref()
const barRef = ref()
let pieChart = null
let barChart = null
const router = useRouter()

// 当前账号是否已注册该路由（未授权菜单不在动态路由中，只会命中 404 兜底路由）
function canAccess(path) {
  const resolved = router.resolve({ path })
  return !!resolved.name && !String(resolved.name).startsWith("404")
}
function go(path, query) {
  if (!canAccess(path)) {
    ElMessage.warning("当前账号没有该模块的访问权限，请联系管理员")
    return
  }
  router.push({ path, query })
}
// 今日类卡片：跳转时按当天日期过滤，保证列表合计与卡片数值一致
function todayStr() {
  const d = new Date()
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}
function goToday(path) {
  const day = todayStr()
  go(path, { beginDate: day, endDate: day })
}
// 今日成品入库取自库存流水，直接定位到当天的「成品入库」流水
function goFinishIn() {
  const day = todayStr()
  go('/warehouse/stocklog', { bizType: '成品入库', beginDate: day, endDate: day })
}
function goInventory(stockType) {
  go('/warehouse/inventory', { stockType })
}
function goWarning(row) {
  const query = {}
  if (row.style_no) query.styleNo = row.style_no
  if (row.barcode) query.barcode = row.barcode
  go('/warehouse/warning', query)
}
function goMessage(m) {
  if (!m.page) return
  go(m.page, m.query || {})
}

// 消息提醒：当前账号没有对应模块权限的条目直接不展示，避免误导
const visibleMessages = computed(() => messages.value.filter(m => !m.page || canAccess(m.page)))

function loadData() {
  getDashboard().then(res => {
    data.value = res.data || {}
    renderCharts()
  })
  listMessages().then(res => {
    messages.value = res.data || []
  })
}

function renderCharts() {
  const inv = data.value.inventory || []
  const getQty = type => {
    const row = inv.find(i => i.stockType === type)
    return row ? Number(row.qty) : 0
  }
  if (pieRef.value) {
    pieChart = pieChart || echarts.init(pieRef.value)
    pieChart.off('click')
    pieChart.on('click', p => {
      const map = { 裁片: 'CUT', 产线: 'LINE', 成品: 'FINISH' }
      goInventory(map[p.name] || '')
    })
    pieChart.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c} 件 ({d}%)' },
      series: [{
        type: 'pie',
        radius: ['48%', '72%'],
        center: ['50%', '50%'],
        avoidLabelOverlap: true,
        itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
        label: { formatter: '{b}\n{c}' },
        data: [
          { name: '裁片', value: getQty('CUT'), itemStyle: { color: '#f56c6c' } },
          { name: '产线', value: getQty('LINE'), itemStyle: { color: '#409eff' } },
          { name: '成品', value: getQty('FINISH'), itemStyle: { color: '#303133' } }
        ]
      }]
    })
  }
  const trend = data.value.trend || []
  if (barRef.value) {
    barChart = barChart || echarts.init(barRef.value)
    barChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 40, right: 16, top: 30, bottom: 28 },
      xAxis: { type: 'category', data: trend.map(t => t.day), axisLine: { lineStyle: { color: '#dcdfe6' } } },
      yAxis: { type: 'value', splitLine: { lineStyle: { color: '#f0f0f0' } } },
      series: [{
        name: '出库件数', type: 'bar', barWidth: '45%',
        data: trend.map(t => t.qty),
        itemStyle: { color: '#409eff', borderRadius: [4, 4, 0, 0] }
      }]
    })
  }
}

function resize() {
  pieChart && pieChart.resize()
  barChart && barChart.resize()
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', resize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resize)
  pieChart && pieChart.dispose()
  barChart && barChart.dispose()
})

// 从其他标签页切回首页时刷新统计与消息，避免展示缓存中的旧数据
let _pageActivated = false
onActivated(() => {
  if (_pageActivated) {
    loadData()
  } else {
    _pageActivated = true
  }
})
</script>

<style scoped lang="scss">
.dashboard {
  .stat-card.clickable {
    cursor: pointer;
    transition: box-shadow 0.2s, transform 0.2s;
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
    }
  }
  .legend-item {
    cursor: pointer;
  }
  :deep(.el-table .clickable-row) {
    cursor: pointer;
  }
  .card-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 10px;
    flex-wrap: wrap;
  }
  .chart {
    width: 100%;
    height: 260px;
  }
  .chart-bar {
    height: 260px;
  }
  .msg-list {
    max-height: 320px;
    overflow-y: auto;
  }
  .msg-item {
    display: flex;
    gap: 8px;
    padding: 10px 0;
    border-bottom: 1px dashed #ebeef5;
    &:last-child { border-bottom: none; }
    &.clickable {
      cursor: pointer;
      padding-left: 6px;
      padding-right: 6px;
      border-radius: 4px;
      &:hover { background: #f5f7fa; }
    }
  }
  .msg-title {
    font-size: 13px;
    font-weight: 600;
    color: #303133;
  }
  .msg-content {
    font-size: 12px;
    color: #909399;
    margin-top: 2px;
    line-height: 1.5;
  }
}
@media (max-width: 768px) {
  .chart, .chart-bar { height: 210px; }
}
</style>
