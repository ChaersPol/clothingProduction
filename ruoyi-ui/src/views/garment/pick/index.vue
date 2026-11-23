<template>
  <div class="app-container garment-page">
    <el-card class="section-card" shadow="never">
      <template #header><span>扫码领片</span></template>
      <el-form class="scan-bar" :inline="true" @submit.prevent>
        <el-form-item label="员工" required>
          <el-input :model-value="operatorName" disabled style="width: 180px" />
        </el-form-item>
        <el-form-item label="扎卡条码" class="scan-input">
          <el-input ref="scanRef" v-model="scanCode" placeholder="扫描/输入扎号自动领片" clearable @input="onCodeInput" @keyup.enter="onCodeEnter(scanCode)" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" v-hasPermi="['garment:pick:add']" @click="doPick">确认领片</el-button>
          <GarmentScan v-hasPermi="['garment:pick:add']" @scan="onScanPick" @error="m => proxy.$modal.msgWarning(m)" />
        </el-form-item>
      </el-form>
      <div v-if="route.query.code" class="muted" style="margin-top: 6px">
        已从二维码带入扎号 {{ route.query.code }}，确认员工无误后点击「确认领片」
      </div>
    </el-card>

    <el-form v-show="showSearch" class="search-form" :inline="true">
      <el-form-item label="员工" prop="employee">
        <el-input :model-value="operatorName" disabled style="width: 130px" />
      </el-form-item>
      <el-form-item label="扎号" prop="bundleNo">
        <el-input v-model="queryParams.bundleNo" placeholder="扎号" clearable style="width: 120px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="领片日期">
        <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD" start-placeholder="开始" end-placeholder="结束" style="width: 240px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <div class="table-wrap">
      <el-table v-loading="loading" :data="pickList" border>
        <el-table-column label="领片时间" prop="pickTime" width="170" />
        <el-table-column label="扎号" prop="bundleNo" width="150" />
        <el-table-column label="款号" prop="styleNo" min-width="110" />
        <el-table-column label="颜色" prop="color" width="100" />
        <el-table-column label="尺码" prop="size" width="70" align="center" />
        <el-table-column label="数量" prop="qty" width="80" align="right" />
        <el-table-column label="员工" prop="employee" width="90" />
      </el-table>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup name="Pick">
import { pickBundle, listPicks, commonBundle } from '@/api/garment'
import { currentOperatorName } from '@/utils/garment'
import GarmentScan from '@/components/GarmentScan/index.vue'
import { useAutoScan } from '@/utils/autoScan'

const showSearch = ref(true)

const { proxy } = getCurrentInstance()
const route = useRoute()
// 领片人固定为当前登录用户，不允许代选，列表也只展示自己的领片记录，避免产量统计串人
const operatorName = currentOperatorName()
const scanCode = ref('')
const scanRef = ref()
const pickList = ref([])
const loading = ref(false)
const total = ref(0)
const dateRange = ref([])
const queryParams = ref({ pageNum: 1, pageSize: 10, bundleNo: undefined })

function getList() {
  loading.value = true
  const params = { ...queryParams.value, employee: operatorName }
  if (dateRange.value && dateRange.value.length === 2) {
    params.params = { beginDate: dateRange.value[0], endDate: dateRange.value[1] }
  }
  listPicks(params).then(res => {
    pickList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { queryParams.value.bundleNo = undefined; dateRange.value = []; handleQuery() }
// 手机拍照扫码：识别后直接执行领片
function onScanPick(code) {
  scanCode.value = code
  doPick()
}
function doPick() {
  if (!operatorName) { proxy.$modal.msgWarning('未获取到当前登录用户'); return }
  if (!scanCode.value) { proxy.$modal.msgWarning('请扫描扎卡条码'); return }
  pickBundle({ code: scanCode.value.trim(), employee: operatorName }).then(res => {
    const b = res.data || {}
    proxy.$modal.msgSuccess(`领片成功：${b.bundleNo || ''} ${b.styleNo || ''} ${b.color || ''}/${b.size || ''} ${b.qty || ''}件`)
    scanCode.value = ''
    if (route.query.code) proxy.$router.replace({ path: route.path, query: {} })
    focusScan()
    getList()
  })
}

// 手动输入或扫码枪：输入停顿后自动查询并领片，无需回车
const { onInput: onCodeInput, cancel: cancelCode } = useAutoScan(code => {
  scanCode.value = code
  // 先静默判断扎卡是否存在，避免输入过程中弹出“扎卡不存在”
  commonBundle(code).then(res => { if (res.data) doPick() })
}, { minLength: 8 })
function onCodeEnter(code) {
  cancelCode()
  scanCode.value = code
  doPick()
}

// 手机系统相机扫扎卡上的二维码后打开本页，自动带入扎号
watch(() => route.query.code, code => {
  if (!code) return
  scanCode.value = String(code)
  focusScan()
}, { immediate: true })
// 扫码枪：页面进入后自动聚焦扫码框，领片成功继续保持焦点，支持连续扫描
function focusScan() {
  nextTick(() => { if (scanRef.value) scanRef.value.focus() })
}

getList()
focusScan()

// 从标签页切回本页时自动刷新数据，避免展示缓存中的旧列表
let _pageActivated = false
onActivated(() => {
  if (_pageActivated) {
    getList()
  } else {
    _pageActivated = true
  }
  focusScan()
})

</script>
