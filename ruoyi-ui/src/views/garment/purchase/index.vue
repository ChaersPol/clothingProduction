<template>
  <div class="app-container garment-page">
    <el-form v-show="showSearch" class="search-form" :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item label="采购单号" prop="purchaseNo">
        <el-input v-model="queryParams.purchaseNo" placeholder="单号" clearable style="width: 150px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="类型" prop="purchaseType">
        <el-select v-model="queryParams.purchaseType" placeholder="全部" clearable style="width: 130px">
          <el-option label="公用耗材" value="COMMON" />
          <el-option label="按款物料" value="MATERIAL" />
        </el-select>
      </el-form-item>
      <el-form-item label="供应商" prop="supplierId">
        <el-select v-model="queryParams.supplierId" placeholder="全部" clearable filterable style="width: 170px">
          <el-option v-for="s in suppliers" :key="s.supplierId" :label="s.supplierName" :value="s.supplierId" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
          <el-option label="待入库" value="0" />
          <el-option label="部分入库" value="1" />
          <el-option label="已入库" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd">新增采购单</el-button>
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
      <el-table v-loading="loading" :data="purchaseList" border @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="50" align="center" />
            
        <el-table-column label="采购单号" prop="purchaseNo" width="150" fixed="left" />
        <el-table-column label="类型" prop="purchaseType" width="100" align="center">
          <template #default="s">
            <el-tag size="small" :type="s.row.purchaseType === 'MATERIAL' ? 'warning' : 'info'">
              {{ s.row.purchaseType === 'MATERIAL' ? '按款物料' : '公用耗材' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="类别" prop="category" width="90" />
        <el-table-column label="款号" prop="styleNo" width="100" />
        <el-table-column label="供应商" prop="supplierName" min-width="140" show-overflow-tooltip />
        <el-table-column label="采购日期" prop="purchaseDate" width="110" />
        <el-table-column label="金额" prop="totalAmount" width="100" align="right" />
        <el-table-column label="已付" prop="paidAmount" width="90" align="right" />
        <el-table-column label="状态" prop="status" width="95" align="center">
          <template #default="s">
            <el-tag size="small" :type="s.row.status === '2' ? 'success' : s.row.status === '1' ? 'warning' : 'danger'">
              {{ statusText(s.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" class-name="op-cell" width="220" align="center" fixed="right">
          <template #default="s">
            <el-button link type="primary" @click="viewDetail(s.row)">明细</el-button>
            <el-button link type="success" @click="openReceive(s.row)">入库</el-button>
            <el-button link type="primary" @click="handleUpdate(s.row)">修改</el-button>
            <el-button link type="danger" @click="handleDelete(s.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改 -->
    <el-dialog :title="form.purchaseId ? '修改采购单' : '新增采购单'" v-model="open" width="900px" append-to-body>
      <el-form ref="purchaseRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="12">
          <el-col :xs="24" :sm="8">
            <el-form-item label="采购类型">
              <el-radio-group v-model="form.purchaseType">
                <el-radio value="COMMON">公用耗材</el-radio>
                <el-radio value="MATERIAL">按款物料</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="类别">
              <el-select v-model="form.category" placeholder="选择" style="width: 100%">
                <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="款号">
              <el-select v-model="form.styleNo" placeholder="按款物料时选择" clearable filterable style="width: 100%" @change="onStyleChange">
                <el-option v-for="s in styles" :key="s.styleNo" :label="s.styleNo + ' ' + s.cnName" :value="s.styleNo" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="供应商">
              <el-select v-model="form.supplierId" placeholder="选择供应商" clearable filterable style="width: 100%">
                <el-option v-for="s in suppliers" :key="s.supplierId" :label="s.supplierName" :value="s.supplierId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="采购日期">
              <el-date-picker v-model="form.purchaseDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="已付金额">
              <el-input-number v-model="form.paidAmount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <div class="toolbar">
          <span class="page-title" style="margin: 0">采购明细</span>
          <el-button type="primary" plain size="small" icon="Plus" @click="addItem">添加明细</el-button>
        </div>
        <el-table :data="form.items" border size="small">
          <el-table-column label="物料类型" width="110">
            <template #default="s">
              <el-select v-model="s.row.materialType" size="small">
                <el-option v-for="t in materialTypes" :key="t" :label="t" :value="t" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="名称" min-width="140">
            <template #default="s"><el-input v-model="s.row.materialName" size="small" /></template>
          </el-table-column>
          <el-table-column label="规格" width="110">
            <template #default="s"><el-input v-model="s.row.spec" size="small" /></template>
          </el-table-column>
          <el-table-column label="单位" width="80">
            <template #default="s"><el-input v-model="s.row.unit" size="small" /></template>
          </el-table-column>
          <el-table-column label="单价" width="110">
            <template #default="s"><el-input-number v-model="s.row.price" :min="0" :precision="2" size="small" controls-position="right" style="width: 100%" /></template>
          </el-table-column>
          <el-table-column label="数量" width="110">
            <template #default="s"><el-input-number v-model="s.row.qty" :min="0" :precision="2" size="small" controls-position="right" style="width: 100%" /></template>
          </el-table-column>
          <el-table-column label="金额" width="90" align="right">
            <template #default="s">{{ itemAmount(s.row) }}</template>
          </el-table-column>
          <el-table-column label="操作" class-name="op-cell" width="60" align="center">
            <template #default="s"><el-button link type="danger" @click="form.items.splice(s.$index, 1)">删除</el-button></template>
          </el-table-column>
        </el-table>
        <div class="muted" style="text-align: right; margin-top: 8px">合计：<span class="money">¥{{ formTotal }}</span></div>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </template>
    </el-dialog>

    <!-- 明细查看 -->
    <el-dialog title="采购明细" v-model="detailOpen" width="760px" append-to-body>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="采购单号">{{ detail.purchaseNo }}</el-descriptions-item>
        <el-descriptions-item label="供应商">{{ detail.supplierName }}</el-descriptions-item>
        <el-descriptions-item label="采购日期">{{ detail.purchaseDate }}</el-descriptions-item>
        <el-descriptions-item label="合计金额">¥{{ detail.totalAmount }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="detail.items || []" border size="small" style="margin-top: 12px">
        <el-table-column label="类型" prop="materialType" width="90" />
        <el-table-column label="名称" prop="materialName" min-width="140" />
        <el-table-column label="规格" prop="spec" width="100" />
        <el-table-column label="单价" prop="price" width="80" align="right" />
        <el-table-column label="数量" prop="qty" width="80" align="right" />
        <el-table-column label="已入库" prop="receivedQty" width="80" align="right" />
        <el-table-column label="金额" prop="amount" width="90" align="right" />
      </el-table>
    </el-dialog>

    <!-- 入库 -->
    <el-dialog title="采购入库" v-model="receiveOpen" width="560px" append-to-body>
      <el-alert type="success" :closable="false" show-icon style="margin-bottom: 12px"
        title="确认入库后将自动增加对应物资库存并生成「采购入库」流水，无需再到仓库手工登记。" />
      <el-form label-width="90px">
        <el-form-item label="采购单号">{{ receiveForm.purchaseNo }}</el-form-item>
        <el-form-item label="入库明细">
          <el-select v-model="receiveForm.itemId" placeholder="选择入库物料" style="width: 100%">
            <el-option
              v-for="i in receiveForm.items"
              :key="i.itemId"
              :label="`${i.materialName}（待入 ${(i.qty || 0) - (i.receivedQty || 0)}）`"
              :value="i.itemId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="入库数量">
          <el-input-number v-model="receiveForm.qty" :min="0.01" :precision="2" controls-position="right" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitReceive">确认入库</el-button>
        <el-button @click="receiveOpen = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Purchase">
import { listPurchase, getPurchase, addPurchase, updatePurchase, delPurchase, receivePurchase, allSuppliers, commonStyles, listBom } from '@/api/garment'

const showSearch = ref(true)

const ids = ref([])
const multiple = ref(true)

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.purchaseId)
  multiple.value = !selection.length
}

const { proxy } = getCurrentInstance()
const categories = ['办公室', '裁床', '仓库', '车间', '后道']
const materialTypes = ['面料', '里布', '辅料', '配件', '耗材', '包装']
const purchaseList = ref([])
const suppliers = ref([])
const styles = ref([])
const loading = ref(false)
const total = ref(0)
const open = ref(false)
const detailOpen = ref(false)
const receiveOpen = ref(false)
const detail = ref({})
const receiveForm = ref({})
const form = ref({})
const queryParams = ref({ pageNum: 1, pageSize: 10, purchaseNo: undefined, purchaseType: undefined, supplierId: undefined, status: undefined })
const rules = {}

const formTotal = computed(() => {
  return (form.value.items || []).reduce((s, i) => s + Number(i.price || 0) * Number(i.qty || 0), 0).toFixed(2)
})

function today() {
  const d = new Date()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${d.getFullYear()}-${m}-${day}`
}
function statusText(s) {
  return s === '2' ? '已入库' : s === '1' ? '部分入库' : '待入库'
}
function itemAmount(row) {
  return (Number(row.price || 0) * Number(row.qty || 0)).toFixed(2)
}
function getList() {
  loading.value = true
  listPurchase(queryParams.value).then(res => {
    purchaseList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
function onStyleChange(styleNo) {
  if (!styleNo || form.value.purchaseType !== 'MATERIAL') return
  listBom({ styleNo, pageNum: 1, pageSize: 200 }).then(res => {
    const rows = res.rows || []
    if (!rows.length) {
      proxy.$modal.msgWarning('该款还没有用料清单（BOM），请先在研发设计中维护')
      return
    }
    form.value.items = rows.map(b => ({
      materialType: b.materialType,
      materialName: b.materialName,
      spec: b.spec,
      unit: b.unit,
      price: b.unitPrice,
      qty: b.qtyPerPiece
    }))
    proxy.$modal.msgSuccess(`已带出该款 BOM 物料 ${rows.length} 项`)
  })
}

function addItem() {
  if (!form.value.items) form.value.items = []
  form.value.items.push({ materialType: '面料', materialName: '', spec: '', unit: '米', price: 0, qty: 0 })
}
function handleAdd() {
  form.value = { purchaseType: 'COMMON', category: '裁床', styleNo: undefined, supplierId: undefined, purchaseDate: today(), paidAmount: 0, remark: undefined, items: [] }
  addItem()
  open.value = true
}
function handleUpdate(row) {
  getPurchase(row.purchaseId).then(res => {
    form.value = res.data
    if (!form.value.items) form.value.items = []
    open.value = true
  })
}
function submitForm() {
  if (!form.value.items || !form.value.items.length) {
    proxy.$modal.msgWarning('请至少录入一条采购明细')
    return
  }
  if (form.value.items.some(i => !i.materialName)) {
    proxy.$modal.msgWarning('请填写物料名称')
    return
  }
  const fn = form.value.purchaseId ? updatePurchase : addPurchase
  fn(form.value).then(() => {
    proxy.$modal.msgSuccess('保存成功')
    open.value = false
    getList()
  })
}
function viewDetail(row) {
  getPurchase(row.purchaseId).then(res => { detail.value = res.data; detailOpen.value = true })
}
function openReceive(row) {
  getPurchase(row.purchaseId).then(res => {
    const items = (res.data.items || []).filter(i => Number(i.receivedQty || 0) < Number(i.qty || 0))
    if (!items.length) {
      proxy.$modal.msgWarning('该采购单已全部入库')
      return
    }
    receiveForm.value = { purchaseId: row.purchaseId, purchaseNo: row.purchaseNo, items, itemId: items[0].itemId, qty: Number(items[0].qty) - Number(items[0].receivedQty || 0) }
    receiveOpen.value = true
  })
}
function submitReceive() {
  if (!receiveForm.value.itemId) {
    proxy.$modal.msgWarning('请选择入库明细')
    return
  }
  receivePurchase({ purchaseId: receiveForm.value.purchaseId, itemId: receiveForm.value.itemId, qty: receiveForm.value.qty }).then(() => {
    proxy.$modal.msgSuccess('入库成功')
    receiveOpen.value = false
    getList()
  })
}
function handleDelete(row) {
  const _ids = row ? [row.purchaseId] : ids.value
  if (!_ids.length) return
  proxy.$modal.confirm(row ? `确认删除采购单「${row.purchaseNo}」？` : `是否确认删除选中的 ${_ids.length} 条数据？`).then(() => delPurchase(_ids)).then(() => {
    proxy.$modal.msgSuccess('删除成功')
    getList()
    ids.value = []
    multiple.value = true
  }).catch(() => {})
}
function handleExport() {
  proxy.download('garment/purchase/export', { ...queryParams.value }, `purchase_${Date.now()}.xlsx`)
}

getList()
allSuppliers().then(res => { suppliers.value = res.data || [] })
commonStyles().then(res => { styles.value = res.data || [] })
</script>
