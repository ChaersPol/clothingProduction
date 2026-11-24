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
      <el-col :span="4" style="display: flex; align-items: center; gap: 6px">
        <span class="muted">纸张</span>
        <el-select v-model="sizeKey" size="small" style="width: 120px">
          <el-option v-for="p in SIZE_PRESETS" :key="p.key" :label="p.label" :value="p.key" />
        </el-select>
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
      <div id="label-print-area" class="label-mode" :style="{ '--lw': labelW + 'mm', '--lh': labelH + 'mm' }">
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
            <canvas class="label-qr" :ref="el => setQrRef(el, outboundUrl(item.barcode))"></canvas>
          </div>
        </template>
      </div>
      </div>
    </teleport>
  </div>
</template>

<script setup name="Label">
import QRCode from 'qrcode'
import { labelList } from '@/api/garment'

// key 带版本号：旧版本残留的其他规格会在 50×30 纸卷上被判成两页，升级后重置为默认 50×30
const PRINT_KEY = 'garment:label-print-settings-v2'
const savedPrint = (() => {
  try { return JSON.parse(localStorage.getItem(PRINT_KEY)) || {} } catch (e) { return {} }
})()
const showSearch = ref(true)
const labelType = ref(savedPrint.labelType || 'FACTORY')
const copies = ref(savedPrint.copies || 1)
// 客户规格按「宽×高」书写：60×40 即宽 60mm、高 40mm（横向）
const SIZE_PRESETS = [
  { key: '50x30', label: '50×30 mm', w: 50, h: 30 },
  { key: '60x40', label: '60×40 mm', w: 60, h: 40 },
  { key: '100x100', label: '100×100 mm', w: 100, h: 100 }
]
const sizeKey = ref(SIZE_PRESETS.some(p => p.key === savedPrint.sizeKey) ? savedPrint.sizeKey : '50x30')
const paper = computed(() => SIZE_PRESETS.find(p => p.key === sizeKey.value) || SIZE_PRESETS[0])
const labelW = computed(() => paper.value.w)
const labelH = computed(() => paper.value.h)

// 打印前才注入 @page，避免离开页面后影响扎卡等其他打印
function applyPageStyle() {
  // 清掉另一个打印模块遗留的纸张设置，避免两套 @page 互相覆盖
  document.getElementById('bundle-page-style')?.remove()
  let el = document.getElementById('label-page-style')
  if (!el) {
    el = document.createElement('style')
    el.id = 'label-page-style'
    document.head.appendChild(el)
  }
  el.textContent = `@page { size: ${labelW.value}mm ${labelH.value}mm; margin: 0; }`
}
function clearPageStyle() {
  document.getElementById('label-page-style')?.remove()
}
// 打印结束后清空打印缓存，避免与扎卡打印内容混在一起
function resetPrintCache() {
  printItems.value = []
  qrRefs.length = 0
}
function handleAfterPrint() {
  resetPrintCache()
  clearPageStyle()
}
window.addEventListener('afterprint', handleAfterPrint)
watch([sizeKey, copies, labelType], () => {
  localStorage.setItem(PRINT_KEY, JSON.stringify({
    sizeKey: sizeKey.value,
    copies: copies.value,
    labelType: labelType.value
  }))
})
onUnmounted(() => {
  window.removeEventListener('afterprint', handleAfterPrint)
  resetPrintCache()
  clearPageStyle()
})
// 切到其他模块时清空本页打印缓存，避免残留卡片混入其他模块的打印
onDeactivated(() => {
  resetPrintCache()
  clearPageStyle()
})

const { proxy } = getCurrentInstance()
const skuList = ref([])
const selection = ref([])
const printItems = ref([])
const loading = ref(false)
const total = ref(0)
const queryParams = ref({ pageNum: 1, pageSize: 20, styleNo: undefined, color: undefined, size: undefined, barcode: undefined })
const qrRefs = []

// 手机系统相机扫标签二维码后直接打开成品出库页并带入条码，无需 HTTPS 摄像头权限
function outboundUrl(code) {
  return `${window.location.origin}/warehouse/outbound?barcode=${encodeURIComponent(code)}`
}

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
function setQrRef(el, url) {
  if (el) qrRefs.push({ el, url })
}
function renderQr(done) {
  nextTick(() => {
    const qrTasks = qrRefs.map(({ el, url }) =>
      QRCode.toCanvas(el, url, { width: 320, margin: 0 }).then(() => {
        // 尺寸交给打印样式按纸张大小计算，这里清掉库写入的 320px 行内样式
        el.style.width = ''
        el.style.height = ''
      }).catch(() => {})
    )
    qrRefs.length = 0
    Promise.all(qrTasks).then(() => { if (done) done() })
  })
}
function expandCopies(rows) {
  const n = Math.max(1, Number(copies.value) || 1)
  return rows.flatMap(row => Array.from({ length: n }, (_, i) => ({ ...row, _key: `${row.skuId}-${i}` })))
}
function handlePrint() {
  applyPageStyle()
  printItems.value = expandCopies(selection.value)
  renderQr(() => setTimeout(() => window.print(), 60))
}
function printOne(row) {
  applyPageStyle()
  printItems.value = expandCopies([row])
  renderQr(() => setTimeout(() => window.print(), 60))
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
    display: flex;
    flex-direction: column;
    justify-content: space-evenly;
    align-items: center;
    gap: 0.15em;
    width: var(--lw, 50mm);
    /* 比纸张高度少 1.5mm，给热敏机不可打印边距留余量，避免最后一行被挤到第二张 */
    height: calc(var(--lh, 30mm) - 1.5mm);
    padding: 1.2mm;
    margin: 0;
    box-sizing: border-box;
    border: none;
    text-align: center;
    overflow: hidden;
    /* 禁止手机端打印时自动放大字号，避免内容被撑出纸张 */
    -webkit-text-size-adjust: none;
    text-size-adjust: none;
    /* 去掉条码后按短边放大字号，并用标签宽度封顶防止长品名撑出纸外 */
    font-size: min(calc(min(var(--lw, 50mm), var(--lh, 30mm)) * 0.07), calc(var(--lw, 50mm) * 0.06));
    page-break-inside: avoid;
    break-inside: avoid;
    page-break-after: always;
    break-after: page;
  }
  .label-card:last-child {
    page-break-after: auto;
    break-after: auto;
  }
  .label-card .label-style { font-size: 1.15em; font-weight: 700; line-height: 1.1; }
  .label-card .label-name { font-size: 1em; line-height: 1.2; white-space: nowrap; max-width: 100%; overflow: hidden; text-overflow: ellipsis; }
  .label-card .label-attr { font-size: 1em; line-height: 1.2; }
  .label-card .label-barcode {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
  }
  .label-card .label-qr {
    /* 二维码放到短边的 46%，撑满纸张便于手机扫码 */
    flex: none;
    width: calc(min(var(--lw, 50mm), var(--lh, 30mm)) * 0.46);
    height: calc(min(var(--lw, 50mm), var(--lh, 30mm)) * 0.46);
  }
  .label-card.factory-card {
    /* 厂内标签只有两行，字号再放大一档并用宽度封顶 */
    font-size: min(calc(min(var(--lw, 50mm), var(--lh, 30mm)) * 0.15), calc(var(--lw, 50mm) * 0.1));
    gap: 0.2em;
  }
  .label-card.factory-card .factory-name {
    font-size: 1.2em;
    font-weight: 700;
    line-height: 1.2;
    max-width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .label-card.factory-card .factory-attr {
    font-size: 1em;
    line-height: 1.2;
    letter-spacing: 0.02em;
  }
}
</style>
