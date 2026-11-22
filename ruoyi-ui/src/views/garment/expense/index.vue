<template>
  <div class="app-container garment-page">
    <el-form v-show="showSearch" class="search-form" :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item label="费用类型" prop="expenseType">
        <el-select v-model="queryParams.expenseType" placeholder="全部" clearable style="width: 140px">
          <el-option v-for="t in types" :key="t" :label="t" :value="t" />
        </el-select>
      </el-form-item>
      <el-form-item label="款号" prop="styleNo">
        <el-input v-model="queryParams.styleNo" placeholder="款号" clearable style="width: 130px" @keyup.enter="getList" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" v-hasPermi="['garment:expense:add']" @click="handleAdd">新增费用</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" v-hasPermi="['garment:expense:remove']" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <div class="stat-grid">
      <div class="stat-card red"><div class="stat-label">当前页费用合计</div><div class="stat-value">¥{{ pageTotal }}</div></div>
      <div class="stat-card"><div class="stat-label">费用记录数</div><div class="stat-value">{{ total }}</div></div>
    </div>



    <div class="table-wrap">
      <el-table v-loading="loading" :data="expenseList" border @selection-change="handleSelectionChange">
            <el-table-column v-if="checkPermi(['garment:expense:remove'])" type="selection" width="50" align="center" />
            
        <el-table-column label="费用日期" prop="expenseDate" width="120" />
        <el-table-column label="费用类型" prop="expenseType" width="130" />
        <el-table-column label="关联款号" prop="styleNo" width="120" />
        <el-table-column label="金额" prop="amount" width="120" align="right">
          <template #default="s"><span class="money">¥{{ s.row.amount }}</span></template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" min-width="180" show-overflow-tooltip />
        <el-table-column v-if="checkPermi(['garment:expense:edit','garment:expense:remove'])" label="操作" class-name="op-cell" width="140" align="center" fixed="right">
          <template #default="s">
            <el-button link type="primary" v-hasPermi="['garment:expense:edit']" @click="handleUpdate(s.row)">修改</el-button>
            <el-button link type="danger" v-hasPermi="['garment:expense:remove']" @click="handleDelete(s.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="form.expenseId ? '修改费用' : '新增费用'" v-model="open" width="520px" append-to-body>
      <el-form ref="expenseRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="费用类型" prop="expenseType">
          <el-select v-model="form.expenseType" placeholder="选择类型" style="width: 100%">
            <el-option v-for="t in types" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联款号"><el-input v-model="form.styleNo" placeholder="可选" /></el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input-number v-model="form.amount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="费用日期">
          <el-date-picker v-model="form.expenseDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" v-hasPermi="['garment:expense:add','garment:expense:edit']" @click="submitForm">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Expense">
import { listExpense, addExpense, updateExpense, delExpense } from '@/api/garment'

const showSearch = ref(true)

const ids = ref([])
const multiple = ref(true)

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.expenseId)
  multiple.value = !selection.length
}

const { proxy } = getCurrentInstance()
const types = ['辅料', '加工费', '运费', '海运费', '头程运费', '尾程运费', '运营费', '拍摄费', '办公', '差旅', '推广', '其他']
const expenseList = ref([])
const loading = ref(false)
const total = ref(0)
const open = ref(false)
const form = ref({})
const queryParams = ref({ pageNum: 1, pageSize: 10, expenseType: undefined, styleNo: undefined })
const rules = {
  expenseType: [{ required: true, message: '请选择费用类型', trigger: 'change' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }]
}

const pageTotal = computed(() => expenseList.value.reduce((s, i) => s + Number(i.amount || 0), 0).toFixed(2))

function today() {
  const d = new Date()
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}
function getList() {
  loading.value = true
  listExpense(queryParams.value).then(res => {
    expenseList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}
function resetQuery() { proxy.resetForm('queryRef'); getList() }
function handleAdd() {
  form.value = { expenseType: '辅料', styleNo: '', amount: 0, expenseDate: today(), remark: '' }
  open.value = true
}
function handleUpdate(row) { form.value = { ...row }; open.value = true }
function submitForm() {
  proxy.$refs['expenseRef'].validate(valid => {
    if (!valid) return
    const fn = form.value.expenseId ? updateExpense : addExpense
    fn(form.value).then(() => {
      proxy.$modal.msgSuccess('保存成功')
      open.value = false
      getList()
    })
  })
}
function handleDelete(row) {
  const _ids = row ? [row.expenseId] : ids.value
  if (!_ids.length) return
  proxy.$modal.confirm(row ? '确认删除该费用记录？' : `是否确认删除选中的 ${_ids.length} 条数据？`).then(() => delExpense(_ids)).then(() => {
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
