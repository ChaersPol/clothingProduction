import useUserStore from '@/store/modules/user'

/**
 * 当前登录用户作为业务操作人（领片 / 交后道 / 包装 / 检验 / 盘点等）的姓名。
 * 账号昵称形如「车间-李梅」，去掉部门前缀后与员工档案中的姓名一致，
 * 保证按操作人统计的产量数据归属到正确的人。
 */
export function currentOperatorName() {
  const userStore = useUserStore()
  const nick = (userStore.nickName || userStore.name || '').trim()
  const idx = nick.lastIndexOf('-')
  return idx > -1 ? nick.slice(idx + 1) : nick
}
