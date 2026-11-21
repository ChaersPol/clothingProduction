<template>
  <div class="app-container garment-page">
    <el-form v-show="showSearch" class="search-form" :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item label="款号" prop="styleNo">
        <el-select v-model="queryParams.styleNo" placeholder="选择款号" clearable filterable style="width: 180px">
          <el-option v-for="s in styles" :key="s.styleNo" :label="s.styleNo + ' ' + s.cnName" :value="s.styleNo" />
        </el-select>
      </el-form-item>
      <el-form-item label="物料类型" prop="materialType">
        <el-select v-model="queryParams.materialType" placeholder="全部" clearable style="width: 130px">
          <el-option v-for="t in types" :key="t" :label="t" :value="t" />
        </el-select>
      </el-form-item>
      <el-form-item label="物料名称" prop="materialName">
        <el-input v-model="queryParams.materialName" placeholder="名称" clearable style="width: 140px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd">新增物料</el-button>
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
      <el-table v-loading="loading" :data="bomList" border show-summary :summary-method="getSummary" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="50" align="center" />
            
        <el-table-column label="款号" prop="styleNo" width="110" />
        <el-table-column label="类型" prop="materialType" width="90" />
        <el-table-column label="物料名称" prop="materialName" min-width="140" />
        <el-table-column label="规格" prop="spec" min-width="120" show-overflow-tooltip />
        <el-table-column label="单位" prop="unit" width="70" align="center" />
        <el-table-column label="单件用量" prop="qtyPerPiece" width="100" align="right" />
        <el-table-column label="损耗率%" prop="lossRate" width="90" align="right" />
        <el-table-column label="单价" prop="unitPrice" width="90" align="right" />
        <el-table-column label="小计" prop="amount" width="100" align="right" />
        <el-table-column label="操作" class-name="op-cell" width="130" align="center" fixed="right">
          <template #default="s">
            <el-button link type="primary" @click="handleUpdate(s.row)">修改</el-button>
            <el-button link type="danger" @click="handleDelete(s.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="form.bomId ? '修改物料' : '新增物料'" v-model="open" width="620px" append-to-body>
      <el-form ref="bomRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="款号" prop="styleNo">
              <el-select v-model="form.styleNo" placeholder="选择款号" filterable style="width: 100%">
                <el-option v-for="s in styles" :key="s.styleNo" :label="s.styleNo + ' ' + s.cnName" :value="s.styleNo" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="物料类型">
              <el-select v-model="form.materialType" style="width: 100%">
                <el-option v-for="t in types" :key="t" :label="t" :value="t" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="物料名称" prop="materialName"><el-input v-model="form.materialName" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="规格"><el-input v-model="form.spec" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="单位"><el-input v-model="form.unit" placeholder="米/个/条" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="单件用量"><el-input-number v-model="form.qtyPerPiece" :min="0" :precision="3" controls-position="right" style="width: 100%" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="损耗率%"><el-input-number v-model="form.lossRate" :min="0" :precision="2" controls-position="right" style="width: 100%" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="单价"><el-input-number v-model="form.unitPrice" :min="0" :precision="2" controls-position="right" style="width: 100%" /></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注"><el-input v-model="form.remark" /></el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Bom">
import { listBom, addBom, updateBom, delBom, commonStyles } from '@/api/garment'

const showSearch = ref(true)

const ids = ref([])
const multiple = ref(true)

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.bomId)
  multiple.value = !selection.length
}

const { proxy } = getCurrentInstance()
const types = ['面料', '里布', '辅料', '配件']
const bomList = ref([])
const styles = ref([])
const loading = ref(false)
const total = ref(0)
const open = ref(false)
const form = ref({})
const queryParams = ref({ pageNum: 1, pageSize: 20, styleNo: undefined, materialType: undefined, materialName: undefined })
const rules = {
  styleNo: [{ required: true, message: '请选择款号', trigger: 'change' }],
  materialName: [{ required: true, message: '请输入物料名称', trigger: 'blur' }]
}

function getList() {
  loading.value = true
  listBom(queryParams.value).then(res => {
    bomList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}
function reset() {
  form.value = { styleNo: queryParams.value.styleNo, materialType: '面料', materialName: undefined, spec: undefined, unit: '米', qtyPerPiece: 0, lossRate: 0, unitPrice: 0, remark: undefined }
  proxy.resetForm('bomRef')
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
function handleAdd() { reset(); open.value = true }
function handleUpdate(row) { form.value = { ...row }; open.value = true }
function submitForm() {
  proxy.$refs['bomRef'].validate(valid => {
    if (!valid) return
    const fn = form.value.bomId ? updateBom : addBom
    fn(form.value).then(() => {
      proxy.$modal.msgSuccess('保存成功')
      open.value = false
      getList()
    })
  })
}
function handleDelete(row) {
  const _ids = row ? [row.bomId] : ids.value
  if (!_ids.length) return
  proxy.$modal.confirm(row ? '确认删除该物料？' : `是否确认删除选中的 ${_ids.length} 条数据？`).then(() => delBom(_ids)).then(() => {
    proxy.$modal.msgSuccess('删除成功')
    getList()
    ids.value = []
    multiple.value = true
  }).catch(() => {})
}
function getSummary({ columns, data }) {
  const sums = []
  columns.forEach((col, index) => {
    if (index === 0) { sums[index] = '合计'; return }
    if (col.property === 'amount') {
      sums[index] = data.reduce((s, r) => s + Number(r.amount || 0), 0).toFixed(2)
    } else {
      sums[index] = ''
    }
  })
  return sums
}
function handleExport() {
  proxy.download('garment/bom/export', { ...queryParams.value }, `bom_${Date.now()}.xlsx`)
}

getList()
commonStyles().then(res => { styles.value = res.data || [] })
</script>
