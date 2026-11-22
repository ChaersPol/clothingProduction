<template>
  <div class="app-container garment-page">
    <el-form v-show="showSearch" class="search-form" :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item label="款号" prop="styleNo">
        <el-input v-model="queryParams.styleNo" placeholder="款号" clearable style="width: 160px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['garment:cost:add']">新增成本核算</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['garment:cost:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <div class="table-wrap">
      <el-table v-loading="loading" :data="costList" border :default-sort="{ prop: 'totalCost', order: 'descending' }" @selection-change="handleSelectionChange">
        <el-table-column v-if="checkPermi(['garment:cost:remove'])" type="selection" width="50" align="center" />
        <el-table-column label="款号" prop="styleNo" width="110" />
        <el-table-column label="品名" prop="cnName" min-width="150" show-overflow-tooltip />
        <el-table-column label="面料" prop="fabricCost" width="80" align="right" />
        <el-table-column label="里布" prop="liningCost" width="80" align="right" />
        <el-table-column label="辅料" prop="accessoryCost" width="80" align="right" />
        <el-table-column label="配件" prop="trimCost" width="80" align="right" />
        <el-table-column label="加工费" prop="laborCost" width="90" align="right" />
        <el-table-column label="合计成本" prop="totalCost" width="100" align="right" sortable>
          <template #default="s"><span class="money">{{ s.row.totalCost }}</span></template>
        </el-table-column>
        <el-table-column label="出厂价" prop="exFactoryPrice" width="100" align="right" />
        <el-table-column label="建议售价" prop="suggestPrice" width="100" align="right" />
        <el-table-column v-if="checkPermi(['garment:cost:edit','garment:cost:remove'])" label="操作" class-name="op-cell" width="130" align="center" fixed="right">
          <template #default="s">
            <el-button link type="primary" @click="openCost(s.row)" v-hasPermi="['garment:cost:edit']">核算</el-button>
            <el-button link type="danger" @click="handleDelete(s.row)" v-hasPermi="['garment:cost:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="form.costId ? '成本核算 - ' + form.styleNo : '新增成本核算'" v-model="open" width="760px" append-to-body>
      <el-form ref="costRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="款号" prop="styleNo">
              <el-select v-model="form.styleNo" placeholder="选择款号" filterable style="width: 100%" :disabled="!!form.costId" @change="onStyleChange">
                <el-option v-for="s in styles" :key="s.styleNo" :label="s.styleNo + ' ' + s.cnName" :value="s.styleNo" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="合计成本">
              <el-input :model-value="previewTotal" disabled />
            </el-form-item>
          </el-col>
          <el-col v-for="f in costFields" :key="f.key" :xs="24" :sm="12">
            <el-form-item :label="f.label">
              <el-input-number v-model="form[f.key]" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="出厂价">
              <el-input-number v-model="form.exFactoryPrice" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="建议售价">
              <el-input-number v-model="form.suggestPrice" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注"><el-input v-model="form.remark" /></el-form-item>
          </el-col>
        </el-row>
        <el-alert type="info" :closable="false" show-icon title="保存后会自动同步该款所有 SKU 的成本价与销售价；出厂价、建议售价留空时系统按成本自动计算。" />
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">保存并同步SKU</el-button>
        <el-button @click="open = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Cost">
import { listCost, saveCost, delCost, commonStyles } from '@/api/garment'

const showSearch = ref(true)
const ids = ref([])
const multiple = ref(true)

const { proxy } = getCurrentInstance()
const costFields = [
  { key: 'fabricCost', label: '面料成本' },
  { key: 'liningCost', label: '里布成本' },
  { key: 'accessoryCost', label: '辅料成本' },
  { key: 'trimCost', label: '配件成本' },
  { key: 'laborCost', label: '加工费' },
  { key: 'patternCost', label: '打版费' },
  { key: 'shootCost', label: '拍摄费' },
  { key: 'operateCost', label: '运营费' },
  { key: 'firstFreight', label: '头程运费' },
  { key: 'lastFreight', label: '尾程运费' },
  { key: 'seaFreight', label: '海运费' },
  { key: 'otherCost', label: '其他费用' }
]
const costList = ref([])
const styles = ref([])
const loading = ref(false)
const total = ref(0)
const open = ref(false)
const form = ref({})
const queryParams = ref({ pageNum: 1, pageSize: 10, styleNo: undefined })
const rules = { styleNo: [{ required: true, message: '请选择款号', trigger: 'change' }] }

const previewTotal = computed(() => {
  return costFields.reduce((sum, f) => sum + Number(form.value[f.key] || 0), 0).toFixed(2)
})

function getList() {
  loading.value = true
  listCost(queryParams.value).then(res => {
    costList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
function handleSelectionChange(selection) {
  ids.value = selection.map(i => i.costId)
  multiple.value = !selection.length
}
function handleDelete(row) {
  const delIds = row && row.costId ? [row.costId] : ids.value
  if (!delIds.length) return
  proxy.$modal.confirm('确认删除选中的成本核算记录吗？').then(() => {
    Promise.all(delIds.map(id => delCost(id))).then(() => {
      proxy.$modal.msgSuccess('删除成功')
      getList()
    })
  }).catch(() => {})
}
function emptyForm() {
  const obj = { styleNo: undefined, exFactoryPrice: 0, suggestPrice: 0, remark: undefined }
  costFields.forEach(f => { obj[f.key] = 0 })
  return obj
}
function handleAdd() { form.value = emptyForm(); open.value = true }
function openCost(row) { form.value = { ...emptyForm(), ...row }; open.value = true }
function onStyleChange(styleNo) {
  const s = styles.value.find(i => i.styleNo === styleNo)
  if (s && s.expectPrice) form.value.suggestPrice = s.expectPrice
}
function submitForm() {
  proxy.$refs['costRef'].validate(valid => {
    if (!valid) return
    saveCost(form.value).then(() => {
      proxy.$modal.msgSuccess('保存成功，已同步SKU价格')
      open.value = false
      getList()
    })
  })
}

getList()
commonStyles().then(res => { styles.value = res.data || [] })

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
