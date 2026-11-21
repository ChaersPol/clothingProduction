<template>
  <div class="app-container garment-page">
    <el-form v-show="showSearch" class="search-form" :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item label="扎号" prop="bundleNo">
        <el-input v-model="queryParams.bundleNo" placeholder="扎号" clearable style="width: 130px" @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="款号" prop="styleNo">
        <el-input v-model="queryParams.styleNo" placeholder="款号" clearable style="width: 120px" @keyup.enter="getList" />
      </el-form-item>
      <el-form-item label="检验日期">
        <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD" start-placeholder="开始" end-placeholder="结束" style="width: 240px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="openQc">新增检验</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Warning" @click="openDefect">次品登记</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <div class="table-wrap">
      <el-table v-loading="loading" :data="qcList" border>
        <el-table-column label="检验时间" prop="checkTime" width="170" />
        <el-table-column label="扎号" prop="bundleNo" width="150" />
        <el-table-column label="款号" prop="styleNo" width="100" />
        <el-table-column label="颜色" prop="color" width="100" />
        <el-table-column label="尺码" prop="size" width="70" align="center" />
        <el-table-column label="检验数" prop="checkQty" width="80" align="right" />
        <el-table-column label="合格数" prop="qualifiedQty" width="80" align="right" />
        <el-table-column label="次品数" prop="defectQty" width="80" align="right">
          <template #default="s"><span class="money">{{ s.row.defectQty }}</span></template>
        </el-table-column>
        <el-table-column label="次品原因" prop="defectReason" min-width="140" show-overflow-tooltip />
        <el-table-column label="检验人" prop="inspector" width="100" />
      </el-table>
    </div>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="新增检验" v-model="qcOpen" width="600px" append-to-body>
      <el-form label-width="90px">
        <el-form-item label="扎卡条码" required>
          <el-input v-model="qcForm.code" placeholder="扫描或输入扎号后点查询" @keyup.enter="fetchBundle(qcForm)" />
        </el-form-item>
        <el-form-item label="扎号信息">
          <span v-if="qcForm.bundleId">{{ qcForm.bundleNo }} · {{ qcForm.styleNo }} · {{ qcForm.color }}/{{ qcForm.size }} · 领片 {{ qcForm.bundleQty }} 件</span>
          <span v-else class="muted">请先查询扎卡</span>
        </el-form-item>
        <el-form-item label="检验数">
          <el-input-number v-model="qcForm.checkQty" :min="1" controls-position="right" style="width: 100%" @change="syncQualified" />
        </el-form-item>
        <el-form-item label="次品数">
          <el-input-number v-model="qcForm.defectQty" :min="0" :max="qcForm.checkQty" controls-position="right" style="width: 100%" @change="syncQualified" />
        </el-form-item>
        <el-form-item label="合格数">
          <el-input-number v-model="qcForm.qualifiedQty" :min="0" controls-position="right" style="width: 100%" disabled />
        </el-form-item>
        <el-form-item label="次品原因"><el-input v-model="qcForm.defectReason" placeholder="线头/污渍/尺寸等" /></el-form-item>
        <el-form-item label="检验人"><el-input v-model="qcForm.inspector" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitQc">保存检验</el-button>
        <el-button @click="qcOpen = false">取 消</el-button>
      </template>
    </el-dialog>

    <el-dialog title="次品登记" v-model="defectOpen" width="560px" append-to-body>
      <el-form label-width="90px">
        <el-form-item label="扎卡条码" required>
          <el-input v-model="defectForm.code" placeholder="扫描扎号后查询" @keyup.enter="fetchBundle(defectForm)" />
        </el-form-item>
        <el-form-item label="扎号信息">
          <span v-if="defectForm.bundleId">{{ defectForm.bundleNo }} · {{ defectForm.styleNo }} · {{ defectForm.color }}/{{ defectForm.size }}</span>
          <span v-else class="muted">请先查询扎卡</span>
        </el-form-item>
        <el-form-item label="处理方式">
          <el-radio-group v-model="defectForm.defectType">
            <el-radio value="补片">补片</el-radio>
            <el-radio value="报废">报废</el-radio>
            <el-radio value="返修">返修</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="数量"><el-input-number v-model="defectForm.qty" :min="1" controls-position="right" style="width: 100%" /></el-form-item>
        <el-form-item label="处理人"><el-input v-model="defectForm.handler" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="defectForm.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitDefect">确 定</el-button>
        <el-button @click="defectOpen = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Qc">
import { listQc, saveQc, saveDefect, commonBundle } from '@/api/garment'

const showSearch = ref(true)

const { proxy } = getCurrentInstance()
const qcList = ref([])
const loading = ref(false)
const total = ref(0)
const dateRange = ref([])
const qcOpen = ref(false)
const defectOpen = ref(false)
const qcForm = ref({})
const defectForm = ref({})
const queryParams = ref({ pageNum: 1, pageSize: 10, bundleNo: undefined, styleNo: undefined })

function getList() {
  loading.value = true
  const params = { ...queryParams.value }
  if (dateRange.value && dateRange.value.length === 2) {
    params.params = { beginDate: dateRange.value[0], endDate: dateRange.value[1] }
  }
  listQc(params).then(res => {
    qcList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}
function resetQuery() { proxy.resetForm('queryRef'); dateRange.value = []; queryParams.value.pageNum = 1; getList() }
function fetchBundle(target) {
  if (!target.code) { proxy.$modal.msgWarning('请输入扎卡条码'); return }
  commonBundle(target.code.trim()).then(res => {
    const b = res.data
    if (!b) { proxy.$modal.msgWarning('扎卡不存在'); return }
    target.bundleId = b.bundleId
    target.bundleNo = b.bundleNo
    target.skuId = b.skuId
    target.styleNo = b.styleNo
    target.color = b.color
    target.size = b.size
    target.bundleQty = b.qty
    if (target === qcForm.value) {
      target.checkQty = b.qty || 1
      target.defectQty = 0
      target.qualifiedQty = target.checkQty
    }
    proxy.$modal.msgSuccess('已读取扎卡信息')
  })
}
function syncQualified() {
  qcForm.value.qualifiedQty = Math.max(0, Number(qcForm.value.checkQty || 0) - Number(qcForm.value.defectQty || 0))
}
function openQc() {
  qcForm.value = { code: '', bundleId: null, checkQty: 1, defectQty: 0, qualifiedQty: 1, defectReason: '', inspector: '' }
  qcOpen.value = true
}
function submitQc() {
  if (!qcForm.value.bundleId) { proxy.$modal.msgWarning('请先查询扎卡'); return }
  saveQc(qcForm.value).then(() => {
    proxy.$modal.msgSuccess('检验记录已保存')
    qcOpen.value = false
    getList()
  })
}
function openDefect() {
  defectForm.value = { code: '', bundleId: null, defectType: '补片', qty: 1, handler: '', remark: '' }
  defectOpen.value = true
}
function submitDefect() {
  if (!defectForm.value.bundleId) { proxy.$modal.msgWarning('请先查询扎卡'); return }
  saveDefect(defectForm.value).then(() => {
    proxy.$modal.msgSuccess('次品登记成功')
    defectOpen.value = false
    getList()
  })
}

getList()
</script>
