<template>
  <div class="app-container garment-page">
    <el-form v-show="showSearch" class="search-form" :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item label="盘点单号" prop="checkNo">
        <el-input v-model="queryParams.checkNo" placeholder="单号" clearable style="width: 150px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
          <el-option label="进行中" value="0" />
          <el-option label="已完成" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" v-hasPermi="['garment:check:add']" @click="openCreate">新建盘点</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" v-hasPermi="['garment:check:remove']" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <div class="table-wrap">
      <el-table v-loading="loading" :data="checkList" border @selection-change="handleSelectionChange">
            <el-table-column v-if="checkPermi(['garment:check:remove'])" type="selection" width="50" align="center" />
            
        <el-table-column label="盘点单号" prop="checkNo" width="160" />
        <el-table-column label="盘点日期" prop="checkDate" width="120" />
        <el-table-column label="盘点人" prop="checker" width="120" />
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template #default="s">
            <el-tag size="small" :type="s.row.status === '1' ? 'success' : 'warning'">{{ s.row.status === '1' ? '已完成' : '进行中' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" min-width="180" show-overflow-tooltip />
        <el-table-column v-if="checkPermi(['garment:check:edit','garment:check:finish','garment:check:remove'])" label="操作" class-name="op-cell" width="220" align="center" fixed="right">
          <template #default="s">
            <el-button link type="primary" v-hasPermi="['garment:check:edit']" @click="openItems(s.row)">{{ s.row.status === '1' ? '查看' : '录入实盘' }}</el-button>
            <el-button link type="success" v-hasPermi="['garment:check:finish']" :disabled="s.row.status === '1'" @click="handleComplete(s.row)">完成盘点</el-button>
            <el-button link type="danger" v-hasPermi="['garment:check:remove']" @click="handleDelete(s.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="新建盘点" v-model="createOpen" width="480px" append-to-body>
      <el-form label-width="90px">
        <el-form-item label="盘点类型">
          <el-radio-group v-model="createForm.stockType">
            <el-radio value="CUT">裁片库</el-radio>
            <el-radio value="LINE">产线库</el-radio>
            <el-radio value="FINISH">成品库</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="盘点日期"><el-date-picker v-model="createForm.checkDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" /></el-form-item>
        <el-form-item label="盘点人">
          <el-input :model-value="operatorName" disabled />
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="createForm.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" v-hasPermi="['garment:check:add']" @click="submitCreate">生成盘点单</el-button>
        <el-button @click="createOpen = false">取 消</el-button>
      </template>
    </el-dialog>

    <el-dialog title="盘点明细" v-model="itemsOpen" width="820px" append-to-body>
      <el-descriptions :column="2" border size="small" style="margin-bottom: 12px">
        <el-descriptions-item label="盘点单号">{{ current.checkNo }}</el-descriptions-item>
        <el-descriptions-item label="盘点日期">{{ current.checkDate }}</el-descriptions-item>
        <el-descriptions-item label="盘点人">{{ current.checker }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ current.status === '1' ? '已完成' : '进行中' }}</el-descriptions-item>
      </el-descriptions>
      <div class="table-wrap">
        <el-table :data="items" border size="small" max-height="400">
          <el-table-column label="款号" prop="styleNo" min-width="100" />
          <el-table-column label="颜色" prop="color" width="90" />
          <el-table-column label="尺码" prop="size" width="65" align="center" />
          <el-table-column label="库存类型" prop="stockType" width="90" align="center" />
          <el-table-column label="账面数" prop="bookQty" width="90" align="right" />
          <el-table-column label="实盘数" width="120">
            <template #default="s">
              <el-input-number v-model="s.row.actualQty" :min="0" size="small" controls-position="right" style="width: 100%" :disabled="current.status === '1'" />
            </template>
          </el-table-column>
          <el-table-column label="差异" width="80" align="right">
            <template #default="s">
              <span :class="(s.row.actualQty - s.row.bookQty) === 0 ? '' : 'money'">{{ (s.row.actualQty || 0) - (s.row.bookQty || 0) }}</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <template #footer>
        <el-button v-if="current.status !== '1'" v-hasPermi="['garment:check:edit']" type="primary" @click="saveItems">保存实盘</el-button>
        <el-button @click="itemsOpen = false">关 闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Check">
import { listCheck, getCheck, addCheck, saveCheckItems, completeCheck, delCheck } from '@/api/garment'
import { currentOperatorName } from '@/utils/garment'

const showSearch = ref(true)

const ids = ref([])
const multiple = ref(true)

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.checkId)
  multiple.value = !selection.length
}

const { proxy } = getCurrentInstance()
const checkList = ref([])
const items = ref([])
const current = ref({})
const loading = ref(false)
const total = ref(0)
const createOpen = ref(false)
const itemsOpen = ref(false)
const createForm = ref({})
// 盘点人固定为当前登录用户
const operatorName = currentOperatorName()
const queryParams = ref({ pageNum: 1, pageSize: 10, checkNo: undefined, status: undefined })

function today() {
  const d = new Date()
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}
function getList() {
  loading.value = true
  listCheck(queryParams.value).then(res => {
    checkList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
function openCreate() {
  createForm.value = { stockType: 'FINISH', checkDate: today(), checker: operatorName, remark: '' }
  createOpen.value = true
}
function submitCreate() {
  addCheck(createForm.value, createForm.value.stockType).then(res => {
    proxy.$modal.msgSuccess('盘点单已生成，请录入实盘数')
    createOpen.value = false
    getList()
    const check = res.data
    if (check && check.checkId) openItems(check)
  })
}
function openItems(row) {
  getCheck(row.checkId).then(res => {
    current.value = res.data || {}
    items.value = (res.data.items || []).map(i => ({ ...i, actualQty: i.actualQty == null ? i.bookQty : i.actualQty }))
    itemsOpen.value = true
  })
}
function saveItems() {
  const payload = {
    checkId: current.value.checkId,
    items: items.value.map(i => ({ itemId: i.itemId, bookQty: i.bookQty, actualQty: i.actualQty }))
  }
  saveCheckItems(payload).then(() => {
    proxy.$modal.msgSuccess('实盘已保存')
    itemsOpen.value = false
    getList()
  })
}
function handleComplete(row) {
  proxy.$modal.confirm('完成盘点后将按实盘数调整库存，是否继续？').then(() => completeCheck(row.checkId)).then(() => {
    proxy.$modal.msgSuccess('盘点已完成，库存已调整')
    getList()
  }).catch(() => {})
}
function handleDelete(row) {
  const _ids = row ? [row.checkId] : ids.value
  if (!_ids.length) return
  proxy.$modal.confirm(row ? '确认删除盘点单？' : `是否确认删除选中的 ${_ids.length} 条数据？`).then(() => delCheck(_ids)).then(() => {
    proxy.$modal.msgSuccess('删除成功')
    getList()
    ids.value = []
    multiple.value = true
  }).catch(() => {})
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
