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

/**
 * 库存类型三色口径：裁片-红、产线-蓝、成品-黑，全系统统一
 */
const STOCK_TYPE_LABEL = { CUT: '裁片', LINE: '产线', FINISH: '成品' }

export function stockTypeText(type) {
  return STOCK_TYPE_LABEL[type] || type || ''
}

export function stockTypeClass(type) {
  return type === 'CUT' ? 'stock-cut-tag' : type === 'LINE' ? 'stock-line-tag' : 'stock-finish-tag'
}

/**
 * 物料类型标签颜色
 */
const MATERIAL_TYPE_TAG = { 面料: 'primary', 里布: 'success', 配件: 'warning', 辅料: 'info', 耗材: 'danger' }

export function materialTypeTag(type) {
  return MATERIAL_TYPE_TAG[type] || 'info'
}

/**
 * 库存流水业务类型标签颜色：入库绿、出库/报废红、领料/补片橙、其他蓝
 */
export function bizTypeTag(type) {
  if (!type) return 'info'
  if (type.includes('入库') || type.includes('初始')) return 'success'
  if (type.includes('出库') || type.includes('报废') || type.includes('作废')) return 'danger'
  if (type.includes('领') || type.includes('补片')) return 'warning'
  return 'primary'
}

/**
 * 次品处理方式标签颜色
 */
export function defectTypeTag(type) {
  return type === '报废' ? 'danger' : type === '补片' ? 'primary' : 'warning'
}

/**
 * 费用类型标签颜色：物料蓝、加工/物流橙、运营推广绿、行政灰
 */
const EXPENSE_TYPE_TAG = {
  辅料: 'primary',
  加工费: 'warning',
  运费: 'warning',
  海运费: 'warning',
  头程运费: 'warning',
  尾程运费: 'warning',
  运营费: 'success',
  拍摄费: 'success',
  推广: 'success',
  办公: 'info',
  差旅: 'info',
  其他: 'info'
}

export function expenseTypeTag(type) {
  return EXPENSE_TYPE_TAG[type] || 'info'
}

/**
 * 包装方式标签颜色
 */
export function packMethodTag(method) {
  return method === '袋装' ? 'primary' : method === '盒装' ? 'warning' : 'success'
}
