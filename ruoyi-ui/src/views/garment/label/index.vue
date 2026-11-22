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
        <el-button type="primary" icon="Printer" :disabled="!selection.length" @click="handlePrint">打印标签</el-button>
      </el-col>
      <el-col :span="6" style="display: flex; align-items: center">
        <el-radio-group v-model="labelType" size="small">
          <el-radio-button value="FACTORY">厂内标签</el-radio-button>
          <el-radio-button value="PRODUCT">成品标签</el-radio-button>
        </el-radio-group>
      </el-col>
      <el-col :span="4" style="display: flex; align-items: center">
        <span class="muted">已选 {{ selection.length }} 个 SKU</span>
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
            <el-button link type="primary" @click="printOne(s.row)">打印</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 打印区域 -->
    <div id="label-print-area">
      <div v-for="item in printItems" :key="item.skuId" class="label-card" :class="{ 'factory-card': labelType === 'FACTORY' }">
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
  </div>
</template>

<script setup name="Label">
import JsBarcode from 'jsbarcode'
import { labelList } from '@/api/garment'

const showSearch = ref(true)
const labelType = ref('FACTORY')

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
function renderBarcodes() {
  nextTick(() => {
    barcodeRefs.forEach(({ el, code }) => {
      try {
        JsBarcode(el, code, { format: 'CODE128', width: 1.2, height: 34, displayValue: false, margin: 0 })
      } catch (e) { /* 忽略非法条码 */ }
    })
    barcodeRefs.length = 0
  })
}
function handlePrint() {
  printItems.value = selection.value
  renderBarcodes()
  setTimeout(() => window.print(), 300)
}
function printOne(row) {
  printItems.value = [row]
  renderBarcodes()
  setTimeout(() => window.print(), 300)
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
    display: flex;
    flex-wrap: wrap;
    gap: 4mm;
  }
  .label-card {
    width: 50mm;
    height: 30mm;
    border: 1px dashed #999;
    padding: 2mm;
    box-sizing: border-box;
    text-align: center;
    page-break-inside: avoid;
  }
  .label-card .label-style { font-size: 11px; font-weight: bold; }
  .label-card .label-name { font-size: 9px; white-space: nowrap; overflow: hidden; }
  .label-card .label-attr { font-size: 9px; }
  .label-card .label-code { font-size: 7px; letter-spacing: 0.5px; }
  .label-card svg { width: 100%; height: 8mm; }
  .label-card.factory-card {
    height: auto;
    min-height: 18mm;
    display: flex;
    flex-direction: column;
    justify-content: center;
    border-style: solid;
  }
  .label-card.factory-card .factory-name {
    font-size: 14px;
    font-weight: 700;
    margin-bottom: 2mm;
    white-space: nowrap;
  }
  .label-card.factory-card .factory-attr {
    font-size: 12px;
    letter-spacing: 0.3px;
  }
}
</style>
