<template>
  <div class="app-container garment-page">
    <div class="stat-grid">
      <div class="stat-card green"><div class="stat-label">当日包装件数</div><div class="stat-value">{{ totalQty }}</div></div>
      <div class="stat-card blue"><div class="stat-label">包装次数</div><div class="stat-value">{{ totalTimes }}</div></div>
      <div class="stat-card"><div class="stat-label">包装人数</div><div class="stat-value">{{ statList.length }}</div></div>
      <div class="stat-card orange"><div class="stat-label">人均件数</div><div class="stat-value">{{ avgQty }}</div></div>
    </div>

    <el-form v-show="showSearch" class="search-form" :inline="true">
      <el-form-item label="统计日期">
        <el-date-picker v-model="statDate" type="date" value-format="YYYY-MM-DD" style="width: 160px" @change="loadData" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="loadData">统计</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar v-model:showSearch="showSearch" @queryTable="loadData"></right-toolbar>
    </el-row>

    <el-row :gutter="14">
      <el-col :xs="24" :md="12">
        <el-card shadow="never" class="section-card">
          <template #header><span>包装产量排行</span></template>
          <div class="table-wrap">
            <el-table :data="statList" border size="small">
              <el-table-column label="包装人" prop="packer" min-width="110" />
              <el-table-column label="包装件数" prop="qty" width="110" align="right" />
              <el-table-column label="包装次数" prop="times" width="100" align="center" />
            </el-table>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card shadow="never" class="section-card">
          <template #header><span>次品处理记录</span></template>
          <div class="table-wrap">
            <el-table :data="defectList" border size="small">
              <el-table-column label="处理时间" prop="handleTime" width="170" />
              <el-table-column label="扎号" prop="bundleNo" width="150" />
              <el-table-column label="处理方式" prop="defectType" width="90" align="center">
                <template #default="s">
                  <el-tag size="small" :type="defectTypeTag(s.row.defectType)">{{ s.row.defectType }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="数量" prop="qty" width="70" align="right" />
              <el-table-column label="处理人" prop="handler" min-width="110" />
            </el-table>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="FinishingStat">
import { finishingStat, listDefect } from '@/api/garment'
import { defectTypeTag } from '@/utils/garment'

const showSearch = ref(true)

const statDate = ref(new Date().toISOString().slice(0, 10))
const statList = ref([])
const defectList = ref([])

const totalQty = computed(() => statList.value.reduce((s, i) => s + Number(i.qty || 0), 0))
const totalTimes = computed(() => statList.value.reduce((s, i) => s + Number(i.times || 0), 0))
const avgQty = computed(() => statList.value.length ? Math.round(totalQty.value / statList.value.length) : 0)

function loadData() {
  finishingStat(statDate.value).then(res => { statList.value = res.data || [] })
  listDefect({ pageNum: 1, pageSize: 10 }).then(res => { defectList.value = res.rows || [] })
}

loadData()

// 从标签页切回本页时自动刷新数据，避免展示缓存中的旧列表
let _pageActivated = false
onActivated(() => {
  if (_pageActivated) {
    loadData()
  } else {
    _pageActivated = true
  }
})

</script>
