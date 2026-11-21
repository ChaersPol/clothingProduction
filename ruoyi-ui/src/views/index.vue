<template>
  <div class="app-container garment-page dashboard">
    <div class="stat-grid">
      <div class="stat-card">
        <div class="stat-label">款号总数</div>
        <div class="stat-value">{{ data.styleCount || 0 }}</div>
        <div class="stat-sub">在售 SKU {{ data.skuCount || 0 }} 个</div>
      </div>
      <div class="stat-card orange">
        <div class="stat-label">今日裁货</div>
        <div class="stat-value">{{ data.todayCutQty || 0 }}</div>
        <div class="stat-sub">件</div>
      </div>
      <div class="stat-card green">
        <div class="stat-label">今日成品入库</div>
        <div class="stat-value">{{ data.todayInQty || 0 }}</div>
        <div class="stat-sub">件</div>
      </div>
      <div class="stat-card red">
        <div class="stat-label">今日出库</div>
        <div class="stat-value">{{ data.todayOutQty || 0 }}</div>
        <div class="stat-sub">金额 ¥{{ data.todayOutAmount || 0 }}</div>
      </div>
      <div class="stat-card blue">
        <div class="stat-label">产线在制扎数</div>
        <div class="stat-value">{{ data.onlineBundles || 0 }}</div>
        <div class="stat-sub">裁片在库 {{ data.cutStockBundles || 0 }} 扎</div>
      </div>
      <div class="stat-card red">
        <div class="stat-label">产线超期</div>
        <div class="stat-value">{{ data.overdueBundles || 0 }}</div>
        <div class="stat-sub">停留超过 3 天</div>
      </div>
    </div>

    <el-row :gutter="14">
      <el-col :xs="24" :md="10">
        <el-card class="section-card" shadow="never">
          <template #header>
            <div class="card-head">
              <span>三色库存概览</span>
              <div class="color-legend">
                <span class="legend-item"><i class="dot cut"></i>裁片</span>
                <span class="legend-item"><i class="dot line"></i>产线</span>
                <span class="legend-item"><i class="dot finish"></i>成品</span>
              </div>
            </div>
          </template>
          <div ref="pieRef" class="chart chart-pie"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="14">
        <el-card class="section-card" shadow="never">
          <template #header><span>近 7 天出库趋势</span></template>
          <div ref="barRef" class="chart chart-bar"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="14">
      <el-col :xs="24" :md="14">
        <el-card class="section-card" shadow="never">
          <template #header>
            <div class="card-head">
              <span>库存预警</span>
              <el-button link type="primary" @click="$router.push('/warehouse/warning')">查看全部</el-button>
            </div>
          </template>
          <div class="table-wrap">
            <el-table :data="data.warnings || []" size="small" max-height="320">
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
      <el-col :xs="24" :md="10">
        <el-card class="section-card" shadow="never">
          <template #header>
            <div class="card-head">
              <span>消息提醒</span>
              <el-button link type="primary" @click="$router.push('/msg')">更多</el-button>
            </div>
          </template>
          <div class="msg-list">
            <div v-for="(m, i) in messages" :key="i" class="msg-item">
              <el-tag :type="m.type === 'warning' ? 'danger' : m.type === 'task' ? 'warning' : 'info'" size="small">
                {{ m.type === 'warning' ? '预警' : m.type === 'task' ? '待办' : '提示' }}
              </el-tag>
              <div class="msg-body">
                <div class="msg-title">{{ m.title }}</div>
                <div class="msg-content">{{ m.content }}</div>
              </div>
            </div>
            <el-empty v-if="!messages.length" description="暂无消息" :image-size="60" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Index">
import echarts from '@/utils/echarts'
import { getDashboard, listMessages } from '@/api/garment'

const data = ref({})
const messages = ref([])
const pieRef = ref()
const barRef = ref()
let pieChart = null
let barChart = null

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
</script>

<style scoped lang="scss">
.dashboard {
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
