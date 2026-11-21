<template>
  <div class="app-container garment-page">
    <el-form v-show="showSearch" class="search-form" :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item label="裁货单号" prop="cutNo">
        <el-input v-model="queryParams.cutNo" placeholder="单号" clearable style="width: 160px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="款号" prop="styleNo">
        <el-input v-model="queryParams.styleNo" placeholder="款号" clearable style="width: 130px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="裁工" prop="cutter">
        <el-input v-model="queryParams.cutter" placeholder="裁工" clearable style="width: 110px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
          <el-option label="待分扎" value="0" />
          <el-option label="已入库" value="1" />
          <el-option label="已完成" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="裁货日期">
        <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD" start-placeholder="开始" end-placeholder="结束" style="width: 240px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd">裁货开单</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button plain icon="Download" @click="handleExport">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <div class="table-wrap">
      <el-table v-loading="loading" :data="orderList" border @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="50" align="center" />
            
        <el-table-column label="裁货单号" prop="cutNo" width="150" fixed="left" />
        <el-table-column label="款号" prop="styleNo" width="100" />
        <el-table-column label="品名" prop="cnName" min-width="140" show-overflow-tooltip />
        <el-table-column label="客户" prop="customer" width="110" />
        <el-table-column label="面料" prop="fabric" min-width="110" show-overflow-tooltip />
        <el-table-column label="总件数" prop="totalQty" width="85" align="right" />
        <el-table-column label="层数" prop="layerCount" width="70" align="center" />
        <el-table-column label="裁工" prop="cutter" width="90" />
        <el-table-column label="裁货日期" prop="cutDate" width="110" />
        <el-table-column label="扎数" prop="bundleCount" width="70" align="center" />
        <el-table-column label="状态" prop="status" width="95" align="center">
          <template #default="s">
            <el-tag size="small" :type="s.row.status === '2' ? 'success' : s.row.status === '1' ? 'warning' : 'danger'">{{ statusText(s.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" class-name="op-cell" width="240" align="center" fixed="right">
          <template #default="s">
            <el-button link type="primary" @click="viewDetail(s.row)">明细</el-button>
            <el-button link type="success" @click="goBundle(s.row)">去分扎</el-button>
            <el-button link type="primary" @click="handleUpdate(s.row)">修改</el-button>
            <el-button link type="danger" @click="handleDelete(s.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="form.cutId ? '修改裁货单' : '裁货开单'" v-model="open" width="900px" append-to-body>
      <el-form ref="cutRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="12">
          <el-col :xs="24" :sm="8">
            <el-form-item label="款号" prop="styleNo">
              <el-select v-model="form.styleNo" placeholder="选择款号" filterable style="width: 100%" @change="onStyleChange">
                <el-option v-for="s in styles" :key="s.styleNo" :label="s.styleNo + ' ' + s.cnName" :value="s.styleNo" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="品名"><el-input v-model="form.cnName" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="客户"><el-input v-model="form.customer" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="面料"><el-input v-model="form.fabric" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="里布"><el-input v-model="form.lining" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="层数"><el-input-number v-model="form.layerCount" :min="0" controls-position="right" style="width: 100%" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="裁工"><el-input v-model="form.cutter" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="裁货日期"><el-date-picker v-model="form.cutDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="总件数"><el-input v-model="form.totalQty" disabled /></el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">裁片明细</el-divider>
        <div class="toolbar">
          <el-select v-model="gen.colors" multiple collapse-tags placeholder="选择颜色" size="small" style="width: 200px">
            <el-option v-for="c in colors" :key="c.cnName" :label="c.cnName" :value="c.cnName" />
          </el-select>
          <el-select v-model="gen.sizes" multiple collapse-tags placeholder="选择尺码" size="small" style="width: 180px">
            <el-option v-for="s in sizes" :key="s.sizeName" :label="s.sizeName" :value="s.sizeName" />
          </el-select>
          <el-input-number v-model="gen.qty" :min="1" controls-position="right" size="small" style="width: 110px" />
          <el-button type="primary" plain size="small" @click="generateMatrix">生成明细</el-button>
          <el-button size="small" icon="Plus" @click="addRow">手动添加</el-button>
        </div>
        <el-table :data="form.items" border size="small" max-height="280">
          <el-table-column label="层次" width="80">
            <template #default="s"><el-input-number v-model="s.row.layerNo" :min="1" size="small" controls-position="right" style="width: 100%" /></template>
          </el-table-column>
          <el-table-column label="颜色" min-width="150">
            <template #default="s"><el-input v-model="s.row.color" size="small" /></template>
          </el-table-column>
          <el-table-column label="尺码" min-width="110">
            <template #default="s"><el-input v-model="s.row.size" size="small" /></template>
          </el-table-column>
          <el-table-column label="数量" width="130">
            <template #default="s"><el-input-number v-model="s.row.qty" :min="0" size="small" controls-position="right" style="width: 100%" /></template>
          </el-table-column>
          <el-table-column label="操作" class-name="op-cell" width="70" align="center">
            <template #default="s"><el-button link type="danger" @click="form.items.splice(s.$index, 1)">删除</el-button></template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">保存裁货单</el-button>
        <el-button @click="open = false">取 消</el-button>
      </template>
    </el-dialog>

    <el-dialog title="裁货明细" v-model="detailOpen" width="720px" append-to-body>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="裁货单号">{{ detail.cutNo }}</el-descriptions-item>
        <el-descriptions-item label="款号">{{ detail.styleNo }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ detail.customer }}</el-descriptions-item>
        <el-descriptions-item label="总件数">{{ detail.totalQty }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="detail.items || []" border size="small" style="margin-top: 12px">
        <el-table-column label="层次" prop="layerNo" min-width="80" />
        <el-table-column label="颜色" prop="color" width="140" />
        <el-table-column label="尺码" prop="size" width="100" />
        <el-table-column label="数量" prop="qty" width="100" align="right" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup name="CutOrder">
import { useRouter } from 'vue-router'
import { listCutOrder, getCutOrder, addCutOrder, updateCutOrder, delCutOrder, commonStyles, commonColors, commonSizes } from '@/api/garment'

const showSearch = ref(true)

const ids = ref([])
const multiple = ref(true)

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.cutId)
  multiple.value = !selection.length
}

const { proxy } = getCurrentInstance()
const router = useRouter()
const orderList = ref([])
const styles = ref([])
const colors = ref([])
const sizes = ref([])
const loading = ref(false)
const total = ref(0)
const open = ref(false)
const detailOpen = ref(false)
const detail = ref({})
const dateRange = ref([])
const form = ref({})
const gen = ref({ colors: [], sizes: [], qty: 1 })
const queryParams = ref({ pageNum: 1, pageSize: 10, cutNo: undefined, styleNo: undefined, cutter: undefined, status: undefined })
const rules = { styleNo: [{ required: true, message: '请选择款号', trigger: 'change' }] }

const totalQty = computed(() => (form.value.items || []).reduce((s, i) => s + Number(i.qty || 0), 0))
watch(totalQty, v => { form.value.totalQty = v })

function statusText(s) { return s === '2' ? '已完成' : s === '1' ? '已入库' : '待分扎' }
function today() {
  const d = new Date()
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}
function getList() {
  loading.value = true
  const params = { ...queryParams.value }
  if (dateRange.value && dateRange.value.length === 2) {
    params.params = { beginDate: dateRange.value[0], endDate: dateRange.value[1] }
  }
  listCutOrder(params).then(res => {
    orderList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); dateRange.value = []; handleQuery() }
function onStyleChange(styleNo) {
  const s = styles.value.find(i => i.styleNo === styleNo)
  if (s) form.value.cnName = s.cnName
}
function addRow() {
  if (!form.value.items) form.value.items = []
  form.value.items.push({ layerNo: 1, color: '', size: '', qty: 0 })
}
function generateMatrix() {
  if (!gen.value.colors.length || !gen.value.sizes.length) {
    proxy.$modal.msgWarning('请先选择颜色和尺码')
    return
  }
  const items = []
  gen.value.colors.forEach(c => {
    gen.value.sizes.forEach(s => {
      items.push({ layerNo: 1, color: c, size: s, qty: gen.value.qty })
    })
  })
  form.value.items = items
}
function handleAdd() {
  form.value = { styleNo: undefined, cnName: '', customer: '', fabric: '', lining: '', layerCount: 1, cutter: '', cutDate: today(), totalQty: 0, items: [] }
  gen.value = { colors: [], sizes: [], qty: 1 }
  open.value = true
}
function handleUpdate(row) {
  getCutOrder(row.cutId).then(res => {
    form.value = res.data
    if (!form.value.items) form.value.items = []
    open.value = true
  })
}
function submitForm() {
  proxy.$refs['cutRef'].validate(valid => {
    if (!valid) return
    if (!form.value.items || !form.value.items.length) {
      proxy.$modal.msgWarning('请录入裁片明细')
      return
    }
    form.value.totalQty = totalQty.value
    const fn = form.value.cutId ? updateCutOrder : addCutOrder
    fn(form.value).then(() => {
      proxy.$modal.msgSuccess('保存成功')
      open.value = false
      getList()
    })
  })
}
function viewDetail(row) {
  getCutOrder(row.cutId).then(res => { detail.value = res.data; detailOpen.value = true })
}
function goBundle(row) {
  router.push({ path: '/cut/bundle', query: { cutId: row.cutId } })
}
function handleDelete(row) {
  const _ids = row ? [row.cutId] : ids.value
  if (!_ids.length) return
  proxy.$modal.confirm(row ? `确认删除裁货单「${row.cutNo}」？关联扎包也会一并清理。` : `是否确认删除选中的 ${_ids.length} 条数据？`).then(() => delCutOrder(_ids)).then(() => {
    proxy.$modal.msgSuccess('删除成功')
    getList()
    ids.value = []
    multiple.value = true
  }).catch(() => {})
}
function handleExport() {
  proxy.download('garment/cutorder/export', { ...queryParams.value }, `cutorder_${Date.now()}.xlsx`)
}

getList()
commonStyles().then(res => { styles.value = res.data || [] })
commonColors().then(res => { colors.value = res.data || [] })
commonSizes().then(res => { sizes.value = res.data || [] })
</script>
