<template>
  <div class="app-container garment-page">
    <el-form v-show="showSearch" class="search-form" :inline="true" :model="queryParams" ref="queryRef">
      <el-form-item label="类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="全部" clearable style="width: 120px">
          <el-option label="预警" value="warning" />
          <el-option label="待办" value="task" />
          <el-option label="提示" value="info" />
        </el-select>
      </el-form-item>
      <el-form-item label="关键词" prop="keyword">
        <el-input v-model="queryParams.keyword" placeholder="提醒事项 / 内容" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Refresh" @click="loadMessages">刷新消息</el-button>
      </el-col>
      <el-col :span="6" style="display: flex; align-items: center">
        <span class="muted">共 {{ filtered.length }} 条提醒</span>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="loadMessages"></right-toolbar>
    </el-row>

    <div class="table-wrap">
      <el-table v-loading="loading" :data="pagedList" border>
        <el-table-column label="序号" type="index" width="60" align="center" :index="indexMethod" />
        <el-table-column label="类型" prop="type" width="90" align="center">
          <template #default="s">
            <el-tag :type="tagType(s.row.type)" size="small">{{ typeText(s.row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="提醒时间" prop="time" width="170" />
        <el-table-column label="提醒事项" prop="title" min-width="180" />
        <el-table-column label="提醒内容" prop="content" min-width="320" show-overflow-tooltip />
        <el-table-column label="操作" class-name="op-cell" width="120" align="center" fixed="right">
          <template #default="s">
            <el-button v-if="actionOf(s.row)" link type="primary" @click="$router.push(actionOf(s.row).path)">
              {{ actionOf(s.row).text }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <pagination v-show="filtered.length > 0" :total="filtered.length" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="() => {}" />
  </div>
</template>

<script setup name="Message">
import { listMessages } from '@/api/garment'

const { proxy } = getCurrentInstance()
const showSearch = ref(true)
const loading = ref(false)
const messages = ref([])
const queryParams = ref({ pageNum: 1, pageSize: 10, type: undefined, keyword: undefined })

const filtered = computed(() => {
  const kw = (queryParams.value.keyword || '').trim()
  return messages.value.filter(m =>
    (!queryParams.value.type || m.type === queryParams.value.type) &&
    (!kw || (m.title || '').includes(kw) || (m.content || '').includes(kw))
  )
})
const pagedList = computed(() => {
  const start = (queryParams.value.pageNum - 1) * queryParams.value.pageSize
  return filtered.value.slice(start, start + queryParams.value.pageSize)
})

const ACTIONS = [
  { key: '库存', text: '查看库存', path: '/warehouse/inventory' },
  { key: '分扎', text: '去分扎', path: '/cut/bundle' },
  { key: '领片', text: '去领片', path: '/workshop/pick' },
  { key: '超期', text: '查看产线', path: '/workshop/linestock' },
  { key: '入库', text: '去入库', path: '/finishing/packing' }
]

function loadMessages() {
  loading.value = true
  listMessages().then(res => { messages.value = res.data || [] }).finally(() => { loading.value = false })
}
function handleQuery() { queryParams.value.pageNum = 1 }
function resetQuery() {
  proxy.resetForm('queryRef')
  queryParams.value.type = undefined
  queryParams.value.keyword = undefined
  queryParams.value.pageNum = 1
}
function indexMethod(i) { return (queryParams.value.pageNum - 1) * queryParams.value.pageSize + i + 1 }
function typeText(t) { return t === 'warning' ? '预警' : t === 'task' ? '待办' : '提示' }
function tagType(t) { return t === 'warning' ? 'danger' : t === 'task' ? 'warning' : 'info' }
function actionOf(row) { return ACTIONS.find(a => (row.title || '').includes(a.key)) }

loadMessages()
</script>
