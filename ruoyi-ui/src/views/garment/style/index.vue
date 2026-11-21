<template>
  <div class="app-container garment-page">
    <el-form v-show="showSearch" class="search-form" :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item label="款号" prop="styleNo">
        <el-input v-model="queryParams.styleNo" placeholder="请输入款号" clearable style="width: 160px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="品名" prop="cnName">
        <el-input v-model="queryParams.cnName" placeholder="请输入品名" clearable style="width: 160px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="品类" prop="category">
        <el-input v-model="queryParams.category" placeholder="品类" clearable style="width: 130px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd">新增款号</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button plain icon="Download" @click="handleExport">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <div class="table-wrap">
      <el-table v-loading="loading" :data="styleList" @selection-change="handleSelectionChange" border>
        <el-table-column type="selection" width="45" align="center" />
        <el-table-column label="款号" prop="styleNo" min-width="100" />
        <el-table-column label="品名" prop="cnName" min-width="150" show-overflow-tooltip />
        <el-table-column label="品类" prop="category" width="90" />
        <el-table-column label="季节" prop="season" width="80" />
        <el-table-column label="设计人" prop="designer" width="90" />
        <el-table-column label="尺码表" prop="sizeTable" min-width="130" show-overflow-tooltip />
        <el-table-column label="预计卖价" prop="expectPrice" width="100" align="right" />
        <el-table-column label="SKU数" prop="skuCount" width="90" align="center" />
        <el-table-column label="状态" prop="status" width="90" align="center">
          <template #default="s">
            <el-tag :type="s.row.status === '0' ? 'success' : 'info'" size="small">{{ s.row.status === '0' ? '正常' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" class-name="op-cell" width="150" align="center" fixed="right">
          <template #default="s">
            <el-button link type="primary" @click="handleUpdate(s.row)">修改</el-button>
            <el-button link type="danger" @click="handleDelete(s.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="820px" append-to-body>
      <el-form ref="styleRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="款号" prop="styleNo">
              <el-input v-model="form.styleNo" placeholder="如 JT03" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="父体款号" prop="parentStyleNo">
              <el-input v-model="form.parentStyleNo" placeholder="用于多颜色合并统计" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="中文品名" prop="cnName">
              <el-input v-model="form.cnName" placeholder="请输入中文品名" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="英文名称" prop="styleName">
              <el-input v-model="form.styleName" placeholder="请输入英文名称" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="品类" prop="category">
              <el-input v-model="form.category" placeholder="连衣裙/上衣/裤装" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="季节" prop="season">
              <el-input v-model="form.season" placeholder="春夏/秋季/冬季" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="设计人" prop="designer">
              <el-select v-model="form.designer" placeholder="选择设计人" clearable style="width: 100%">
                <el-option v-for="e in employees" :key="e.employeeId" :label="e.name" :value="e.name" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="尺码表" prop="sizeTable">
              <el-input v-model="form.sizeTable" placeholder="S,M,L,XL,XXL" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="预计卖价" prop="expectPrice">
              <el-input-number v-model="form.expectPrice" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio value="0">正常</el-radio>
                <el-radio value="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="款式图片" prop="image">
              <image-upload v-model="form.image" :limit="1" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="参考图片" prop="refImage">
              <image-upload v-model="form.refImage" :limit="1" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="设计备注" prop="designRemark">
              <el-input v-model="form.designRemark" type="textarea" :rows="3" placeholder="纸样、样衣、修改记录等" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Style">
import { listStyle, getStyle, addStyle, updateStyle, delStyle, commonEmployees } from '@/api/garment'

const { proxy } = getCurrentInstance()
const styleList = ref([])
const employees = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref('')

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, styleNo: undefined, cnName: undefined, category: undefined, status: undefined },
  rules: {
    styleNo: [{ required: true, message: '款号不能为空', trigger: 'blur' }],
    cnName: [{ required: true, message: '中文品名不能为空', trigger: 'blur' }]
  }
})
const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listStyle(queryParams.value).then(res => {
    styleList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}
function cancel() { open.value = false; reset() }
function reset() {
  form.value = { styleNo: undefined, parentStyleNo: undefined, cnName: undefined, styleName: undefined, category: undefined, season: undefined, designer: undefined, sizeTable: 'S,M,L,XL,XXL', expectPrice: 0, status: '0', image: undefined, refImage: undefined, patternFile: undefined, sampleImage: undefined, designRemark: undefined }
  proxy.resetForm('styleRef')
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
function handleSelectionChange(selection) {
  ids.value = selection.map(i => i.styleId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}
function handleAdd() { reset(); open.value = true; title.value = '新增款号' }
function handleUpdate(row) {
  reset()
  const id = row.styleId || ids.value[0]
  getStyle(id).then(res => { form.value = res.data; open.value = true; title.value = '修改款号' })
}
function submitForm() {
  proxy.$refs['styleRef'].validate(valid => {
    if (!valid) return
    const fn = form.value.styleId ? updateStyle : addStyle
    fn(form.value).then(() => {
      proxy.$modal.msgSuccess(form.value.styleId ? '修改成功' : '新增成功')
      open.value = false
      getList()
    })
  })
}
function handleDelete(row) {
  const list = row.styleId ? [row.styleId] : ids.value
  proxy.$modal.confirm('是否确认删除选中的款号？删除后不可恢复。').then(() => delStyle(list)).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}
function handleExport() {
  proxy.download('garment/style/export', { ...queryParams.value }, `style_${Date.now()}.xlsx`)
}

getList()
commonEmployees().then(res => { employees.value = res.data || [] })
</script>
