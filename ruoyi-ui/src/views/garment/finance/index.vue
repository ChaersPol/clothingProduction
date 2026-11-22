<template>
  <div class="app-container garment-page">
    <el-form v-show="showSearch" class="search-form" :inline="true">
      <el-form-item label="统计日期">
        <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD" start-placeholder="开始" end-placeholder="结束" style="width: 240px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="loadAll">统计</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar v-model:showSearch="showSearch" @queryTable="loadAll"></right-toolbar>
    </el-row>

    <div class="stat-grid">
      <div class="stat-card blue"><div class="stat-label">销售额</div><div class="stat-value">¥{{ totals.revenue }}</div></div>
      <div class="stat-card orange"><div class="stat-label">销售成本</div><div class="stat-value">¥{{ totals.cost }}</div></div>
      <div class="stat-card red"><div class="stat-label">费用支出</div><div class="stat-value">¥{{ totals.expense }}</div></div>
      <div class="stat-card green"><div class="stat-label">利润</div><div class="stat-value">¥{{ totals.profit }}</div></div>
      <div class="stat-card black"><div class="stat-label">库存货值</div><div class="stat-value">¥{{ totals.stockValue }}</div></div>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="出库排行" name="rank">
        <el-row :gutter="14">
          <el-col :xs="24" :md="12">
            <el-card shadow="never">
              <template #header><span>SKU 出库排行</span></template>
              <div class="table-wrap">
                <el-table :data="rankSkuList" border size="small" max-height="420">
                  <el-table-column label="款号" prop="style_no" width="100" />
                  <el-table-column label="颜色/尺码" width="105">
                    <template #default="s">{{ s.row.color }}/{{ s.row.size }}</template>
                  </el-table-column>
                  <el-table-column label="条码" prop="barcode" min-width="150" show-overflow-tooltip />
                  <el-table-column label="出库数" prop="qty" width="80" align="right" />
                  <el-table-column label="金额" prop="amount" width="95" align="right" />
                </el-table>
              </div>
            </el-card>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-card shadow="never">
              <template #header><span>款式（父体）出库排行</span></template>
              <div class="table-wrap">
                <el-table :data="rankStyleList" border size="small" max-height="420">
                  <el-table-column label="款号" prop="style_no" width="110" />
                  <el-table-column label="品名" prop="cn_name" min-width="150" show-overflow-tooltip />
                  <el-table-column label="出库数" prop="qty" width="90" align="right" />
                  <el-table-column label="金额" prop="amount" width="100" align="right" />
                </el-table>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <el-tab-pane label="库存货值" name="inventory">
        <div class="table-wrap">
          <el-table :data="inventoryList" border size="small" show-summary :summary-method="sumInventory">
            <el-table-column label="款号" prop="style_no" width="110" />
            <el-table-column label="品名" prop="cn_name" min-width="150" show-overflow-tooltip />
            <el-table-column label="颜色" prop="color" width="100" />
            <el-table-column label="尺码" prop="size" width="65" align="center" />
            <el-table-column label="库存类型" prop="stockType" width="95" align="center">
              <template #default="s">{{ s.row.stockType === 'CUT' ? '裁片' : s.row.stockType === 'LINE' ? '产线' : '成品' }}</template>
            </el-table-column>
            <el-table-column label="数量" prop="qty" width="80" align="right" />
            <el-table-column label="成本价" prop="costPrice" width="90" align="right" />
            <el-table-column label="货值" prop="amount" width="100" align="right" />
            <el-table-column label="库龄(天)" prop="ageDays" width="100" align="center" />
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="利润分析" name="profit">
        <div class="table-wrap">
          <el-table :data="profitList" border size="small" show-summary :summary-method="sumProfit">
            <el-table-column label="款号" prop="style_no" width="110" />
            <el-table-column label="品名" prop="cn_name" min-width="160" show-overflow-tooltip />
            <el-table-column label="销售额" prop="revenue" width="110" align="right" />
            <el-table-column label="成本" prop="cost" width="110" align="right" />
            <el-table-column label="费用" prop="expense" width="110" align="right" />
            <el-table-column label="利润" prop="profit" width="110" align="right">
              <template #default="s"><span class="money">{{ s.row.profit }}</span></template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="退货统计" name="return">
        <el-row :gutter="14">
          <el-col :xs="24" :md="13">
            <el-card shadow="never">
              <template #header><span>按款退货</span></template>
              <div class="table-wrap">
                <el-table :data="returnByStyle" border size="small">
                  <el-table-column label="款号" prop="style_no" width="110" />
                  <el-table-column label="品名" prop="cn_name" min-width="140" show-overflow-tooltip />
                  <el-table-column label="退货数" prop="qty" width="80" align="right" />
                  <el-table-column label="好货" prop="goodQty" width="75" align="right" />
                  <el-table-column label="次品" prop="badQty" width="75" align="right" />
                  <el-table-column label="次数" prop="times" width="65" align="center" />
                </el-table>
              </div>
            </el-card>
          </el-col>
          <el-col :xs="24" :md="11">
            <el-card shadow="never">
              <template #header><span>按原因退货</span></template>
              <div class="table-wrap">
                <el-table :data="returnByReason" border size="small">
                  <el-table-column label="退货原因" prop="reason" min-width="150" show-overflow-tooltip />
                  <el-table-column label="退货数" prop="qty" width="90" align="right" />
                  <el-table-column label="次数" prop="times" width="80" align="center" />
                </el-table>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup name="Finance">
import { rankSku, rankStyle, financeInventory, profitReport, returnReport } from '@/api/garment'

const showSearch = ref(true)

const { proxy } = getCurrentInstance()
const activeTab = ref('rank')
const dateRange = ref([])
const rankSkuList = ref([])
const rankStyleList = ref([])
const inventoryList = ref([])
const profitList = ref([])
const returnByStyle = ref([])
const returnByReason = ref([])
const totals = ref({ revenue: '0.00', cost: '0.00', expense: '0.00', profit: '0.00', stockValue: '0.00' })

function params() {
  const p = {}
  if (dateRange.value && dateRange.value.length === 2) {
    p.beginDate = dateRange.value[0]
    p.endDate = dateRange.value[1]
  }
  return p
}
function sumBy(list, key) {
  return list.reduce((s, i) => s + Number(i[key] || 0), 0)
}
function loadAll() {
  const p = params()
  rankSku(p).then(res => { rankSkuList.value = res.data || [] })
  rankStyle(p).then(res => { rankStyleList.value = res.data || [] })
  financeInventory({}).then(res => {
    inventoryList.value = res.data || []
    totals.value.stockValue = sumBy(inventoryList.value, 'amount').toFixed(2)
  })
  profitReport(p).then(res => {
    profitList.value = res.data || []
    totals.value.revenue = sumBy(profitList.value, 'revenue').toFixed(2)
    totals.value.cost = sumBy(profitList.value, 'cost').toFixed(2)
    totals.value.expense = sumBy(profitList.value, 'expense').toFixed(2)
    totals.value.profit = sumBy(profitList.value, 'profit').toFixed(2)
  })
  returnReport(p).then(res => {
    const d = res.data || {}
    returnByStyle.value = d.byStyle || []
    returnByReason.value = d.byReason || []
  })
}
function resetQuery() { dateRange.value = []; loadAll() }
function sumInventory({ columns, data }) {
  return columns.map((col, i) => {
    if (i === 0) return '合计'
    if (col.property === 'qty') return sumBy(data, 'qty')
    if (col.property === 'amount') return sumBy(data, 'amount').toFixed(2)
    return ''
  })
}
function sumProfit({ columns, data }) {
  return columns.map((col, i) => {
    if (i === 0) return '合计'
    if (['revenue', 'cost', 'expense', 'profit'].includes(col.property)) return sumBy(data, col.property).toFixed(2)
    return ''
  })
}

loadAll()

// 从标签页切回本页时自动刷新数据，避免展示缓存中的旧列表
let _pageActivated = false
onActivated(() => {
  if (_pageActivated) {
    loadAll()
  } else {
    _pageActivated = true
  }
})

</script>
