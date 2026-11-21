<template>
  <div class="app-container garment-page">
    <el-form v-show="showSearch" class="search-form" :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item label="姓名" prop="name">
        <el-input v-model="queryParams.name" placeholder="员工姓名" clearable style="width: 140px" @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="岗位" prop="position">
        <el-input v-model="queryParams.position" placeholder="岗位/工种" clearable style="width: 140px" @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="在职状态" clearable style="width: 120px">
          <el-option label="在职" value="0" />
          <el-option label="离职" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['garment:employee:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['garment:employee:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <div class="table-wrap">
      <el-table v-loading="loading" :data="employeeList" border @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column label="姓名" prop="name" width="120" />
        <el-table-column label="岗位/工种" prop="position" min-width="160" show-overflow-tooltip />
        <el-table-column label="排序" prop="sort" width="80" align="center" />
        <el-table-column label="状态" prop="status" width="90" align="center">
          <template #default="s">
            <el-tag :type="s.row.status === '0' ? 'success' : 'info'" size="small">{{ s.row.status === '0' ? '在职' : '离职' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" class-name="op-cell" width="130" align="center">
          <template #default="s">
            <el-button link type="primary" @click="handleUpdate(s.row)" v-hasPermi="['garment:employee:edit']">修改</el-button>
            <el-button link type="danger" @click="handleDelete(s.row)" v-hasPermi="['garment:employee:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="form.employeeId ? '修改员工' : '新增员工'" v-model="open" width="560px" append-to-body>
      <el-form ref="employeeRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="员工姓名" />
        </el-form-item>
        <el-form-item label="岗位/工种">
          <el-input v-model="form.position" placeholder="如 车间-车工、后道-检验" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio value="0">在职</el-radio>
            <el-radio value="1">离职</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Employee">
import { listEmployee, addEmployee, updateEmployee, delEmployee } from '@/api/garment'

const showSearch = ref(true)

const ids = ref([])
const multiple = ref(true)

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.employeeId)
  multiple.value = !selection.length
}

const { proxy } = getCurrentInstance()
const employeeList = ref([])
const loading = ref(false)
const total = ref(0)
const open = ref(false)
const form = ref({})
const queryParams = ref({ pageNum: 1, pageSize: 10, name: undefined, position: undefined, status: undefined })
const rules = { name: [{ required: true, message: '请输入员工姓名', trigger: 'blur' }] }

function getList() {
  loading.value = true
  listEmployee(queryParams.value).then(res => {
    employeeList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}
function resetQuery() {
  proxy.resetForm('queryRef')
  queryParams.value.pageNum = 1
  getList()
}
function reset() {
  form.value = { employeeId: undefined, name: undefined, position: undefined, sort: 0, status: '0', remark: undefined }
  proxy.resetForm('employeeRef')
}
function handleAdd() { reset(); open.value = true }
function handleUpdate(row) { form.value = { ...row }; open.value = true }
function submitForm() {
  proxy.$refs['employeeRef'].validate(valid => {
    if (!valid) return
    const fn = form.value.employeeId ? updateEmployee : addEmployee
    fn(form.value).then(() => {
      proxy.$modal.msgSuccess('保存成功')
      open.value = false
      getList()
    })
  })
}
function handleDelete(row) {
  const _ids = row ? [row.employeeId] : ids.value
  if (!_ids.length) return
  proxy.$modal.confirm(row ? `确认删除员工「${row.name}」？` : `是否确认删除选中的 ${_ids.length} 条数据？`).then(() => delEmployee(_ids)).then(() => {
    proxy.$modal.msgSuccess('删除成功')
    getList()
    ids.value = []
    multiple.value = true
  }).catch(() => {})
}

getList()
</script>
