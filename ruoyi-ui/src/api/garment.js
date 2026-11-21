import request from '@/utils/request'

/* ---------------- 款号 ---------------- */
export function listStyle(query) {
  return request({ url: '/garment/style/list', method: 'get', params: query })
}
export function getStyle(styleId) {
  return request({ url: '/garment/style/' + styleId, method: 'get' })
}
export function addStyle(data) {
  return request({ url: '/garment/style', method: 'post', data })
}
export function updateStyle(data) {
  return request({ url: '/garment/style', method: 'put', data })
}
export function delStyle(styleId) {
  return request({ url: '/garment/style/' + styleId, method: 'delete' })
}

/* ---------------- 颜色 / 尺码 ---------------- */
export function listColor(query) {
  return request({ url: '/garment/color/list', method: 'get', params: query })
}
export function addColor(data) {
  return request({ url: '/garment/color', method: 'post', data })
}
export function updateColor(data) {
  return request({ url: '/garment/color', method: 'put', data })
}
export function delColor(colorId) {
  return request({ url: '/garment/color/' + colorId, method: 'delete' })
}
export function listSize(query) {
  return request({ url: '/garment/size/list', method: 'get', params: query })
}
export function addSize(data) {
  return request({ url: '/garment/size', method: 'post', data })
}
export function updateSize(data) {
  return request({ url: '/garment/size', method: 'put', data })
}
export function delSize(sizeId) {
  return request({ url: '/garment/size/' + sizeId, method: 'delete' })
}

/* ---------------- SKU ---------------- */
export function listSku(query) {
  return request({ url: '/garment/sku/list', method: 'get', params: query })
}
export function getSku(skuId) {
  return request({ url: '/garment/sku/' + skuId, method: 'get' })
}
export function addSku(data) {
  return request({ url: '/garment/sku', method: 'post', data })
}
export function updateSku(data) {
  return request({ url: '/garment/sku', method: 'put', data })
}
export function delSku(skuId) {
  return request({ url: '/garment/sku/' + skuId, method: 'delete' })
}
export function generateSku(data) {
  return request({ url: '/garment/sku/generate', method: 'post', data })
}
export function importStock(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/garment/sku/importStock',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
export function stockTemplate() {
  return request({ url: '/garment/sku/importTemplate', method: 'get', responseType: 'blob' })
}
export function labelList(query) {
  return request({ url: '/garment/sku/label/list', method: 'get', params: query })
}

/* ---------------- BOM / 成本 ---------------- */
export function listBom(query) {
  return request({ url: '/garment/bom/list', method: 'get', params: query })
}
export function addBom(data) {
  return request({ url: '/garment/bom', method: 'post', data })
}
export function updateBom(data) {
  return request({ url: '/garment/bom', method: 'put', data })
}
export function delBom(bomId) {
  return request({ url: '/garment/bom/' + bomId, method: 'delete' })
}
export function listCost(query) {
  return request({ url: '/garment/cost/list', method: 'get', params: query })
}
export function getCostByStyle(styleNo) {
  return request({ url: '/garment/cost/style/' + styleNo, method: 'get' })
}
export function saveCost(data) {
  return request({ url: '/garment/cost', method: 'post', data })
}

/* ---------------- 供应商 / 采购 ---------------- */
export function listSupplier(query) {
  return request({ url: '/garment/supplier/list', method: 'get', params: query })
}
export function allSuppliers() {
  return request({ url: '/garment/supplier/all', method: 'get' })
}
export function addSupplier(data) {
  return request({ url: '/garment/supplier', method: 'post', data })
}
export function updateSupplier(data) {
  return request({ url: '/garment/supplier', method: 'put', data })
}
export function delSupplier(supplierId) {
  return request({ url: '/garment/supplier/' + supplierId, method: 'delete' })
}
export function reconciliation() {
  return request({ url: '/garment/supplier/reconciliation', method: 'get' })
}
export function listPurchase(query) {
  return request({ url: '/garment/purchase/list', method: 'get', params: query })
}
export function getPurchase(purchaseId) {
  return request({ url: '/garment/purchase/' + purchaseId, method: 'get' })
}
export function addPurchase(data) {
  return request({ url: '/garment/purchase', method: 'post', data })
}
export function updatePurchase(data) {
  return request({ url: '/garment/purchase', method: 'put', data })
}
export function delPurchase(purchaseId) {
  return request({ url: '/garment/purchase/' + purchaseId, method: 'delete' })
}
export function receivePurchase(data) {
  return request({ url: '/garment/purchase/receive', method: 'post', data })
}
export function listPurchaseItems(query) {
  return request({ url: '/garment/purchase/items', method: 'get', params: query })
}

/* ---------------- 裁床 ---------------- */
export function listCutOrder(query) {
  return request({ url: '/garment/cutorder/list', method: 'get', params: query })
}
export function getCutOrder(cutId) {
  return request({ url: '/garment/cutorder/' + cutId, method: 'get' })
}
export function addCutOrder(data) {
  return request({ url: '/garment/cutorder', method: 'post', data })
}
export function updateCutOrder(data) {
  return request({ url: '/garment/cutorder', method: 'put', data })
}
export function delCutOrder(cutId) {
  return request({ url: '/garment/cutorder/' + cutId, method: 'delete' })
}
export function listBundle(query) {
  return request({ url: '/garment/bundle/list', method: 'get', params: query })
}
export function getBundle(bundleId) {
  return request({ url: '/garment/bundle/' + bundleId, method: 'get' })
}
export function bundleByCode(code) {
  return request({ url: '/garment/bundle/code/' + code, method: 'get' })
}
export function autoBundle(data) {
  return request({ url: '/garment/bundle/auto', method: 'post', data })
}
export function addBundle(data) {
  return request({ url: '/garment/bundle', method: 'post', data })
}
export function bundleStockIn(code) {
  return request({ url: '/garment/bundle/stockin', method: 'post', data: { code } })
}
export function patchBundle(data) {
  return request({ url: '/garment/bundle/patch', method: 'post', data })
}
export function delBundle(bundleId) {
  return request({ url: '/garment/bundle/' + bundleId, method: 'delete' })
}

/* ---------------- 车间 ---------------- */
export function pickBundle(data) {
  return request({ url: '/garment/workshop/pick', method: 'post', data })
}
export function listPicks(query) {
  return request({ url: '/garment/workshop/picks', method: 'get', params: query })
}
export function lineStock(query) {
  return request({ url: '/garment/workshop/linestock', method: 'get', params: query })
}
export function deliverBundle(data) {
  return request({ url: '/garment/workshop/deliver', method: 'post', data })
}
export function listDeliveries(query) {
  return request({ url: '/garment/workshop/deliveries', method: 'get', params: query })
}
export function pickStat(date) {
  return request({ url: '/garment/workshop/pickstat', method: 'get', params: { date } })
}
export function deliveryStat(date) {
  return request({ url: '/garment/workshop/deliverystat', method: 'get', params: { date } })
}

/* ---------------- 后道 ---------------- */
export function saveQc(data) {
  return request({ url: '/garment/finishing/qc', method: 'post', data })
}
export function listQc(query) {
  return request({ url: '/garment/finishing/qc/list', method: 'get', params: query })
}
export function savePack(data) {
  return request({ url: '/garment/finishing/pack', method: 'post', data })
}
export function listPack(query) {
  return request({ url: '/garment/finishing/pack/list', method: 'get', params: query })
}
export function saveDefect(data) {
  return request({ url: '/garment/finishing/defect', method: 'post', data })
}
export function listDefect(query) {
  return request({ url: '/garment/finishing/defect/list', method: 'get', params: query })
}
export function finishingStockIn(data) {
  return request({ url: '/garment/finishing/stockin', method: 'post', data })
}
export function finishingStat(date) {
  return request({ url: '/garment/finishing/stat', method: 'get', params: { date } })
}

/* ---------------- 仓库 ---------------- */
export function listInventory(query) {
  return request({ url: '/garment/inventory/list', method: 'get', params: query })
}
export function listStockLogs(query) {
  return request({ url: '/garment/stocklog/list', method: 'get', params: query })
}
export function listWarnings(query) {
  return request({ url: '/garment/warning/list', method: 'get', params: query })
}
export function cutSuggestion(styleNo) {
  return request({ url: '/garment/warning/suggestion', method: 'get', params: { styleNo } })
}
export function updateMinStock(data) {
  return request({ url: '/garment/warning/minstock', method: 'put', data })
}
export function listCheck(query) {
  return request({ url: '/garment/check/list', method: 'get', params: query })
}
export function getCheck(checkId) {
  return request({ url: '/garment/check/' + checkId, method: 'get' })
}
export function addCheck(data, stockType) {
  return request({ url: '/garment/check', method: 'post', params: { stockType }, data })
}
export function saveCheckItems(data) {
  return request({ url: '/garment/check/items', method: 'put', data })
}
export function completeCheck(checkId) {
  return request({ url: '/garment/check/' + checkId + '/complete', method: 'post' })
}
export function delCheck(checkId) {
  return request({ url: '/garment/check/' + checkId, method: 'delete' })
}
export function listOutbound(query) {
  return request({ url: '/garment/outbound/list', method: 'get', params: query })
}
export function getOutbound(outboundId) {
  return request({ url: '/garment/outbound/' + outboundId, method: 'get' })
}
export function addOutbound(data) {
  return request({ url: '/garment/outbound', method: 'post', data })
}
export function cancelOutbound(outboundId, reason) {
  return request({ url: '/garment/outbound/' + outboundId + '/cancel', method: 'post', data: { reason } })
}
export function listShipment(query) {
  return request({ url: '/garment/shipment/list', method: 'get', params: query })
}
export function addShipment(data) {
  return request({ url: '/garment/shipment', method: 'post', data })
}
export function updateShipment(data) {
  return request({ url: '/garment/shipment', method: 'put', data })
}
export function delShipment(shipId) {
  return request({ url: '/garment/shipment/' + shipId, method: 'delete' })
}
export function shipmentSummary(query) {
  return request({ url: '/garment/shipment/summary', method: 'get', params: query })
}
export function listReturn(query) {
  return request({ url: '/garment/return/list', method: 'get', params: query })
}
export function addReturn(data) {
  return request({ url: '/garment/return', method: 'post', data })
}
export function delReturn(returnId) {
  return request({ url: '/garment/return/' + returnId, method: 'delete' })
}

/* ---------------- 财务 / 报表 ---------------- */
export function rankSku(query) {
  return request({ url: '/garment/finance/rankSku', method: 'get', params: query })
}
export function rankStyle(query) {
  return request({ url: '/garment/finance/rankStyle', method: 'get', params: query })
}
export function financeInventory(query) {
  return request({ url: '/garment/finance/inventoryDetail', method: 'get', params: query })
}
export function profitReport(query) {
  return request({ url: '/garment/finance/profit', method: 'get', params: query })
}
export function returnReport(query) {
  return request({ url: '/garment/finance/returnStats', method: 'get', params: query })
}
export function listExpense(query) {
  return request({ url: '/garment/expense/list', method: 'get', params: query })
}
export function addExpense(data) {
  return request({ url: '/garment/expense', method: 'post', data })
}
export function updateExpense(data) {
  return request({ url: '/garment/expense', method: 'put', data })
}
export function delExpense(expenseId) {
  return request({ url: '/garment/expense/' + expenseId, method: 'delete' })
}

/* ---------------- 通用 ---------------- */
export function commonStyles() {
  return request({ url: '/garment/common/styles', method: 'get' })
}
export function commonColors() {
  return request({ url: '/garment/common/colors', method: 'get' })
}
export function commonSizes() {
  return request({ url: '/garment/common/sizes', method: 'get' })
}
export function commonSkus(styleNo) {
  return request({ url: '/garment/common/skus', method: 'get', params: { styleNo } })
}
export function skuByBarcode(barcode) {
  return request({ url: '/garment/common/sku', method: 'get', params: { barcode } })
}
export function commonBundle(code) {
  return request({ url: '/garment/common/bundle', method: 'get', params: { code } })
}
export function commonEmployees() {
  return request({ url: '/garment/common/employees', method: 'get' })
}

/* ---------------- 员工档案 ---------------- */
export function listEmployee(query) {
  return request({ url: '/garment/employee/list', method: 'get', params: query })
}
export function allEmployees() {
  return request({ url: '/garment/employee/all', method: 'get' })
}
export function addEmployee(data) {
  return request({ url: '/garment/employee', method: 'post', data })
}
export function updateEmployee(data) {
  return request({ url: '/garment/employee', method: 'put', data })
}
export function delEmployee(employeeId) {
  return request({ url: '/garment/employee/' + employeeId, method: 'delete' })
}

/* ---------------- 看板 ---------------- */
export function getDashboard() {
  return request({ url: '/garment/dashboard', method: 'get' })
}
export function listMessages() {
  return request({ url: '/garment/message/list', method: 'get' })
}
export function cutByCutter(query) {
  return request({ url: '/garment/cutstat/cutter', method: 'get', params: query })
}
export function cutByDay(query) {
  return request({ url: '/garment/cutstat/day', method: 'get', params: query })
}

/* ---------------- 物资库存 ---------------- */
export function listMaterial(query) {
  return request({ url: '/garment/material/list', method: 'get', params: query })
}
export function listMaterialLogs(query) {
  return request({ url: '/garment/material/logs', method: 'get', params: query })
}
export function updateMaterialMinStock(data) {
  return request({ url: '/garment/material/minstock', method: 'put', data })
}

// 物资手工库存调整（盘盈盘亏）
export function adjustMaterialStock(data) {
  return request({ url: '/garment/material/adjust', method: 'put', data })
}
