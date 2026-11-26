<template>
  <div class="app-container garment-page">
    <el-form v-show="showSearch" class="search-form" :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item label="客户名称" prop="customerName">
        <el-input v-model="queryParams.customerName" placeholder="客户名称" clearable style="width: 180px" @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="联系人" prop="contact">
        <el-input v-model="queryParams.contact" placeholder="联系人" clearable style="width: 130px" @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="电话" prop="phone">
        <el-input v-model="queryParams.phone" placeholder="电话" clearable style="width: 140px" @keyup.enter="getList" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" v-hasPermi="['garment:customer:add']" @click="handleAdd">新增客户</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button plain icon="Download" v-hasPermi="['garment:customer:export']" @click="handleExport">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" v-hasPermi="['garment:customer:remove']" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <div class="table-wrap">
      <el-table v-loading="loading" :data="customerList" border @selection-change="handleSelectionChange">
        <el-table-column v-if="checkPermi(['garment:customer:remove'])" type="selection" width="50" align="center" />
        <el-table-column label="客户名称" prop="customerName" min-width="180" show-overflow-tooltip />
        <el-table-column label="联系人" prop="contact" width="110" />
        <el-table-column label="电话" prop="phone" width="140" />
        <el-table-column label="地址" prop="address" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" prop="status" width="90" align="center">
          <template #default="s">
            <el-tag :type="s.row.status === '0' ? 'success' : 'info'" size="small">{{ s.row.status === '0' ? '正常' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" min-width="140" show-overflow-tooltip />
        <el-table-column v-if="checkPermi(['garment:customer:edit','garment:customer:remove'])" label="操作" class-name="op-cell" width="130" align="center" fixed="right">
          <template #default="s">
            <el-button link type="primary" v-hasPermi="['garment:customer:edit']" @click="handleUpdate(s.row)">修改</el-button>
            <el-button link type="danger" v-hasPermi="['garment:customer:remove']" @click="handleDelete(s.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="form.customerId ? '修改客户' : '新增客户'" v-model="open" width="600px" append-to-body>
      <el-form ref="customerRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="客户名称" prop="customerName"><el-input v-model="form.customerName" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="联系人"><el-input v-model="form.contact" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio value="0">正常</el-radio>
                <el-radio value="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="地址"><el-input v-model="form.address" /></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button type="primary" v-hasPermi="['garment:customer:add','garment:customer:edit']" @click="submitForm">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Customer">
import { listCustomer, addCustomer, updateCustomer, delCustomer } from '@/api/garment'

const showSearch = ref(true)
const ids = ref([])
const multiple = ref(true)

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.customerId)
  multiple.value = !selection.length
}

const { proxy } = getCurrentInstance()
const customerList = ref([])
const loading = ref(false)
const total = ref(0)
const open = ref(false)
const form = ref({})
const queryParams = ref({ pageNum: 1, pageSize: 10, customerName: undefined, contact: undefined, phone: undefined })
const rules = { customerName: [{ required: true, message: '请输入客户名称', trigger: 'blur' }] }

function getList() {
  loading.value = true
  listCustomer(queryParams.value).then(res => {
    customerList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}
function reset() {
  form.value = { customerName: undefined, contact: undefined, phone: undefined, address: undefined, status: '0', remark: undefined }
  proxy.resetForm('customerRef')
}
function resetQuery() { proxy.resetForm('queryRef'); getList() }
function handleAdd() { reset(); open.value = true }
function handleUpdate(row) { form.value = { ...row }; open.value = true }
function submitForm() {
  proxy.$refs['customerRef'].validate(valid => {
    if (!valid) return
    const fn = form.value.customerId ? updateCustomer : addCustomer
    fn(form.value).then(() => {
      proxy.$modal.msgSuccess('保存成功')
      open.value = false
      getList()
    })
  })
}
function handleDelete(row) {
  const isRow = !!(row && row.customerId)
  const _ids = isRow ? [row.customerId] : ids.value
  if (!_ids.length) return
  proxy.$modal.confirm(isRow ? `确认删除客户「${row.customerName}」？` : `是否确认删除选中的 ${_ids.length} 条数据？`).then(() => delCustomer(_ids)).then(() => {
    proxy.$modal.msgSuccess('删除成功')
    getList()
    ids.value = []
    multiple.value = true
  }).catch(() => {})
}
function handleExport() {
  proxy.download('garment/customer/export', { ...queryParams.value }, `customer_${Date.now()}.xlsx`)
}

getList()
</script>
