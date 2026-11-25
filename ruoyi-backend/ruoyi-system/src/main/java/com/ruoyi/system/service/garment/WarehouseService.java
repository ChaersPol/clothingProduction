package com.ruoyi.system.service.garment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.garment.*;
import com.ruoyi.system.mapper.garment.*;

/**
 * 仓库管理服务：库存看板、成品出库、出货、盘点、退货、预警
 */
@Service
public class WarehouseService
{
    @Autowired
    private BizInventoryMapper inventoryMapper;
    @Autowired
    private BizStockLogMapper logMapper;
    @Autowired
    private BizSkuMapper skuMapper;
    @Autowired
    private BizOutboundMapper outboundMapper;
    @Autowired
    private BizShipmentMapper shipmentMapper;
    @Autowired
    private BizCheckMapper checkMapper;
    @Autowired
    private BizReturnMapper returnMapper;
    @Autowired
    private BizDefectMapper defectMapper;
    @Autowired
    private GarmentStockService stockService;
    @Autowired
    private BizCustomerMapper customerMapper;

    public List<BizInventory> selectInventory(BizInventory query)
    {
        return inventoryMapper.selectInventoryList(query);
    }

    public List<BizStockLog> selectLogs(BizStockLog log)
    {
        return logMapper.selectLogList(log);
    }

    public List<Map<String, Object>> selectWarnings(String styleNo)
    {
        return inventoryMapper.selectWarnings(styleNo);
    }

    /**
     * 根据库存预警生成裁货建议单
     */
    public List<Map<String, Object>> cutSuggestion(String styleNo)
    {
        List<Map<String, Object>> warnings = inventoryMapper.selectWarnings(styleNo);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : warnings)
        {
            int minStock = toInt(row.get("min_stock"));
            int finish = toInt(row.get("finish_qty"));
            int line = toInt(row.get("line_qty"));
            int cut = toInt(row.get("cut_qty"));
            int suggest = minStock * 2 - finish - line - cut;
            if (suggest > 0)
            {
                Map<String, Object> item = new HashMap<>(row);
                item.put("suggestQty", suggest);
                result.add(item);
            }
        }
        return result;
    }

    public int updateMinStock(Long skuId, Integer minStock)
    {
        return skuMapper.updateMinStock(skuId, minStock);
    }

    /* ---------------- 成品出库 ---------------- */
    public List<BizOutbound> selectOutboundList(BizOutbound query)
    {
        return outboundMapper.selectOutboundList(query);
    }

    public BizOutbound selectOutboundById(Long id)
    {
        BizOutbound outbound = outboundMapper.selectOutboundById(id);
        if (outbound != null)
        {
            outbound.setItems(outboundMapper.selectItems(id));
        }
        return outbound;
    }

    @Transactional
    public BizOutbound saveOutbound(BizOutbound outbound)
    {
        if (outbound.getOutboundId() != null)
        {
            throw new ServiceException("出库单已生成，如数量有误请先作废再重新开单");
        }
        checkCustomer(outbound.getCustomer());
        if (outbound.getItems() == null || outbound.getItems().isEmpty())
        {
            throw new ServiceException("请录入出库明细");
        }
        if (StringUtils.isEmpty(outbound.getOutboundNo()))
        {
            String prefix = "CK" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
            outbound.setOutboundNo(prefix + String.format("%03d", outboundMapper.maxSeq(prefix) + 1));
        }
        if (StringUtils.isEmpty(outbound.getOperator()))
        {
            outbound.setOperator(SecurityUtils.getUsername());
        }
        outbound.setStatus("0");
        outbound.setCreateBy(SecurityUtils.getUsername());
        int totalQty = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<BizOutboundItem> items = new ArrayList<>();
        int rowNo = 0;
        for (BizOutboundItem item : outbound.getItems())
        {
            rowNo++;
            BizSku sku = item.getSkuId() != null ? skuMapper.selectSkuById(item.getSkuId())
                    : (StringUtils.isEmpty(item.getBarcode()) ? null : skuMapper.selectSkuByBarcode(item.getBarcode()));
            if (sku == null)
            {
                throw new ServiceException("第 " + rowNo + " 行未找到商品：" + (item.getBarcode() == null || item.getBarcode().isEmpty() ? "未选择商品" : item.getBarcode()));
            }
            if (item.getQty() == null || item.getQty() <= 0)
            {
                throw new ServiceException("出库数量必须大于0");
            }
            item.setSkuId(sku.getSkuId());
            item.setStyleNo(sku.getStyleNo());
            item.setColor(sku.getColor());
            item.setSize(sku.getSize());
            item.setPrice(item.getPrice() == null || item.getPrice().compareTo(BigDecimal.ZERO) == 0
                    ? sku.getSalePrice() : item.getPrice());
            item.setAmount(item.getPrice().multiply(new BigDecimal(item.getQty())).setScale(2, java.math.RoundingMode.HALF_UP));
            totalQty += item.getQty();
            totalAmount = totalAmount.add(item.getAmount());
            items.add(item);
        }
        outbound.setTotalQty(totalQty);
        outbound.setTotalAmount(totalAmount.setScale(2, java.math.RoundingMode.HALF_UP));
        outboundMapper.insertOutbound(outbound);
        for (BizOutboundItem item : items)
        {
            stockService.change(item.getSkuId(), GarmentStockService.FINISH, -item.getQty(), "成品出库",
                    outbound.getOutboundNo(), "", "出库");
            item.setOutboundId(outbound.getOutboundId());
            outboundMapper.insertItem(item);
        }
        return outbound;
    }

    /**
     * 作废出库单：回滚库存并留痕
     */
    @Transactional
    public int cancelOutbound(Long outboundId, String reason)
    {
        BizOutbound outbound = outboundMapper.selectOutboundById(outboundId);
        if (outbound == null)
        {
            throw new ServiceException("出库单不存在");
        }
        if ("1".equals(outbound.getStatus()))
        {
            throw new ServiceException("该出库单已作废");
        }
        if (StringUtils.isEmpty(reason))
        {
            throw new ServiceException("请填写作废原因");
        }
        for (BizOutboundItem item : outboundMapper.selectItems(outboundId))
        {
            stockService.change(item.getSkuId(), GarmentStockService.FINISH, item.getQty(), "出库作废",
                    outbound.getOutboundNo(), "", "作废原因：" + reason);
        }
        return outboundMapper.cancel(outboundId, reason);
    }

    /* ---------------- 出货表 ---------------- */
    public List<BizShipment> selectShipmentList(BizShipment query)
    {
        return shipmentMapper.selectShipmentList(query);
    }

    public List<Map<String, Object>> shipmentSummary(String beginDate, String endDate)
    {
        return shipmentMapper.sumByDay(beginDate, endDate);
    }

    @Transactional
    public int saveShipment(BizShipment shipment)
    {
        checkCustomer(shipment.getCustomer());
        if (shipment.getAmount() == null)
        {
            BigDecimal price = shipment.getPrice() == null ? BigDecimal.ZERO : shipment.getPrice();
            shipment.setAmount(price.multiply(new BigDecimal(shipment.getQty() == null ? 0 : shipment.getQty()))
                    .setScale(2, java.math.RoundingMode.HALF_UP));
        }
        if (shipment.getSeaFreight() == null)
        {
            shipment.setSeaFreight(BigDecimal.ZERO);
        }
        if (shipment.getShipId() == null)
        {
            shipment.setCreateBy(SecurityUtils.getUsername());
            shipmentMapper.insertShipment(shipment);
            autoOutbound(shipment);
            return 1;
        }
        BizShipment old = shipmentMapper.selectShipmentById(shipment.getShipId());
        if (old != null && "9".equals(old.getStatus()))
        {
            throw new ServiceException("该出货单已作废，不能修改；如填错请作废后重新开单");
        }
        if (old != null && old.getOutboundId() != null)
        {
            cancelOutbound(old.getOutboundId(), "出货单修改，自动作废重新出库");
        }
        shipment.setUpdateBy(SecurityUtils.getUsername());
        int rows = shipmentMapper.updateShipment(shipment);
        autoOutbound(shipment);
        return rows;
    }

    /**
     * 出货保存后按 款号+颜色+尺码 自动匹配商品，生成成品出库单并扣减成品库存
     */
    private void autoOutbound(BizShipment shipment)
    {
        if (shipment.getQty() == null || shipment.getQty() <= 0)
        {
            return;
        }
        if (StringUtils.isEmpty(shipment.getStyleNo()))
        {
            throw new ServiceException("出货单必须填写款号，系统会按款号/颜色/尺码自动匹配商品并出库");
        }
        BizSku sku = skuMapper.selectSkuByStyleColorSize(shipment.getStyleNo(), shipment.getColor(), shipment.getSize());
        if (sku == null)
        {
            throw new ServiceException("未找到商品：" + shipment.getStyleNo() + " " + shipment.getColor() + "/"
                    + shipment.getSize() + "，请先在商品资料中生成对应 SKU");
        }
        BizOutbound outbound = new BizOutbound();
        outbound.setCustomer(shipment.getCustomer());
        outbound.setOutboundDate(shipment.getShipDate());
        outbound.setRemark("出货表自动出库");
        BizOutboundItem item = new BizOutboundItem();
        item.setSkuId(sku.getSkuId());
        item.setQty(shipment.getQty());
        item.setPrice(shipment.getPrice());
        outbound.setItems(java.util.Collections.singletonList(item));
        BizOutbound saved = saveOutbound(outbound);
        shipmentMapper.updateOutboundId(shipment.getShipId(), saved.getOutboundId());
        shipment.setOutboundId(saved.getOutboundId());
        shipment.setOutboundNo(saved.getOutboundNo());
    }

    @Transactional
    public int deleteShipment(Long id)
    {
        BizShipment shipment = shipmentMapper.selectShipmentById(id);
        if (shipment != null && "9".equals(shipment.getStatus()))
        {
            throw new ServiceException("已作废的出货单需保留记录，不能删除");
        }
        if (shipment != null && shipment.getOutboundId() != null)
        {
            cancelOutbound(shipment.getOutboundId(), "删除出货单，自动作废出库并回滚库存");
        }
        return shipmentMapper.deleteShipment(id);
    }

    /**
     * 作废出货单：保留原单记录并回滚自动出库库存，填写作废原因
     */
    @Transactional
    public int voidShipment(Long id, String reason)
    {
        if (StringUtils.isEmpty(reason) || reason.trim().isEmpty())
        {
            throw new ServiceException("请填写作废原因");
        }
        BizShipment shipment = shipmentMapper.selectShipmentById(id);
        if (shipment == null)
        {
            throw new ServiceException("出货单不存在");
        }
        if ("9".equals(shipment.getStatus()))
        {
            throw new ServiceException("该出货单已作废");
        }
        if (shipment.getOutboundId() != null)
        {
            cancelOutbound(shipment.getOutboundId(), "出货单作废，回滚自动出库：" + reason.trim());
        }
        return shipmentMapper.voidShipment(id, reason.trim(), SecurityUtils.getUsername());
    }

    /* ---------------- 盘点 ---------------- */
    public List<BizCheck> selectCheckList(BizCheck check)
    {
        return checkMapper.selectCheckList(check);
    }

    public BizCheck selectCheckById(Long id)
    {
        BizCheck check = checkMapper.selectCheckById(id);
        if (check != null)
        {
            check.setItems(checkMapper.selectItems(id));
        }
        return check;
    }

    @Transactional
    public BizCheck createCheck(BizCheck check, String stockType)
    {
        if (StringUtils.isEmpty(check.getCheckNo()))
        {
            String prefix = "PD" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
            check.setCheckNo(prefix + String.format("%03d", checkMapper.maxSeq(prefix) + 1));
        }
        check.setStatus("0");
        check.setCreateBy(SecurityUtils.getUsername());
        if (StringUtils.isEmpty(check.getChecker()))
        {
            check.setChecker(SecurityUtils.getUsername());
        }
        checkMapper.insertCheck(check);
        // 盘点单覆盖全部在售 SKU：无库存记录的 SKU 账面数按 0 生成，避免漏盘
        BizInventory query = new BizInventory();
        query.setStockType(stockType);
        Map<Long, Integer> bookQtyMap = new HashMap<>();
        for (BizInventory inv : inventoryMapper.selectInventoryList(query))
        {
            bookQtyMap.merge(inv.getSkuId(), inv.getQty() == null ? 0 : inv.getQty(), Integer::sum);
        }
        BizSku skuQuery = new BizSku();
        skuQuery.setStatus("0");
        for (BizSku sku : skuMapper.selectSkuList(skuQuery))
        {
            int bookQty = bookQtyMap.getOrDefault(sku.getSkuId(), 0);
            BizCheckItem item = new BizCheckItem();
            item.setCheckId(check.getCheckId());
            item.setSkuId(sku.getSkuId());
            item.setStockType(stockType);
            item.setBookQty(bookQty);
            item.setActualQty(bookQty);
            item.setDiffQty(0);
            checkMapper.insertItem(item);
        }
        return check;
    }

    @Transactional
    public int saveCheckItems(Long checkId, List<BizCheckItem> items)
    {
        for (BizCheckItem item : items)
        {
            int actual = item.getActualQty() == null ? 0 : item.getActualQty();
            item.setDiffQty(actual - (item.getBookQty() == null ? 0 : item.getBookQty()));
            checkMapper.updateItem(item);
        }
        return 1;
    }

    @Transactional
    public int completeCheck(Long checkId)
    {
        BizCheck check = checkMapper.selectCheckById(checkId);
        if (check == null)
        {
            throw new ServiceException("盘点单不存在");
        }
        if ("1".equals(check.getStatus()))
        {
            throw new ServiceException("盘点单已完成");
        }
        for (BizCheckItem item : checkMapper.selectItems(checkId))
        {
            stockService.adjustTo(item.getSkuId(), item.getStockType(), item.getActualQty(),
                    "盘点调整", check.getCheckNo(), "盘点差异调整");
        }
        return checkMapper.updateStatus(checkId, "1");
    }

    @Transactional
    public int deleteCheck(Long id)
    {
        checkMapper.deleteItems(id);
        return checkMapper.deleteCheck(id);
    }

    /* ---------------- 退货 ---------------- */
    public List<BizReturn> selectReturnList(BizReturn query)
    {
        return returnMapper.selectReturnList(query);
    }

    @Transactional
    public int saveReturn(BizReturn ret)
    {
        checkCustomer(ret.getCustomer());
        BizSku sku = ret.getSkuId() != null ? skuMapper.selectSkuById(ret.getSkuId())
                : (StringUtils.isEmpty(ret.getBarcode()) ? null : skuMapper.selectSkuByBarcode(ret.getBarcode()));
        if (sku == null)
        {
            throw new ServiceException("请选择退货商品");
        }
        if (ret.getQty() == null || ret.getQty() <= 0)
        {
            throw new ServiceException("退货数量必须大于0");
        }
        int good = ret.getGoodQty() == null ? 0 : ret.getGoodQty();
        int bad = ret.getBadQty() == null ? 0 : ret.getBadQty();
        if (good + bad != ret.getQty())
        {
            throw new ServiceException("好货数 + 次品数 必须等于退货数量");
        }
        ret.setSkuId(sku.getSkuId());
        ret.setStyleNo(sku.getStyleNo());
        ret.setColor(sku.getColor());
        ret.setSize(sku.getSize());
        if (StringUtils.isEmpty(ret.getReturnNo()))
        {
            String prefix = "TH" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
            ret.setReturnNo(prefix + String.format("%03d", returnMapper.maxSeq(prefix) + 1));
        }
        if (StringUtils.isEmpty(ret.getOperator()))
        {
            ret.setOperator(SecurityUtils.getUsername());
        }
        if (good > 0)
        {
            stockService.change(sku.getSkuId(), GarmentStockService.FINISH, good, "退货入库",
                    ret.getReturnNo(), "", StringUtils.isEmpty(ret.getReason()) ? "客户退货" : ret.getReason());
        }
        if (bad > 0)
        {
            BizDefect defect = new BizDefect();
            defect.setSkuId(sku.getSkuId());
            defect.setQty(bad);
            defect.setDefectType("退货次品");
            defect.setHandler(ret.getOperator());
            defect.setRemark(StringUtils.isEmpty(ret.getReason()) ? "客户退货次品" : ret.getReason());
            defectMapper.insertDefect(defect);
        }
        return returnMapper.insertReturn(ret);
    }

    @Transactional
    public int deleteReturn(Long id)
    {
        BizReturn ret = returnMapper.selectReturnById(id);
        if (ret == null)
        {
            return 0;
        }
        int good = ret.getGoodQty() == null ? 0 : ret.getGoodQty();
        if (good > 0)
        {
            stockService.change(ret.getSkuId(), GarmentStockService.FINISH, -good, "退货作废",
                    ret.getReturnNo(), "", "删除退货单，回滚库存");
        }
        return returnMapper.deleteReturn(id);
    }

    private int toInt(Object value)
    {
        if (value == null)
        {
            return 0;
        }
        return ((Number) value).intValue();
    }

    /** 客户必须来自客户档案，避免同一客户多种写法导致按客户统计分裂 */
    private void checkCustomer(String customer)
    {
        if (StringUtils.isEmpty(customer))
        {
            return;
        }
        String name = customer.trim();
        if (customerMapper.selectByName(name) == null)
        {
            throw new ServiceException("客户「" + name + "」不在客户档案中，请先在 商品资料-客户管理 新增");
        }
    }
}
