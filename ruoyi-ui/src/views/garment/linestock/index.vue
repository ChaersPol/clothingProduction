<template>
  <div class="app-container garment-page">
    <el-form v-show="showSearch" class="search-form" :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item label="扎号" prop="bundleNo">
        <el-input v-model="queryParams.bundleNo" placeholder="扎号" clearable style="width: 130px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="款号" prop="styleNo">
        <el-input v-model="queryParams.styleNo" placeholder="款号" clearable style="width: 120px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="颜色" prop="color">
        <el-input v-model="queryParams.color" placeholder="颜色" clearable style="width: 110px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <div class="stat-grid">
      <div class="stat-card blue"><div class="stat-label">产线扎数</div><div class="stat-value">{{ total }}</div></div>
      <div class="stat-card"><div class="stat-label">产线在制件数</div><div class="stat-value">{{ lineQty }}</div></div>
      <div class="stat-card orange"><div class="stat-label">超期扎数（>3天）</div><div class="stat-value">{{ overdueCount }}</div></div>
    </div>

    <div class="table-wrap">
      <el-table v-loading="loading" :data="bundleList" border>
        <el-table-column label="扎号" prop="bundleNo" width="150" fixed="left" />
        <el-table-column label="款号" prop="styleNo" min-width="100" />
        <el-table-column label="颜色" prop="color" width="100" />
        <el-table-column label="尺码" prop="size" width="65" align="center" />
        <el-table-column label="领片数" prop="qty" width="80" align="right" />
        <el-table-column label="已交后道" prop="deliveredQty" width="85" align="right" />
        <el-table-column label="剩余在制" width="85" align="right">
          <template #default="s">{{ (s.row.qty || 0) - (s.row.deliveredQty || 0) }}</template>
        </el-table-column>
        <el-table-column label="领片人" prop="pickupUser" min-width="100" />
        <el-table-column label="领片时间" prop="pickupTime" width="170" />
        <el-table-column label="在线天数" width="90" align="center">
          <template #default="s">
            <el-tag size="small" :type="(s.row.onlineDays || 0) > 3 ? 'danger' : 'info'">{{ s.row.onlineDays || 0 }} 天</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" class-name="op-cell" width="120" align="center" fixed="right">
          <template #default="s">
            <el-button link type="primary" @click="openDeliver(s.row)">交后道</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="完工交后道" v-model="open" width="520px" append-to-body>
      <el-form label-width="100px">
        <el-form-item label="扎号">{{ form.bundleNo }}（{{ form.styleNo }} {{ form.color }}/{{ form.size }}）</el-form-item>
        <el-form-item label="剩余在制">{{ (form.qty || 0) - (form.deliveredQty || 0) }} 件</el-form-item>
        <el-form-item label="本次完工数">
          <el-input-number v-model="form.qtyInput" :min="1" :max="(form.qty || 0) - (form.deliveredQty || 0)" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="其中次品">
          <el-input-number v-model="form.defectQty" :min="0" :max="form.qtyInput" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="交工人">
          <el-select v-model="form.employee" placeholder="选择员工" filterable allow-create style="width: 100%">
            <el-option v-for="e in employees" :key="e.userName" :label="e.nickName || e.userName" :value="e.nickName || e.userName" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitDeliver">确认交付</el-button>
        <el-button @click="open = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="LineStock">
import { lineStock, deliverBundle, commonEmployees } from '@/api/garment'

const showSearch = ref(true)

const { proxy } = getCurrentInstance()
const bundleList = ref([])
const employees = ref([])
const loading = ref(false)
const total = ref(0)
const open = ref(false)
const form = ref({})
const queryParams = ref({ pageNum: 1, pageSize: 10, bundleNo: undefined, styleNo: undefined, color: undefined })

const lineQty = computed(() => bundleList.value.reduce((s, b) => s + (Number(b.qty || 0) - Number(b.deliveredQty || 0)), 0))
const overdueCount = computed(() => bundleList.value.filter(b => Number(b.onlineDays || 0) > 3).length)

function getList() {
  loading.value = true
  lineStock(queryParams.value).then(res => {
    bundleList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
function openDeliver(row) {
  form.value = {
    bundleId: row.bundleId, bundleNo: row.bundleNo, styleNo: row.styleNo, color: row.color, size: row.size,
    qty: row.qty, deliveredQty: row.deliveredQty, qtyInput: Number(row.qty || 0) - Number(row.deliveredQty || 0),
    defectQty: 0, employee: row.pickupUser || ''
  }
  open.value = true
}
function submitDeliver() {
  if (form.value.qtyInput <= 0) { proxy.$modal.msgWarning('本次完工数必须大于0'); return }
  if (form.value.defectQty > form.value.qtyInput) { proxy.$modal.msgWarning('次品数不能大于完工数'); return }
  deliverBundle({
    bundleId: form.value.bundleId, qty: form.value.qtyInput,
    defectQty: form.value.defectQty, employee: form.value.employee
  }).then(() => {
    proxy.$modal.msgSuccess('交后道成功')
    open.value = false
    getList()
  })
}

getList()
commonEmployees().then(res => { employees.value = res.data || [] })
</script>
