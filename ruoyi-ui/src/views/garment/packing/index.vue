<template>
  <div class="app-container garment-page">
    <el-card class="section-card" shadow="never">
      <template #header><span>成品扫码包装入库（产线 → 成品库）</span></template>
      <el-form class="scan-bar" :inline="true" @submit.prevent>
        <el-form-item label="扎卡条码" class="scan-input">
          <el-input v-model="scan.code" placeholder="扫描扎号，回车自动带出可入库数量" clearable @keyup.enter="fetchBundle" />
        </el-form-item>
        <el-form-item label="入库数量">
          <el-input-number v-model="scan.qty" :min="1" controls-position="right" style="width: 120px" />
        </el-form-item>
        <el-form-item label="包装方式">
          <el-select v-model="scan.packMethod" style="width: 110px">
            <el-option label="袋装" value="袋装" />
            <el-option label="盒装" value="盒装" />
            <el-option label="挂装" value="挂装" />
          </el-select>
        </el-form-item>
        <el-form-item label="每袋件数">
          <el-input-number v-model="scan.perBag" :min="1" controls-position="right" style="width: 110px" />
        </el-form-item>
        <el-form-item label="包装人">
          <el-select v-model="scan.packer" placeholder="选择" filterable style="width: 130px">
            <el-option v-for="e in employees" :key="e.employeeId" :label="e.name" :value="e.name" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Upload" @click="submitStockIn">包装入库</el-button>
        </el-form-item>
      </el-form>
      <div v-if="bundleInfo" class="muted" style="margin-top: 6px">
        当前扎包：{{ bundleInfo.bundleNo }} · {{ bundleInfo.styleNo }} · {{ bundleInfo.color }}/{{ bundleInfo.size }} ·
        已交后道 {{ bundleInfo.deliveredQty || 0 }} 件，已包装 {{ bundleInfo.packedQty || 0 }} 件
      </div>
    </el-card>

    <el-form v-show="showSearch" class="search-form" :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item label="扎号" prop="bundleNo">
        <el-input v-model="queryParams.bundleNo" placeholder="扎号" clearable style="width: 130px" @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="款号" prop="styleNo">
        <el-input v-model="queryParams.styleNo" placeholder="款号" clearable style="width: 120px" @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="包装人" prop="packer">
        <el-select v-model="queryParams.packer" placeholder="包装人" clearable filterable style="width: 130px">
          <el-option v-for="e in employees" :key="e.employeeId" :label="e.name" :value="e.name" />
        </el-select>
      </el-form-item>
      <el-form-item label="包装日期">
        <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD" start-placeholder="开始" end-placeholder="结束" style="width: 240px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <div class="table-wrap">
      <el-table v-loading="loading" :data="packList" border>
        <el-table-column label="包装时间" prop="packTime" width="170" />
        <el-table-column label="扎号" prop="bundleNo" width="150" />
        <el-table-column label="款号" prop="styleNo" min-width="110" />
        <el-table-column label="颜色" prop="color" width="100" />
        <el-table-column label="尺码" prop="size" width="65" align="center" />
        <el-table-column label="包装数量" prop="qty" width="90" align="right" />
        <el-table-column label="方式" prop="packMethod" width="80" align="center" />
        <el-table-column label="每袋件数" prop="perBag" width="90" align="center" />
        <el-table-column label="包装人" prop="packer" min-width="110" />
      </el-table>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup name="Packing">
import { listPack, finishingStockIn, commonBundle, commonEmployees } from '@/api/garment'

const showSearch = ref(true)

const { proxy } = getCurrentInstance()
const packList = ref([])
const employees = ref([])
const bundleInfo = ref(null)
const loading = ref(false)
const total = ref(0)
const dateRange = ref([])
const scan = ref({ code: '', qty: 1, packMethod: '袋装', perBag: 1, packer: '' })
const queryParams = ref({ pageNum: 1, pageSize: 10, bundleNo: undefined, styleNo: undefined, packer: undefined })
const route = useRoute()

// 支持从首页卡片深链过来，按日期过滤入库记录
watch(() => route.query, q => {
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
  listPack(params).then(res => {
    packList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}
function resetQuery() { proxy.resetForm('queryRef'); dateRange.value = []; getList() }
function fetchBundle() {
  if (!scan.value.code) { proxy.$modal.msgWarning('请输入扎卡条码'); return }
  commonBundle(scan.value.code.trim()).then(res => {
    const b = res.data
    if (!b) { proxy.$modal.msgWarning('扎卡不存在'); return }
    bundleInfo.value = b
    const delivered = Number(b.deliveredQty || 0) > 0 ? Number(b.deliveredQty) : Number(b.qty || 0)
    const available = delivered - Number(b.packedQty || 0)
    if (available <= 0) {
      proxy.$modal.msgWarning('该扎已全部包装入库')
      scan.value.qty = 1
    } else {
      scan.value.qty = available
    }
  })
}
function submitStockIn() {
  if (!scan.value.code) { proxy.$modal.msgWarning('请输入扎卡条码'); return }
  if (!scan.value.qty || scan.value.qty <= 0) { proxy.$modal.msgWarning('入库数量必须大于0'); return }
  finishingStockIn({
    code: scan.value.code.trim(), qty: scan.value.qty, packMethod: scan.value.packMethod,
    perBag: scan.value.perBag, packer: scan.value.packer
  }).then(() => {
    proxy.$modal.msgSuccess('成品入库成功')
    scan.value.code = ''
    scan.value.qty = 1
    bundleInfo.value = null
    getList()
  })
}

commonEmployees().then(res => {
  employees.value = res.data || []
  if (employees.value.length) scan.value.packer = employees.value[0].name
})
</script>
