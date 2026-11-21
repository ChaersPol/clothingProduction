<template>
  <div>
    <el-popover ref="noticePopover" placement="bottom-end" :width="340" trigger="manual" v-model:visible="noticeVisible" popper-class="notice-popover">
      <!-- 弹出内容 -->
      <div class="notice-header">
        <span class="notice-title">消息提醒</span>
        <span class="notice-mark-all" @click="viewAll">查看全部</span>
      </div>
      <div v-if="noticeLoading" class="notice-loading">
        <el-icon class="is-loading"><Loading /></el-icon> 加载中...
      </div>
      <div v-else-if="noticeList.length === 0" class="notice-empty">
        <el-icon style="font-size:24px;display:block;margin-bottom:6px;"><Postcard /></el-icon>
        暂无消息提醒
      </div>
      <div v-else>
        <div v-for="(item, i) in noticeList.slice(0, 8)" :key="i" class="notice-item" :class="{ 'no-link': !item.page }" @click="go(item)">
          <div class="notice-item-head">
            <el-tag size="small" :type="tagType(item.type)" class="notice-tag">{{ typeText(item.type) }}</el-tag>
            <span class="notice-item-title">{{ item.title }}</span>
            <span class="notice-item-date">{{ item.time }}</span>
          </div>
          <div class="notice-item-content">{{ item.content }}</div>
        </div>
        <div v-if="noticeList.length > 8" class="notice-more" @click="viewAll">还有 {{ noticeList.length - 8 }} 条，查看全部</div>
      </div>

      <!-- 触发器 -->
      <template #reference>
        <div class="right-menu-item hover-effect notice-trigger" @mouseenter="onNoticeEnter" @mouseleave="onNoticeLeave">
          <svg-icon icon-class="bell" />
          <span v-if="noticeList.length > 0" class="notice-badge">{{ noticeList.length }}</span>
        </div>
      </template>
    </el-popover>
  </div>
</template>

<script setup>
import { listMessages } from '@/api/garment'

const router = useRouter()
const noticePopover = ref(null)
const noticeList = ref([])
const noticeLoading = ref(false)
const noticeVisible = ref(false)
const noticeLeaveTimer = ref(null)

function loadMessages() {
  noticeLoading.value = true
  listMessages().then(res => {
    noticeList.value = (res.data || []).filter(i => i.page)
  }).finally(() => {
    noticeLoading.value = false
  })
}

onMounted(() => loadMessages())

// 鼠标移入铃铛区域
function onNoticeEnter() {
  clearTimeout(noticeLeaveTimer.value)
  noticeVisible.value = true
  loadMessages()
  nextTick(() => {
    const popper = noticePopover.value?.popperRef?.contentRef
    if (popper && !popper._noticeBound) {
      popper._noticeBound = true
      popper.addEventListener('mouseenter', () => clearTimeout(noticeLeaveTimer.value))
      popper.addEventListener('mouseleave', () => {
        noticeLeaveTimer.value = setTimeout(() => { noticeVisible.value = false }, 100)
      })
    }
  })
}

// 鼠标离开铃铛区域
function onNoticeLeave() {
  noticeLeaveTimer.value = setTimeout(() => { noticeVisible.value = false }, 150)
}

function typeText(t) { return t === 'warning' ? '预警' : t === 'task' ? '待办' : '提示' }
function tagType(t) { return t === 'warning' ? 'danger' : t === 'task' ? 'warning' : 'info' }

// 点击消息直接跳到对应业务数据
function go(item) {
  if (!item.page) return
  noticeVisible.value = false
  router.push({ path: item.page, query: item.query || {} })
}

function viewAll() {
  noticeVisible.value = false
  router.push('/msg')
}
</script>

<style lang="scss" scoped>
.notice-trigger {
  position: relative;
  transform: translateX(-6px);
  .svg-icon { width: 1.2em; height: 1.2em; vertical-align: -0.2em; }
  .notice-badge {
    position: absolute;
    top: 7px;
    right: -3px;
    background: #f56c6c;
    color: #fff;
    border-radius: 10px;
    font-size: 10px;
    height: 16px;
    line-height: 16px;
    padding: 0 4px;
    min-width: 16px;
    text-align: center;
    white-space: nowrap;
    pointer-events: none;
  }
}
.notice-popover { padding: 0 !important; }
.notice-popover .notice-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 14px;
  background: #f7f9fb;
  border-bottom: 1px solid #eee;
  font-size: 13px;
  font-weight: 600;
  color: #333;
}
.notice-popover .notice-mark-all {
  font-size: 12px;
  color: var(--el-color-primary);
  font-weight: normal;
  cursor: pointer;
}
.notice-popover .notice-mark-all:hover { color: #2b7cc1; }
.notice-popover .notice-loading,
.notice-popover .notice-empty {
  padding: 24px;
  text-align: center;
  color: #bbb;
  font-size: 12px;
  line-height: 1.8;
}
.notice-popover .notice-item {
  padding: 9px 14px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: background 0.15s;
}
.notice-popover .notice-item.no-link { cursor: default; }
.notice-popover .notice-item:last-child { border-bottom: none; }
.notice-popover .notice-item:hover { background: #f7f9fb; }
.notice-popover .notice-item-head {
  display: flex;
  align-items: center;
  gap: 8px;
}
.notice-popover .notice-tag { flex-shrink: 0; }
.notice-popover .notice-item-title {
  flex: 1;
  font-size: 12px;
  font-weight: 600;
  color: #333;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.notice-popover .notice-item-date {
  flex-shrink: 0;
  font-size: 11px;
  color: #bbb;
}
.notice-popover .notice-item-content {
  margin-top: 3px;
  padding-left: 2px;
  font-size: 12px;
  color: #666;
  line-height: 1.5;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.notice-popover .notice-more {
  padding: 10px;
  text-align: center;
  font-size: 12px;
  color: var(--el-color-primary);
  cursor: pointer;
  border-top: 1px solid #f5f5f5;
}
</style>
