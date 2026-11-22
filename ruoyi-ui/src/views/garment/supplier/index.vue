<template>
  <div class="app-container garment-page">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="供应商管理" name="list">
        <el-form v-show="showSearch" class="search-form" :model="queryParams" ref="queryRef" :inline="true">
          <el-form-item label="名称" prop="supplierName">
            <el-input v-model="queryParams.supplierName" placeholder="供应商名称" clearable style="width: 180px" @keyup.enter="getList" />
          </el-form-item>
          <el-form-item label="联系人" prop="contact">
            <el-input v-model="queryParams.contact" placeholder="联系人" clearable style="width: 130px" @keyup.enter="getList" />
          </el-form-item>
          <el-form-item label="供货内容" prop="supplyContent">
            <el-input v-model="queryParams.supplyContent" placeholder="如 面料/辅料" clearable style="width: 150px" @keyup.enter="getList" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" v-hasPermi="['garment:supplier:add']" @click="handleAdd">新增供应商</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button plain icon="Download" v-hasPermi="['garment:supplier:export']" @click="handleExport">导出</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" v-hasPermi="['garment:supplier:remove']" :disabled="multiple" @click="handleDelete">删除</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>

        <div class="table-wrap">
          <el-table v-loading="loading" :data="supplierList" border @selection-change="handleSelectionChange">
            <el-table-column v-if="checkPermi(['garment:supplier:remove'])" type="selection" width="50" align="center" />
            
            <el-table-column label="供应商名称" prop="supplierName" min-width="160" show-overflow-tooltip />
            <el-table-column label="联系人" prop="contact" width="100" />
            <el-table-column label="电话" prop="phone" width="130" />
            <el-table-column label="供货内容" prop="supplyContent" min-width="180" show-overflow-tooltip />
            <el-table-column label="地址" prop="address" min-width="150" show-overflow-tooltip />
            <el-table-column label="状态" prop="status" width="90" align="center">
              <template #default="s">
                <el-tag :type="s.row.status === '0' ? 'success' : 'info'" size="small">{{ s.row.status === '0' ? '正常' : '停用' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column v-if="checkPermi(['garment:supplier:edit','garment:supplier:remove'])" label="操作" class-name="op-cell" width="130" align="center" fixed="right">
              <template #default="s">
                <el-button link type="primary" v-hasPermi="['garment:supplier:edit']" @click="handleUpdate(s.row)">修改</el-button>
                <el-button link type="danger" v-hasPermi="['garment:supplier:remove']" @click="handleDelete(s.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-tab-pane>

      <el-tab-pane label="供应商对账" name="recon">
        <div class="mb8">
          <span class="muted">按供应商汇总采购金额、已付与未付</span>
        </div>
        <div class="table-wrap">
          <el-table v-loading="reconLoading" :data="reconList" border show-summary :summary-method="sumRecon">
            <el-table-column label="供应商" prop="supplierName" min-width="170" show-overflow-tooltip />
            <el-table-column label="联系人" prop="contact" width="100" />
            <el-table-column label="电话" prop="phone" width="130" />
            <el-table-column label="采购单数" prop="orderCount" width="100" align="center" />
            <el-table-column label="采购金额" prop="totalAmount" width="120" align="right" />
            <el-table-column label="已付金额" prop="paidAmount" width="120" align="right" />
            <el-table-column label="未付金额" prop="unpaidAmount" width="120" align="right">
              <template #default="s"><span class="money">{{ s.row.unpaidAmount }}</span></template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog :title="form.supplierId ? '修改供应商' : '新增供应商'" v-model="open" width="600px" append-to-body>
      <el-form ref="supplierRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="供应商名称" prop="supplierName"><el-input v-model="form.supplierName" /></el-form-item>
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
            <el-form-item label="供货内容"><el-input v-model="form.supplyContent" placeholder="如 雪纺、里布、拉链等" /></el-form-item>
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
        <el-button type="primary" v-hasPermi="['garment:supplier:add','garment:supplier:edit']" @click="submitForm">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Supplier">
import { listSupplier, addSupplier, updateSupplier, delSupplier, reconciliation } from '@/api/garment'

const showSearch = ref(true)

const ids = ref([])
const multiple = ref(true)

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.supplierId)
  multiple.value = !selection.length
}

const { proxy } = getCurrentInstance()
const activeTab = ref('list')
const supplierList = ref([])
const reconList = ref([])
const loading = ref(false)
const reconLoading = ref(false)
const total = ref(0)
const open = ref(false)
const form = ref({})
const queryParams = ref({ pageNum: 1, pageSize: 10, supplierName: undefined, contact: undefined, supplyContent: undefined })
const rules = { supplierName: [{ required: true, message: '请输入供应商名称', trigger: 'blur' }] }

function getList() {
  loading.value = true
  listSupplier(queryParams.value).then(res => {
    supplierList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}
function getRecon() {
  reconLoading.value = true
  reconciliation({ pageNum: 1, pageSize: 100000 }).then(res => {
    reconList.value = res.rows || []
    reconLoading.value = false
  }).catch(() => { reconLoading.value = false })
}
function reset() {
  form.value = { supplierName: undefined, contact: undefined, phone: undefined, supplyContent: undefined, address: undefined, status: '0', remark: undefined }
  proxy.resetForm('supplierRef')
}
function resetQuery() { proxy.resetForm('queryRef'); getList() }
function handleAdd() { reset(); open.value = true }
function handleUpdate(row) { form.value = { ...row }; open.value = true }
function submitForm() {
  proxy.$refs['supplierRef'].validate(valid => {
    if (!valid) return
    const fn = form.value.supplierId ? updateSupplier : addSupplier
    fn(form.value).then(() => {
      proxy.$modal.msgSuccess('保存成功')
      open.value = false
      getList()
      getRecon()
    })
  })
}
function handleDelete(row) {
  const _ids = row ? [row.supplierId] : ids.value
  if (!_ids.length) return
  proxy.$modal.confirm(row ? `确认删除供应商「${row.supplierName}」？` : `是否确认删除选中的 ${_ids.length} 条数据？`).then(() => delSupplier(_ids)).then(() => {
    proxy.$modal.msgSuccess('删除成功')
    getList(); getRecon()
    ids.value = []
    multiple.value = true
  }).catch(() => {})
}
function sumRecon({ columns, data }) {
  return columns.map((col, i) => {
    if (i === 0) return '合计'
    if (['totalAmount', 'paidAmount', 'unpaidAmount'].includes(col.property)) {
      return data.reduce((s, r) => s + Number(r[col.property] || 0), 0).toFixed(2)
    }
    return ''
  })
}
function handleExport() {
  proxy.download('garment/supplier/export', { ...queryParams.value }, `supplier_${Date.now()}.xlsx`)
}

getList()
getRecon()

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
