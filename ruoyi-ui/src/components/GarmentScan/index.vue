<template>
  <span class="garment-scan">
    <el-button icon="Camera" v-bind="$attrs" @click="choose">{{ text }}</el-button>
    <input ref="fileRef" type="file" accept="image/*" capture="environment" @change="onFile" />
  </span>
</template>

<script setup name="GarmentScan">
import { BrowserMultiFormatReader } from '@zxing/library'

defineOptions({ inheritAttrs: false })

const props = defineProps({
  text: { type: String, default: '扫码' }
})
const emit = defineEmits(['scan', 'error'])
const fileRef = ref()
const reader = new BrowserMultiFormatReader()

function choose() {
  fileRef.value?.click()
}

// 手机 HTTP 环境无法调用摄像头实时流，改为调起系统相机拍照后在本地识别条码
function onFile(e) {
  const file = e.target.files && e.target.files[0]
  e.target.value = ''
  if (!file) return
  const url = URL.createObjectURL(file)
  reader.decodeFromImageUrl(url).then(result => {
    const info = parseText(result.getText())
    emit('scan', info.code, { path: info.path, fullPath: info.fullPath })
  }).catch(() => {
    emit('error', '未识别到条码，请靠近并对准条码后重试')
  }).finally(() => {
    URL.revokeObjectURL(url)
  })
}

// 二维码内容可能是系统链接，取出条码并保留页面地址，供页面决定是否跳转
function parseText(text) {
  try {
    const u = new URL(text)
    const code = u.searchParams.get('code') || u.searchParams.get('barcode')
    if (code) {
      return { code, path: u.pathname, fullPath: `${u.pathname}${u.search}` }
    }
  } catch (e) {
    // 普通条码/扎号，按原值使用
  }
  return { code: text, path: '', fullPath: '' }
}
</script>

<style scoped>
.garment-scan input[type='file'] {
  display: none;
}
</style>
