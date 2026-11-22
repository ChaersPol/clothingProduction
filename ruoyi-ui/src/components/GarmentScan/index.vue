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
    emit('scan', extractCode(result.getText()))
  }).catch(() => {
    emit('error', '未识别到条码，请靠近并对准条码后重试')
  }).finally(() => {
    URL.revokeObjectURL(url)
  })
}

// 二维码内容可能是系统链接，取其中的 code / barcode 参数
function extractCode(text) {
  try {
    const u = new URL(text)
    return u.searchParams.get('code') || u.searchParams.get('barcode') || text
  } catch (e) {
    return text
  }
}
</script>

<style scoped>
.garment-scan input[type='file'] {
  display: none;
}
</style>
