import { onUnmounted } from 'vue'

/**
 * 扫码 / 手动输入自动提交：输入停顿后自动执行，无需按回车
 * 扫码枪仍可直接按回车，回车会取消待执行的自动提交，避免重复触发
 *
 * @param {(code: string) => void} submit 提交函数
 * @param {{ delay?: number, minLength?: number }} options 停顿毫秒 / 最小长度
 */
export function useAutoScan(submit, options = {}) {
  const delay = options.delay ?? 600
  const minLength = options.minLength ?? 6
  let timer = null

  function cancel() {
    clearTimeout(timer)
    timer = null
  }

  function onInput(value) {
    cancel()
    const code = String(value ?? '').trim()
    if (code.length < minLength) return
    timer = setTimeout(() => {
      timer = null
      submit(code)
    }, delay)
  }

  function onEnter(value) {
    cancel()
    const code = String(value ?? '').trim()
    if (code) submit(code)
  }

  onUnmounted(cancel)

  return { onInput, onEnter, cancel }
}
