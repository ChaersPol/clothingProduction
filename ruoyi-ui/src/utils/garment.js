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

/**
 * 当前登录人是否可查看全部车间数据。
 * 仅管理员、系统管理员可看全部，其他账号只看本人数据。
 */
export function isGarmentManager() {
  const userStore = useUserStore()
  const roles = userStore.roles || []
  return roles.includes('admin') || roles.includes('sysadmin')
}
