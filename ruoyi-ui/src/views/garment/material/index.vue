<template>
  <div class="app-container garment-page">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="物资库存" name="stock">
        <el-form v-show="showSearch" class="search-form" ref="queryRef" :model="queryParams" :inline="true">
          <el-form-item label="物资名称" prop="materialName">
            <el-input v-model="queryParams.materialName" placeholder="名称/规格关键字" clearable style="width: 160px" @keyup.enter="getList" />
          </el-form-item>
          <el-form-item label="类型" prop="materialType">
            <el-select v-model="queryParams.materialType" placeholder="全部" clearable style="width: 120px">
              <el-option label="面料" value="面料" />
              <el-option label="里布" value="里布" />
              <el-option label="辅料" value="辅料" />
              <el-option label="配件" value="配件" />
              <el-option label="耗材" value="耗材" />
              <el-option label="包装" value="包装" />
            </el-select>
          </el-form-item>
          <el-form-item label="仅看预警">
            <el-switch v-model="warningOnly" @change="getList" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>

        <div class="stat-grid">
          <div class="stat-card blue"><div class="stat-label">物资种类</div><div class="stat-value">{{ list.length }}</div></div>
          <div class="stat-card"><div class="stat-label">库存总值</div><div class="stat-value">¥{{ totalValue }}</div></div>
          <div class="stat-card red"><div class="stat-label">低于安全库存</div><div class="stat-value">{{ warningCount }}</div></div>
        </div>

        <el-alert type="info" :closable="false" show-icon style="margin-bottom: 10px"
          title="采购入库自动增加、裁货开单按 BOM 自动领料扣减；线下补货或实物盘点后，点行内「库存调整」改为实际数量即可。" />

        <div class="table-wrap">
          <el-table v-loading="loading" :data="list" border>
            <el-table-column label="类型" prop="materialType" width="80" align="center" />
            <el-table-column label="物资名称" prop="materialName" min-width="150" show-overflow-tooltip />
            <el-table-column label="规格" prop="spec" width="120" show-overflow-tooltip />
            <el-table-column label="单位" prop="unit" width="70" align="center" />
            <el-table-column label="当前库存" prop="stockQty" width="100" align="right">
              <template #default="s">
                <span :style="{ color: Number(s.row.stockQty) <= Number(s.row.minStock) ? '#f56c6c' : '' }">{{ s.row.stockQty }}</span>
              </template>
            </el-table-column>
            <el-table-column label="安全库存" prop="minStock" width="100" align="right" />
            <el-table-column label="单价" prop="price" width="90" align="right" />
            <el-table-column label="库存货值" prop="stockValue" width="110" align="right">
              <template #default="s">{{ (Number(s.row.stockQty || 0) * Number(s.row.price || 0)).toFixed(2) }}</template>
            </el-table-column>
            <el-table-column label="状态" width="100" align="center">
              <template #default="s">
                <el-tag v-if="Number(s.row.stockQty) < 0" type="danger" size="small">负库存</el-tag>
                <el-tag v-else-if="Number(s.row.stockQty) <= Number(s.row.minStock)" type="warning" size="small">需补货</el-tag>
                <el-tag v-else type="success" size="small">正常</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" class-name="op-cell" width="180" align="center" fixed="right">
              <template #default="s">
                <el-button link type="primary" @click="handleAdjust(s.row)">库存调整</el-button>
                <el-button link type="primary" @click="handleMinStock(s.row)">安全库存</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-tab-pane>

      <el-tab-pane label="出入库流水" name="log">
        <el-form v-show="showSearch" class="search-form" :inline="true" :model="logQuery">
          <el-form-item label="物资名称">
            <el-input v-model="logQuery.materialName" placeholder="物资名称" clearable style="width: 150px" @keyup.enter="getLogs" />
          </el-form-item>
          <el-form-item label="业务类型">
            <el-select v-model="logQuery.bizType" placeholder="全部" clearable style="width: 130px">
              <el-option label="采购入库" value="采购入库" />
              <el-option label="生产领料" value="生产领料" />
              <el-option label="领料退回" value="领料退回" />
              <el-option label="手工调整" value="手工调整" />
            </el-select>
          </el-form-item>
          <el-form-item label="单号">
            <el-input v-model="logQuery.bizNo" placeholder="关联单号" clearable style="width: 150px" @keyup.enter="getLogs" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="getLogs">搜索</el-button>
          </el-form-item>
        </el-form>
        <div class="table-wrap">
          <el-table v-loading="logLoading" :data="logs" border>
            <el-table-column label="时间" prop="operateTime" min-width="170" />
            <el-table-column label="物资名称" prop="materialName" min-width="140" show-overflow-tooltip />
            <el-table-column label="规格" prop="spec" width="110" show-overflow-tooltip />
            <el-table-column label="业务类型" prop="bizType" width="115" align="center">
              <template #default="s">
                <el-tag :type="s.row.bizType === '采购入库' ? 'success' : s.row.bizType === '生产领料' ? 'warning' : 'info'" size="small">
                  {{ s.row.bizType }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="变动数量" prop="changeQty" width="100" align="right">
              <template #default="s">
                <span :style="{ color: Number(s.row.changeQty) >= 0 ? '#67c23a' : '#f56c6c' }">
                  {{ Number(s.row.changeQty) >= 0 ? '+' : '' }}{{ s.row.changeQty }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="变动前" prop="beforeQty" width="90" align="right" />
            <el-table-column label="变动后" prop="afterQty" width="90" align="right" />
            <el-table-column label="关联单号" prop="bizNo" width="150" />
            <el-table-column label="操作人" prop="operator" width="90" align="center" />
            <el-table-column label="备注" prop="remark" min-width="180" show-overflow-tooltip />
          </el-table>
        </div>
        <pagination v-show="logTotal > 0" :total="logTotal" v-model:page="logQuery.pageNum" v-model:limit="logQuery.pageSize" @pagination="getLogs" />
      </el-tab-pane>
    </el-tabs>

    <el-dialog title="库存调整" v-model="adjustOpen" width="420px" append-to-body>
      <el-alert type="info" :closable="false" show-icon style="margin-bottom: 12px"
        title="实物盘点或线下补货后，在这里把库存改为实际数量，系统会自动生成一条「手工调整」流水。" />
      <el-form label-width="90px">
        <el-form-item label="物资">{{ adjustForm.materialName }} {{ adjustForm.spec }}</el-form-item>
        <el-form-item label="当前库存">{{ adjustForm.beforeQty }} {{ adjustForm.unit }}</el-form-item>
        <el-form-item label="调整后库存">
          <el-input-number v-model="adjustForm.stockQty" :min="0" :precision="2" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="调整原因">
          <el-input v-model="adjustForm.remark" placeholder="如 线下补货入库、月度盘点盘盈" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitAdjust">确 定</el-button>
        <el-button @click="adjustOpen = false">取 消</el-button>
      </template>
    </el-dialog>

    <el-dialog title="设置安全库存" v-model="minOpen" width="380px" append-to-body>
      <el-form label-width="90px">
        <el-form-item label="物资">{{ minForm.materialName }} {{ minForm.spec }}</el-form-item>
        <el-form-item label="安全库存">
          <el-input-number v-model="minForm.minStock" :min="0" :precision="2" controls-position="right" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitMinStock">确 定</el-button>
        <el-button @click="minOpen = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Material">
import { listMaterial, listMaterialLogs, updateMaterialMinStock, adjustMaterialStock } from '@/api/garment'

const showSearch = ref(true)

const { proxy } = getCurrentInstance()
const activeTab = ref('stock')
const list = ref([])
const logs = ref([])
const loading = ref(false)
const logLoading = ref(false)
const total = ref(0)
const logTotal = ref(0)
const warningOnly = ref(false)
const minOpen = ref(false)
const minForm = ref({})
const adjustOpen = ref(false)
const adjustForm = ref({})
const queryParams = ref({ pageNum: 1, pageSize: 10, materialName: undefined, materialType: undefined })
const logQuery = ref({ pageNum: 1, pageSize: 10, materialName: undefined, bizType: undefined, bizNo: undefined })

const totalValue = computed(() => list.value.reduce((s, r) => s + Number(r.stockQty || 0) * Number(r.price || 0), 0).toFixed(2))
const warningCount = computed(() => list.value.filter(r => Number(r.stockQty) <= Number(r.minStock)).length)

function getList() {
  loading.value = true
  const p = { ...queryParams.value }
  if (warningOnly.value) p.params = { warning: '1' }
  listMaterial(p).then(res => {
    list.value = res.rows || []
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}
function resetQuery() {
  proxy.resetForm('queryRef')
  warningOnly.value = false
  getList()
}
function getLogs() {
  logLoading.value = true
  listMaterialLogs(logQuery.value).then(res => {
    logs.value = res.rows || []
    logTotal.value = res.total
    logLoading.value = false
  }).catch(() => { logLoading.value = false })
}
function handleMinStock(row) {
  minForm.value = { materialId: row.materialId, materialName: row.materialName, spec: row.spec, minStock: Number(row.minStock || 0) }
  minOpen.value = true
}
function handleAdjust(row) {
  adjustForm.value = {
    materialId: row.materialId,
    materialName: row.materialName,
    spec: row.spec,
    unit: row.unit,
    beforeQty: row.stockQty,
    stockQty: Number(row.stockQty || 0),
    remark: ''
  }
  adjustOpen.value = true
}
function submitAdjust() {
  adjustMaterialStock({
    materialId: adjustForm.value.materialId,
    stockQty: adjustForm.value.stockQty,
    remark: adjustForm.value.remark
  }).then(() => {
    proxy.$modal.msgSuccess('库存已更新')
    adjustOpen.value = false
    getList()
    if (logs.value.length) getLogs()
  })
}
function submitMinStock() {
  updateMaterialMinStock({ materialId: minForm.value.materialId, minStock: minForm.value.minStock }).then(() => {
    proxy.$modal.msgSuccess('已更新')
    minOpen.value = false
    getList()
  })
}

watch(activeTab, v => { if (v === 'log' && !logs.value.length) getLogs() })
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
