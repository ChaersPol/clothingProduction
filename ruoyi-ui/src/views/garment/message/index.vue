<template>
  <div class="app-container garment-page">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Refresh" @click="loadMessages">刷新消息</el-button>
      </el-col>
      <el-col :span="6" style="display: flex; align-items: center">
        <span class="muted">共 {{ messages.length }} 条提醒</span>
      </el-col>
    </el-row>

    <div class="msg-grid">
      <el-card v-for="(m, i) in messages" :key="i" shadow="hover" class="msg-card section-card">
        <div class="msg-head">
          <el-tag :type="tagType(m.type)" size="small">{{ typeText(m.type) }}</el-tag>
          <span class="muted">{{ m.time }}</span>
        </div>
        <div class="msg-title">{{ m.title }}</div>
        <div class="msg-content">{{ m.content }}</div>
        <div class="msg-actions">
          <el-button v-if="m.title.includes('库存')" link type="primary" @click="$router.push('/warehouse/inventory')">查看库存</el-button>
          <el-button v-if="m.title.includes('分扎')" link type="primary" @click="$router.push('/cut/bundle')">去分扎</el-button>
          <el-button v-if="m.title.includes('领片')" link type="primary" @click="$router.push('/workshop/pick')">去领片</el-button>
          <el-button v-if="m.title.includes('入库')" link type="primary" @click="$router.push('/finishing/packing')">去入库</el-button>
          <el-button v-if="m.title.includes('超期')" link type="primary" @click="$router.push('/workshop/linestock')">查看产线</el-button>
        </div>
      </el-card>
    </div>
    <el-empty v-if="!messages.length" description="暂无消息" />
  </div>
</template>

<script setup name="Message">
import { listMessages } from '@/api/garment'

const messages = ref([])

function loadMessages() {
  listMessages().then(res => { messages.value = res.data || [] })
}
function typeText(t) { return t === 'warning' ? '预警' : t === 'task' ? '待办' : '提示' }
function tagType(t) { return t === 'warning' ? 'danger' : t === 'task' ? 'warning' : 'info' }

loadMessages()
</script>

<style scoped lang="scss">
.msg-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  .msg-card {
    flex: 1 1 300px;
    min-width: 0;
    margin-bottom: 0;
  }
}
.msg-card {
  .msg-head {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
  }
  .msg-title {
    font-size: 15px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 6px;
  }
  .msg-content {
    font-size: 13px;
    color: #606266;
    line-height: 1.6;
    min-height: 42px;
  }
  .msg-actions {
    margin-top: 6px;
    text-align: right;
  }
}
</style>
