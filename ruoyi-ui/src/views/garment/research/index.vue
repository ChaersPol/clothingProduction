<template>
  <div class="app-container garment-page">
    <el-form v-show="showSearch" class="search-form" :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item label="款号" prop="styleNo">
        <el-input v-model="queryParams.styleNo" placeholder="款号" clearable style="width: 140px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="品名" prop="cnName">
        <el-input v-model="queryParams.cnName" placeholder="品名" clearable style="width: 150px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="设计人" prop="designer">
        <el-select v-model="queryParams.designer" placeholder="全部" clearable style="width: 130px">
          <el-option v-for="e in employees" :key="e.employeeId" :label="e.name" :value="e.name" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['garment:style:add']">新增选品</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['garment:style:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <div class="table-wrap">
      <el-table v-loading="loading" :data="styleList" border @selection-change="handleSelectionChange">
            <el-table-column v-if="checkPermi(['garment:style:remove'])" type="selection" width="50" align="center" />
            
        <el-table-column label="款号" prop="styleNo" min-width="100" />
        <el-table-column label="品名" prop="cnName" min-width="140" show-overflow-tooltip />
        <el-table-column label="品类" prop="category" width="90" />
        <el-table-column label="季节" prop="season" width="80" />
        <el-table-column label="设计人" prop="designer" width="90" />
        <el-table-column label="尺码表" prop="sizeTable" width="110" show-overflow-tooltip />
        <el-table-column label="预计卖价" prop="expectPrice" width="100" align="right" />
        <el-table-column label="设计/修改记录" prop="designRemark" min-width="200" show-overflow-tooltip />
        <el-table-column v-if="checkPermi(['garment:style:design','garment:style:edit','garment:sku:add','garment:style:remove'])" label="操作" class-name="op-cell" width="270" align="center" fixed="right">
          <template #default="s">
            <el-button link type="primary" @click="openDesign(s.row)" v-hasPermi="['garment:style:design']">设计资料</el-button>
            <el-button link type="primary" @click="handleUpdate(s.row)" v-hasPermi="['garment:style:edit']">编辑</el-button>
            <el-button link type="success" @click="openGenerate(s.row)" v-hasPermi="['garment:sku:add']">生成商品</el-button>
            <el-button link type="danger" @click="handleDelete(s.row)" v-hasPermi="['garment:style:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 设计资料（图片/纸样/记录） -->
    <el-dialog title="研发设计资料" v-model="open" width="720px" append-to-body>
      <el-form :model="form" label-width="90px">
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="款号"><el-input v-model="form.styleNo" disabled /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="品名"><el-input v-model="form.cnName" disabled /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="设计人">
              <el-select v-model="form.designer" placeholder="选择设计人" clearable style="width: 100%">
                <el-option v-for="e in employees" :key="e.employeeId" :label="e.name" :value="e.name" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="预计卖价"><el-input-number v-model="form.expectPrice" :min="0" :precision="2" controls-position="right" style="width: 100%" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="参考图片"><image-upload v-model="form.refImage" :limit="1" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="纸样文件"><file-upload v-model="form.patternFile" :limit="1" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="样衣照片"><image-upload v-model="form.sampleImage" :limit="1" /></el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="款式图片"><image-upload v-model="form.image" :limit="1" /></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="设计记录">
              <el-input v-model="form.designRemark" type="textarea" :rows="4" placeholder="打版、样衣、修改意见等" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button type="primary" v-hasPermi="['garment:style:design']" @click="submit">保 存</el-button>
        <el-button @click="open = false">取 消</el-button>
      </template>
    </el-dialog>

    <!-- 新增/编辑选品 -->
    <el-dialog :title="form2.styleId ? '编辑选品' : '新增选品'" v-model="open2" width="720px" append-to-body>
      <el-form ref="styleRef" :model="form2" :rules="rules" label-width="90px">
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="款号" prop="styleNo">
              <el-input v-model="form2.styleNo" placeholder="如 JT05" :disabled="!!form2.styleId" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="父体款号">
              <el-input v-model="form2.parentStyleNo" placeholder="留空默认同款号" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="中文品名" prop="cnName">
              <el-input v-model="form2.cnName" placeholder="如 卡扣束腰大摆裙" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="英文名称">
              <el-input v-model="form2.styleName" placeholder="英文/别名" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="品类">
              <el-input v-model="form2.category" placeholder="女装/连衣裙" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="季节">
              <el-select v-model="form2.season" placeholder="选择季节" clearable style="width: 100%">
                <el-option label="春季" value="春季" />
                <el-option label="夏季" value="夏季" />
                <el-option label="秋季" value="秋季" />
                <el-option label="冬季" value="冬季" />
                <el-option label="四季" value="四季" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="设计人">
              <el-select v-model="form2.designer" placeholder="选择设计人" clearable style="width: 100%">
                <el-option v-for="e in employees" :key="e.employeeId" :label="e.name" :value="e.name" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="预计卖价">
              <el-input-number v-model="form2.expectPrice" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="尺码表">
              <el-input v-model="form2.sizeTable" placeholder="如 S,M,L,XL" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form2.status">
                <el-radio value="0">正常</el-radio>
                <el-radio value="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="参考图片">
              <image-upload v-model="form2.refImage" :limit="1" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="款式图片">
              <image-upload v-model="form2.image" :limit="1" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="设计记录">
              <el-input v-model="form2.designRemark" type="textarea" :rows="3" placeholder="选品说明、打版/样衣/修改意见" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button type="primary" v-hasPermi="['garment:style:edit']" @click="submitStyle">保 存</el-button>
        <el-button @click="open2 = false">取 消</el-button>
      </template>
    </el-dialog>

    <!-- 一键生成商品 -->
    <el-dialog title="一键生成商品" v-model="generateOpen" width="540px" append-to-body>
      <el-alert type="info" :closable="false" show-icon style="margin-bottom: 12px"
        title="按选择的颜色 × 尺码自动生成 SKU，成本价取自成本核算，已存在的组合自动跳过。" />
      <el-form label-width="80px">
        <el-form-item label="款号">
          <el-input v-model="genForm.styleNo" disabled />
        </el-form-item>
        <el-form-item label="颜色" required>
          <el-select v-model="genForm.colors" multiple collapse-tags placeholder="选择颜色" style="width: 100%">
            <el-option v-for="c in colors" :key="c.colorId" :label="c.cnName" :value="c.cnName">
              <div class="color-opt"><span>{{ c.cnName }}</span><span class="color-opt-chip" :style="{ background: c.colorCode }"></span></div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="尺码" required>
          <el-select v-model="genForm.sizes" multiple collapse-tags placeholder="选择尺码" style="width: 100%">
            <el-option v-for="s in sizes" :key="s.sizeId" :label="s.sizeName" :value="s.sizeName" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" v-hasPermi="['garment:sku:add']" @click="submitGenerate">开始生成</el-button>
        <el-button @click="generateOpen = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Research">
import { listStyle, updateStyle, updateStyleDesign, addStyle, delStyle, generateSku, commonColors, commonSizes, commonEmployees } from '@/api/garment'

const showSearch = ref(true)

const ids = ref([])
const multiple = ref(true)

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.styleId)
  multiple.value = !selection.length
}

const { proxy } = getCurrentInstance()
const styleList = ref([])
const loading = ref(false)
const total = ref(0)
const open = ref(false)
const open2 = ref(false)
const generateOpen = ref(false)
const form = ref({})
const form2 = ref({})
const colors = ref([])
const sizes = ref([])
const employees = ref([])
const genForm = ref({ styleNo: undefined, colors: [], sizes: [] })
const queryParams = ref({ pageNum: 1, pageSize: 10, styleNo: undefined, cnName: undefined, designer: undefined })
const rules = {
  styleNo: [{ required: true, message: '请输入款号', trigger: 'blur' }],
  cnName: [{ required: true, message: '请输入中文品名', trigger: 'blur' }]
}

function getList() {
  loading.value = true
  listStyle(queryParams.value).then(res => {
    styleList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
function openDesign(row) {
  form.value = { ...row }
  open.value = true
}
function submit() {
  updateStyleDesign(form.value).then(() => {
    proxy.$modal.msgSuccess('保存成功')
    open.value = false
    getList()
  })
}
function handleAdd() {
  form2.value = { status: '0', expectPrice: 0 }
  open2.value = true
}
function handleUpdate(row) {
  form2.value = { ...row }
  open2.value = true
}
function submitStyle() {
  proxy.$refs['styleRef'].validate(valid => {
    if (!valid) return
    const fn = form2.value.styleId ? updateStyle : addStyle
    fn(form2.value).then(() => {
      proxy.$modal.msgSuccess('保存成功')
      open2.value = false
      getList()
    })
  })
}
function handleDelete(row) {
  const _ids = row ? [row.styleId] : ids.value
  if (!_ids.length) return
  proxy.$modal.confirm(row ? `确认删除选品「${row.styleNo} ${row.cnName || ''}」吗？` : `是否确认删除选中的 ${_ids.length} 条数据？`).then(() => delStyle(_ids)).then(() => {
    proxy.$modal.msgSuccess('删除成功')
    getList()
    ids.value = []
    multiple.value = true
  }).catch(() => {})
}
function openGenerate(row) {
  genForm.value = { styleNo: row.styleNo, colors: [], sizes: [] }
  if (!colors.value.length) {
    commonColors().then(res => { colors.value = res.data || [] })
  }
  if (!sizes.value.length) {
    commonSizes().then(res => { sizes.value = res.data || [] })
  }
  generateOpen.value = true
}
function submitGenerate() {
  if (!genForm.value.colors.length || !genForm.value.sizes.length) {
    proxy.$modal.msgWarning('请选择颜色和尺码')
    return
  }
  generateSku(genForm.value).then(res => {
    proxy.$modal.msgSuccess(res.msg || '生成完成')
    generateOpen.value = false
    getList()
  })
}

getList()
commonEmployees().then(res => { employees.value = res.data || [] })

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
