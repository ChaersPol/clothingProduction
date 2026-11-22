<template>
  <div class="app-container garment-page">
    <el-form v-show="showSearch" class="search-form" :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item label="款号" prop="styleNo">
        <el-input v-model="queryParams.styleNo" placeholder="款号" clearable style="width: 140px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="颜色" prop="color">
        <el-input v-model="queryParams.color" placeholder="颜色" clearable style="width: 120px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="尺码" prop="size">
        <el-input v-model="queryParams.size" placeholder="尺码" clearable style="width: 100px" @keyup.enter="handleQuery" />
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
        <el-button type="primary" icon="Printer" v-hasPermi="['garment:label:print']" :disabled="!selection.length" @click="handlePrint">打印标签</el-button>
      </el-col>
      <el-col :span="3" style="display: flex; align-items: center; gap: 6px">
        <span class="muted">份数</span>
        <el-input-number v-model="copies" :min="1" size="small" controls-position="right" style="width: 100px" />
      </el-col>
      <el-col :span="3" style="display: flex; align-items: center; gap: 6px">
        <span class="muted">纸张</span>
        <el-select v-model="paperMode" size="small" style="width: 100px">
          <el-option label="标签纸" value="LABEL" />
          <el-option label="A4" value="A4" />
        </el-select>
      </el-col>
      <el-col :span="6" style="display: flex; align-items: center; gap: 4px">
        <span class="muted">尺寸</span>
        <el-input-number v-model="labelW" :min="20" :max="104" size="small" controls-position="right" style="width: 80px" />
        <span class="muted">×</span>
        <el-input-number v-model="labelH" :min="20" :max="150" size="small" controls-position="right" style="width: 80px" />
        <span class="muted">mm</span>
      </el-col>
      <el-col :span="4" style="display: flex; align-items: center">
        <el-radio-group v-model="labelType" size="small">
          <el-radio-button value="FACTORY">厂内标签</el-radio-button>
          <el-radio-button value="PRODUCT">成品标签</el-radio-button>
        </el-radio-group>
      </el-col>
      <el-col :span="4" style="display: flex; align-items: center">
        <span class="muted">已选 {{ selection.length }} 个 SKU，共 {{ selection.length * copies }} 张</span>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <div class="table-wrap">
      <el-table v-loading="loading" :data="skuList" border @selection-change="s => (selection = s)">
        <el-table-column type="selection" width="45" align="center" />
        <el-table-column label="款号" prop="styleNo" min-width="100" />
        <el-table-column label="品名" prop="cnName" min-width="140" show-overflow-tooltip />
        <el-table-column label="颜色" prop="color" width="100" />
        <el-table-column label="尺码" prop="size" width="70" align="center" />
        <el-table-column label="条码" prop="barcode" min-width="170" show-overflow-tooltip />
        <el-table-column label="销售价" prop="salePrice" width="90" align="right" />
        <el-table-column label="操作" class-name="op-cell" width="100" align="center">
          <template #default="s">
            <el-button link type="primary" v-hasPermi="['garment:label:print']" @click="printOne(s.row)">打印</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 打印区域：挂到 body 下，打印时隐藏其余内容并自动分页 -->
    <teleport to="body">
      <div id="label-print-area" :class="paperMode === 'LABEL' ? 'label-mode' : 'a4-mode'" :style="{ '--lw': labelW + 'mm', '--lh': labelH + 'mm' }">
      <div v-for="item in printItems" :key="item._key" class="label-card" :class="{ 'factory-card': labelType === 'FACTORY' }">
        <template v-if="labelType === 'FACTORY'">
          <div class="label-name factory-name">{{ item.cnName }}</div>
          <div class="label-attr factory-attr">{{ item.styleNo }}-{{ item.color }}-{{ item.size }}</div>
        </template>
        <template v-else>
          <div class="label-style">{{ item.styleNo }}</div>
          <div class="label-name">{{ item.cnName }}</div>
          <div class="label-attr">{{ item.color }} / {{ item.size }}</div>
          <div class="label-barcode">
            <svg :ref="el => setBarcodeRef(el, item.barcode)"></svg>
          </div>
          <div class="label-code">{{ item.barcode }}</div>
        </template>
      </div>
      </div>
    </teleport>
  </div>
</template>

<script setup name="Label">
import JsBarcode from 'jsbarcode'
import { labelList } from '@/api/garment'

const PRINT_KEY = 'garment:label-print-settings'
const savedPrint = (() => {
  try { return JSON.parse(localStorage.getItem(PRINT_KEY)) || {} } catch (e) { return {} }
})()
const showSearch = ref(true)
const labelType = ref(savedPrint.labelType || 'FACTORY')
const copies = ref(savedPrint.copies || 1)
const paperMode = ref(savedPrint.paperMode || 'LABEL')
const labelW = ref(savedPrint.labelW || 50)
const labelH = ref(savedPrint.labelH || 30)

// 打印前才注入 @page，避免离开页面后影响扎卡等其他打印
function applyPageStyle() {
  let el = document.getElementById('label-page-style')
  if (!el) {
    el = document.createElement('style')
    el.id = 'label-page-style'
    document.head.appendChild(el)
  }
  el.textContent = paperMode.value === 'LABEL'
    ? `@page { size: ${labelW.value}mm ${labelH.value}mm; margin: 0; }`
    : '@page { size: A4; margin: 8mm; }'
}
function clearPageStyle() {
  document.getElementById('label-page-style')?.remove()
}
window.addEventListener('afterprint', clearPageStyle)
watch([paperMode, labelW, labelH, copies, labelType], () => {
  localStorage.setItem(PRINT_KEY, JSON.stringify({
    paperMode: paperMode.value,
    labelW: labelW.value,
    labelH: labelH.value,
    copies: copies.value,
    labelType: labelType.value
  }))
})
onUnmounted(() => {
  window.removeEventListener('afterprint', clearPageStyle)
  clearPageStyle()
})

const { proxy } = getCurrentInstance()
const skuList = ref([])
const selection = ref([])
const printItems = ref([])
const loading = ref(false)
const total = ref(0)
const queryParams = ref({ pageNum: 1, pageSize: 20, styleNo: undefined, color: undefined, size: undefined, barcode: undefined })
const barcodeRefs = []

function getList() {
  loading.value = true
  labelList(queryParams.value).then(res => {
    skuList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
function setBarcodeRef(el, code) {
  if (el) barcodeRefs.push({ el, code })
}
function renderBarcodes(done) {
  nextTick(() => {
    barcodeRefs.forEach(({ el, code }) => {
      try {
        JsBarcode(el, code, { format: 'CODE128', width: 1.2, height: Math.max(20, Math.min(42, Math.round(labelH.value * 1.1))), displayValue: false, margin: 0 })
      } catch (e) { /* 忽略非法条码 */ }
    })
    barcodeRefs.length = 0
    if (done) done()
  })
}
function expandCopies(rows) {
  const n = Math.max(1, Number(copies.value) || 1)
  return rows.flatMap(row => Array.from({ length: n }, (_, i) => ({ ...row, _key: `${row.skuId}-${i}` })))
}
function handlePrint() {
  applyPageStyle()
  printItems.value = expandCopies(selection.value)
  renderBarcodes(() => setTimeout(() => window.print(), 60))
}
function printOne(row) {
  applyPageStyle()
  printItems.value = expandCopies([row])
  renderBarcodes(() => setTimeout(() => window.print(), 60))
}

getList()

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

<style scoped>
#label-print-area {
  display: none;
}
@media print {
  #label-print-area {
    display: block;
  }
  .label-card {
    display: inline-block;
    vertical-align: top;
    width: var(--lw, 50mm);
    height: var(--lh, 30mm);
    padding: 1.5mm;
    margin: 0;
    box-sizing: border-box;
    text-align: center;
    page-break-inside: avoid;
    break-inside: avoid;
  }
  /* 标签纸模式：一张标签一页，适配佳博 GP-1324D */
  #label-print-area.label-mode .label-card {
    display: block;
    border: none;
    height: calc(var(--lh, 30mm) - 0.3mm);
    page-break-after: always;
    break-after: page;
  }
  #label-print-area.label-mode .label-card:last-child {
    page-break-after: auto;
    break-after: auto;
  }
  /* A4 模式：多张自动换行、自动分页 */
  #label-print-area.a4-mode .label-card {
    margin: 0 2mm 2mm 0;
    border: 1px dashed #999;
  }
  .label-card .label-style { font-size: 11px; font-weight: bold; }
  .label-card .label-name { font-size: 9px; white-space: nowrap; overflow: hidden; }
  .label-card .label-attr { font-size: 9px; }
  .label-card .label-code { font-size: 7px; letter-spacing: 0.5px; }
  .label-card svg { width: 100%; height: 8mm; }
  /* 厂内标签：内容垂直居中并撑满标签 */
  .label-card.factory-card {
    height: var(--lh, 30mm);
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
  .label-card.factory-card .factory-name {
    font-size: 12px;
    font-weight: 700;
    margin-bottom: 1.5mm;
    white-space: nowrap;
  }
  .label-card.factory-card .factory-attr {
    font-size: 10px;
    letter-spacing: 0.3px;
  }
  /* 标签纸模式：撑满整页且不加边框，避免热敏打印浪费 */
  #label-print-area.label-mode .label-card,
  #label-print-area.label-mode .label-card.factory-card {
    height: calc(var(--lh, 30mm) - 0.3mm);
    border: none;
  }
  /* 厂内标签保持 flex，内容在整张标签内垂直居中 */
  #label-print-area.label-mode .label-card.factory-card {
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
}
</style>
