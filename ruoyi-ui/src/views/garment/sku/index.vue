<template>
  <div class="app-container garment-page">
    <el-form v-show="showSearch" class="search-form" :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item label="款号" prop="styleNo">
        <el-input v-model="queryParams.styleNo" placeholder="款号" clearable style="width: 140px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="品名" prop="cnName">
        <el-input v-model="queryParams.cnName" placeholder="品名" clearable style="width: 140px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="颜色" prop="color">
        <el-select v-model="queryParams.color" placeholder="颜色" clearable style="width: 140px">
          <el-option v-for="c in colors" :key="c.cnName" :label="c.cnName" :value="c.cnName">
            <div class="color-opt"><span>{{ c.cnName }}</span><span class="color-opt-chip" :style="{ background: c.colorCode }"></span></div>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="尺码" prop="size">
        <el-select v-model="queryParams.size" placeholder="尺码" clearable style="width: 100px">
          <el-option v-for="s in sizes" :key="s.sizeName" :label="s.sizeName" :value="s.sizeName" />
        </el-select>
      </el-form-item>
      <el-form-item label="条码" prop="barcode">
        <el-input v-model="queryParams.barcode" placeholder="条码" clearable style="width: 150px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['garment:sku:add']">新增SKU</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="MagicStick" @click="generateOpen = true" v-hasPermi="['garment:sku:generate']">批量生成</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-upload :show-file-list="false" :http-request="handleImport" accept=".xlsx,.xls">
          <el-button plain icon="Upload" v-hasPermi="['garment:sku:import']">导入初始库存</el-button>
        </el-upload>
      </el-col>
      <el-col :span="1.5">
        <el-button plain icon="Download" v-hasPermi="['garment:sku:template']" @click="downloadTemplate">下载模板</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button plain icon="Download" v-hasPermi="['garment:sku:export']" @click="handleExport">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['garment:sku:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <div class="table-wrap">
      <el-table v-loading="loading" :data="skuList" border @selection-change="handleSelectionChange">
            <el-table-column v-if="checkPermi(['garment:sku:remove'])" type="selection" width="50" align="center" />
            
        <el-table-column label="款号" prop="styleNo" min-width="100" fixed="left" />
        <el-table-column label="品名" prop="cnName" min-width="140" show-overflow-tooltip />
        <el-table-column label="颜色" prop="color" width="100" />
        <el-table-column label="尺码" prop="size" width="70" align="center" />
        <el-table-column label="条码" prop="barcode" min-width="160" show-overflow-tooltip />
        <el-table-column label="成本价" prop="costPrice" width="90" align="right" />
        <el-table-column label="销售价" prop="salePrice" width="90" align="right" />
        <el-table-column label="最低库存" prop="minStock" width="90" align="center" />
        <el-table-column label="裁片" prop="cutQty" width="70" align="center">
          <template #default="s"><span class="tag-red">{{ s.row.cutQty }}</span></template>
        </el-table-column>
        <el-table-column label="产线" prop="lineQty" width="70" align="center">
          <template #default="s"><span class="tag-blue">{{ s.row.lineQty }}</span></template>
        </el-table-column>
        <el-table-column label="成品" prop="finishQty" width="70" align="center">
          <template #default="s"><strong>{{ s.row.finishQty }}</strong></template>
        </el-table-column>
        <el-table-column v-if="checkPermi(['garment:sku:edit','garment:sku:remove'])" label="操作" class-name="op-cell" width="140" align="center" fixed="right">
          <template #default="s">
            <el-button link type="primary" @click="handleUpdate(s.row)" v-hasPermi="['garment:sku:edit']">修改</el-button>
            <el-button link type="danger" @click="handleDelete(s.row)" v-hasPermi="['garment:sku:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="form.skuId ? '修改SKU' : '新增SKU'" v-model="open" width="620px" append-to-body>
      <el-form ref="skuRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="款号" prop="styleNo">
              <el-select v-model="form.styleNo" placeholder="选择款号" filterable style="width: 100%" @change="onStyleChange">
                <el-option v-for="s in styles" :key="s.styleNo" :label="s.styleNo + ' ' + s.cnName" :value="s.styleNo" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="中文品名"><el-input v-model="form.cnName" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="颜色" prop="color">
              <el-select v-model="form.color" placeholder="颜色" filterable allow-create style="width: 100%">
                <el-option v-for="c in colors" :key="c.cnName" :label="c.cnName" :value="c.cnName">
                  <div class="color-opt"><span>{{ c.cnName }}</span><span class="color-opt-chip" :style="{ background: c.colorCode }"></span></div>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="尺码" prop="size">
              <el-select v-model="form.size" placeholder="尺码" filterable allow-create style="width: 100%">
                <el-option v-for="s in sizes" :key="s.sizeName" :label="s.sizeName" :value="s.sizeName" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="条码"><el-input v-model="form.barcode" placeholder="默认 款号-颜色-尺码" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="最低库存"><el-input-number v-model="form.minStock" :min="0" controls-position="right" style="width: 100%" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="成本价"><el-input-number v-model="form.costPrice" :min="0" :precision="2" controls-position="right" style="width: 100%" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="销售价"><el-input-number v-model="form.salePrice" :min="0" :precision="2" controls-position="right" style="width: 100%" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio value="0">正常</el-radio>
                <el-radio value="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button type="primary" v-hasPermi="['garment:sku:add','garment:sku:edit']" @click="submitForm">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </template>
    </el-dialog>

    <el-dialog title="批量生成SKU" v-model="generateOpen" width="560px" append-to-body>
      <el-form :model="genForm" label-width="80px">
        <el-form-item label="款号" required>
          <el-select v-model="genForm.styleNo" placeholder="选择款号" filterable style="width: 100%">
            <el-option v-for="s in styles" :key="s.styleNo" :label="s.styleNo + ' ' + s.cnName" :value="s.styleNo" />
          </el-select>
        </el-form-item>
        <el-form-item label="颜色" required>
          <el-select v-model="genForm.colors" multiple collapse-tags placeholder="选择颜色" style="width: 100%">
            <el-option v-for="c in colors" :key="c.cnName" :label="c.cnName" :value="c.cnName">
              <div class="color-opt"><span>{{ c.cnName }}</span><span class="color-opt-chip" :style="{ background: c.colorCode }"></span></div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="尺码" required>
          <el-select v-model="genForm.sizes" multiple collapse-tags placeholder="选择尺码" style="width: 100%">
            <el-option v-for="s in sizes" :key="s.sizeName" :label="s.sizeName" :value="s.sizeName" />
          </el-select>
        </el-form-item>
        <el-form-item label="提示">
          <span class="muted">已存在的 SKU 会自动跳过，仅生成新的颜色尺码组合。</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" v-hasPermi="['garment:sku:generate']" @click="submitGenerate">开始生成</el-button>
        <el-button @click="generateOpen = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Sku">
import { saveAs } from 'file-saver'
import { listSku, getSku, addSku, updateSku, delSku, generateSku, importStock, stockTemplate, commonStyles, commonColors, commonSizes } from '@/api/garment'

const { proxy } = getCurrentInstance()
const skuList = ref([])
const styles = ref([])
const colors = ref([])
const sizes = ref([])
const open = ref(false)
const generateOpen = ref(false)
const loading = ref(false)
const showSearch = ref(true)

const ids = ref([])
const multiple = ref(true)

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.skuId)
  multiple.value = !selection.length
}
const total = ref(0)
const queryParams = ref({ pageNum: 1, pageSize: 10, styleNo: undefined, cnName: undefined, color: undefined, size: undefined, barcode: undefined })
const form = ref({})
const genForm = ref({ styleNo: undefined, colors: [], sizes: [] })
const rules = {
  styleNo: [{ required: true, message: '请选择款号', trigger: 'change' }],
  color: [{ required: true, message: '请选择颜色', trigger: 'change' }],
  size: [{ required: true, message: '请选择尺码', trigger: 'change' }]
}

function getList() {
  loading.value = true
  listSku(queryParams.value).then(res => {
    skuList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}
function loadOptions() {
  commonStyles().then(res => { styles.value = res.data || [] })
  commonColors().then(res => { colors.value = res.data || [] })
  commonSizes().then(res => { sizes.value = res.data || [] })
}
function reset() {
  form.value = { styleNo: undefined, cnName: undefined, color: undefined, size: undefined, barcode: undefined, costPrice: 0, salePrice: 0, minStock: 0, status: '0' }
  proxy.resetForm('skuRef')
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
function handleAdd() { reset(); open.value = true }
function handleUpdate(row) {
  reset()
  getSku(row.skuId).then(res => { form.value = res.data; open.value = true })
}
function onStyleChange(styleNo) {
  const style = styles.value.find(s => s.styleNo === styleNo)
  if (style) form.value.cnName = style.cnName
}
function submitForm() {
  proxy.$refs['skuRef'].validate(valid => {
    if (!valid) return
    if (!form.value.barcode) form.value.barcode = `${form.value.styleNo}-${form.value.color}-${form.value.size}`
    const fn = form.value.skuId ? updateSku : addSku
    fn(form.value).then(() => {
      proxy.$modal.msgSuccess('保存成功')
      open.value = false
      getList()
    })
  })
}
function submitGenerate() {
  if (!genForm.value.styleNo || !genForm.value.colors.length || !genForm.value.sizes.length) {
    proxy.$modal.msgWarning('请选择款号、颜色和尺码')
    return
  }
  generateSku(genForm.value).then(res => {
    proxy.$modal.msgSuccess(res.msg || '生成完成')
    generateOpen.value = false
    getList()
  })
}
function handleDelete(row) {
  const _ids = row ? [row.skuId] : ids.value
  if (!_ids.length) return
  proxy.$modal.confirm(row ? '确认删除该 SKU？' : `是否确认删除选中的 ${_ids.length} 条数据？`).then(() => delSku(_ids)).then(() => {
    proxy.$modal.msgSuccess('删除成功')
    getList()
    ids.value = []
    multiple.value = true
  }).catch(() => {})
}
function handleImport(file) {
  importStock(file.file).then(res => {
    proxy.$modal.msgSuccess(res.msg || '导入成功')
    getList()
  }).catch(() => {})
}
function downloadTemplate() {
  stockTemplate().then(data => {
    saveAs(new Blob([data]), '初始库存导入模板.xlsx')
  })
}
function handleExport() {
  proxy.download('garment/sku/export', { ...queryParams.value }, `sku_${Date.now()}.xlsx`)
}

getList()
loadOptions()

// 从标签页切回本页时自动刷新数据，避免展示缓存中的旧列表
let _pageActivated = false
onActivated(() => {
  if (_pageActivated) {
    getList()
    loadOptions()
  } else {
    _pageActivated = true
  }
})

</script>
