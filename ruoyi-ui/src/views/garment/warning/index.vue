<template>
  <div class="app-container garment-page">
    <div class="stat-grid">
      <div class="stat-card red"><div class="stat-label">预警 SKU 数</div><div class="stat-value">{{ total }}</div></div>
      <div class="stat-card orange"><div class="stat-label">建议裁货总件数</div><div class="stat-value">{{ suggestTotal }}</div></div>
      <div class="stat-card blue"><div class="stat-label">缺货差额合计</div><div class="stat-value">{{ gapTotal }}</div></div>
    </div>

    <el-form v-show="showSearch" class="search-form" :inline="true">
      <el-form-item label="款号" prop="styleNo">
        <el-input v-model="styleNo" placeholder="款号" clearable style="width: 140px" @keyup.enter="loadWarnings" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="loadWarnings">查询预警</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar v-model:showSearch="showSearch" @queryTable="loadWarnings"></right-toolbar>
    </el-row>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="库存预警" name="warning">
        <div class="table-wrap">
          <el-table v-loading="loading" :data="warningList" border>
            <el-table-column label="款号" prop="style_no" width="110" />
            <el-table-column label="品名" prop="cn_name" min-width="150" show-overflow-tooltip />
            <el-table-column label="颜色" prop="color" width="100" />
            <el-table-column label="尺码" prop="size" width="65" align="center" />
            <el-table-column label="条码" prop="barcode" min-width="160" show-overflow-tooltip />
            <el-table-column label="成品库存" prop="finish_qty" width="90" align="right">
              <template #default="s"><strong>{{ s.row.finish_qty }}</strong></template>
            </el-table-column>
            <el-table-column label="产线" prop="line_qty" width="70" align="right" />
            <el-table-column label="裁片" prop="cut_qty" width="70" align="right" />
            <el-table-column label="最低库存" prop="min_stock" width="90" align="right" />
            <el-table-column label="缺货差额" width="90" align="right">
              <template #default="s"><span class="money">{{ (s.row.min_stock || 0) - (s.row.finish_qty || 0) }}</span></template>
            </el-table-column>
            <el-table-column v-if="checkPermi(['garment:cutorder:add','garment:warning:edit'])" label="操作" class-name="op-cell" width="170" align="center" fixed="right">
              <template #default="s">
                <el-button link type="primary" v-hasPermi="['garment:cutorder:add']" @click="goReplenish(s.row)">去补货</el-button>
                <el-button link v-hasPermi="['garment:warning:edit']" @click="openMinStock(s.row)">改最低库存</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <pagination v-show="total > 0" :total="total" v-model:page="pageNum" v-model:limit="pageSize" @pagination="getList" />
      </el-tab-pane>

      <el-tab-pane label="裁货建议" name="suggest">
        <div class="muted" style="margin-bottom: 8px">建议裁货量 = 最低库存 × 2 − 成品 − 产线 − 裁片</div>
        <div class="table-wrap">
          <el-table :data="suggestList" border show-summary :summary-method="suggestSummary">
            <el-table-column label="款号" prop="style_no" width="110" />
            <el-table-column label="品名" prop="cn_name" min-width="150" show-overflow-tooltip />
            <el-table-column label="颜色" prop="color" width="100" />
            <el-table-column label="尺码" prop="size" width="65" align="center" />
            <el-table-column label="成品" prop="finish_qty" width="80" align="right" />
            <el-table-column label="产线" prop="line_qty" width="80" align="right" />
            <el-table-column label="裁片" prop="cut_qty" width="80" align="right" />
            <el-table-column label="建议裁货" prop="suggestQty" width="100" align="right">
              <template #default="s"><span class="money">{{ s.row.suggestQty }}</span></template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog title="修改最低库存" v-model="open" width="420px" append-to-body>
      <el-form label-width="100px">
        <el-form-item label="SKU">{{ form.styleNo }} {{ form.color }}/{{ form.size }}</el-form-item>
        <el-form-item label="最低库存">
          <el-input-number v-model="form.minStock" :min="0" controls-position="right" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" v-hasPermi="['garment:warning:edit']" @click="submitMinStock">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Warning">
import { listWarnings, cutSuggestion, updateMinStock } from '@/api/garment'

const showSearch = ref(true)

const { proxy } = getCurrentInstance()
const router = useRouter()
const activeTab = ref('warning')
const warningList = ref([])
const suggestList = ref([])
const loading = ref(false)
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const styleNo = ref('')
const barcode = ref('')
const route = useRoute()
const open = ref(false)
const form = ref({})

// 支持从消息提醒深链过来，直接定位到指定 SKU
watch(() => route.query, q => {
  styleNo.value = q.styleNo || ''
  barcode.value = q.barcode || ''
  activeTab.value = 'warning'
  pageNum.value = 1
  loadWarnings()
}, { immediate: true })

const suggestTotal = computed(() => suggestList.value.reduce((s, i) => s + Number(i.suggestQty || 0), 0))
const gapTotal = computed(() => warningList.value.reduce((s, i) => s + Math.max(0, Number(i.min_stock || 0) - Number(i.finish_qty || 0)), 0))

function getList() {
  loading.value = true
  listWarnings({ pageNum: pageNum.value, pageSize: pageSize.value, styleNo: styleNo.value }).then(res => {
    const rows = res.rows || []
    warningList.value = barcode.value ? rows.filter(r => r.barcode === barcode.value) : rows
    total.value = barcode.value ? warningList.value.length : res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}
function loadSuggest() {
  cutSuggestion(styleNo.value).then(res => { suggestList.value = res.data || [] })
}
function loadWarnings() {
  pageNum.value = 1
  getList()
  loadSuggest()
}
function resetQuery() { styleNo.value = ''; barcode.value = ''; loadWarnings() }
// 去补货：按缺货差额打开裁货开单，预填款号/颜色/尺码/数量
// 未授权菜单不在动态路由中，跳转前校验，避免进入 404
function canAccess(path) {
  const resolved = router.resolve({ path })
  return !!resolved.name && !String(resolved.name).startsWith('404')
}
function goReplenish(row) {
  if (!canAccess('/cut/cutorder')) {
    proxy.$modal.msgWarning('当前账号没有「裁货开单」的访问权限，请联系管理员补货')
    return
  }
  const shortage = Number(row.min_stock || 0) - Number(row.finish_qty || 0)
  router.push({
    path: '/cut/cutorder',
    query: {
      action: 'add',
      styleNo: row.style_no,
      color: row.color,
      size: row.size,
      qty: shortage > 0 ? shortage : 1
    }
  })
}
function openMinStock(row) {
  form.value = { skuId: row.sku_id, styleNo: row.style_no, color: row.color, size: row.size, minStock: row.min_stock }
  open.value = true
}
function submitMinStock() {
  updateMinStock({ skuId: form.value.skuId, minStock: form.value.minStock }).then(() => {
    proxy.$modal.msgSuccess('已更新最低库存')
    open.value = false
    loadWarnings()
  })
}
function suggestSummary({ columns, data }) {
  return columns.map((col, i) => {
    if (i === 0) return '合计'
    if (col.property === 'suggestQty') return data.reduce((s, r) => s + Number(r.suggestQty || 0), 0)
    return ''
  })
}

// 从标签页切回本页时自动刷新数据，避免展示缓存中的旧列表
let _pageActivated = false
onActivated(() => {
  if (_pageActivated) {
    loadWarnings()
  } else {
    _pageActivated = true
  }
})

</script>
