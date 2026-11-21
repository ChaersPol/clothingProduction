package com.ruoyi.system.service.garment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.garment.*;
import com.ruoyi.system.mapper.garment.*;

/**
 * 裁床管理服务：开单 -> 分扎 -> 裁片入库
 */
@Service
public class CuttingService
{
    @Autowired
    private BizCutOrderMapper cutOrderMapper;
    @Autowired
    private BizBundleMapper bundleMapper;
    @Autowired
    private BizSkuMapper skuMapper;
    @Autowired
    private BizDefectMapper defectMapper;
    @Autowired
    private BizBomMapper bomMapper;
    @Autowired
    private BizMaterialMapper materialMapper;
    @Autowired
    private MaterialService materialService;
    @Autowired
    private GarmentStockService stockService;

    public List<BizCutOrder> selectList(BizCutOrder order)
    {
        return cutOrderMapper.selectCutOrderList(order);
    }

    public BizCutOrder selectById(Long id)
    {
        BizCutOrder order = cutOrderMapper.selectCutOrderById(id);
        if (order != null)
        {
            order.setItems(cutOrderMapper.selectItems(id));
        }
        return order;
    }

    @Transactional
    public int save(BizCutOrder order)
    {
        if (order.getItems() == null || order.getItems().isEmpty())
        {
            throw new ServiceException("请录入层次明细");
        }
        int total = 0;
        int maxLayer = 0;
        for (BizCutOrderItem item : order.getItems())
        {
            if (item.getQty() == null)
            {
                item.setQty(0);
            }
            total += item.getQty();
            if (item.getLayerNo() != null && item.getLayerNo() > maxLayer)
            {
                maxLayer = item.getLayerNo();
            }
        }
        order.setTotalQty(total);
        order.setLayerCount(maxLayer);
        if (StringUtils.isEmpty(order.getStatus()))
        {
            order.setStatus("0");
        }
        boolean isNew = order.getCutId() == null;
        if (order.getCutId() == null)
        {
            if (StringUtils.isEmpty(order.getCutNo()))
            {
                order.setCutNo("CH" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE)
                        + String.format("%03d", cutOrderMapper.countToday() + 1));
            }
            order.setCreateBy(SecurityUtils.getUsername());
            cutOrderMapper.insertCutOrder(order);
        }
        else
        {
            cutOrderMapper.deleteItems(order.getCutId());
            order.setUpdateBy(SecurityUtils.getUsername());
            cutOrderMapper.updateCutOrder(order);
        }
        for (BizCutOrderItem item : order.getItems())
        {
            item.setCutId(order.getCutId());
            cutOrderMapper.insertItem(item);
        }
        // 新建裁货单时按 BOM 用量 × 件数自动扣减物资库存
        if (isNew)
        {
            changeBomStock(order, -1);
        }
        return 1;
    }

    /**
     * 按 BOM 用量自动增减物资库存：sign=-1 领料扣减，sign=1 领料退回
     */
    private void changeBomStock(BizCutOrder order, int sign)
    {
        if (StringUtils.isEmpty(order.getStyleNo()) || order.getTotalQty() == null || order.getTotalQty() <= 0)
        {
            return;
        }
        BizBom query = new BizBom();
        query.setStyleNo(order.getStyleNo());
        for (BizBom bom : bomMapper.selectBomList(query))
        {
            if (bom.getQtyPerPiece() == null || StringUtils.isEmpty(bom.getMaterialName()))
            {
                continue;
            }
            BigDecimal loss = bom.getLossRate() == null ? BigDecimal.ZERO : bom.getLossRate();
            BigDecimal need = bom.getQtyPerPiece().multiply(new BigDecimal(order.getTotalQty()))
                    .multiply(BigDecimal.ONE.add(loss.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)))
                    .setScale(2, RoundingMode.HALF_UP);
            if (need.compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            materialService.stockChange(bom.getMaterialName(), bom.getSpec(), bom.getUnit(), bom.getMaterialType(),
                    need.multiply(new BigDecimal(sign)), sign < 0 ? "生产领料" : "领料退回", order.getCutNo(),
                    "裁货单 " + order.getCutNo() + "（" + order.getStyleNo() + " " + order.getTotalQty() + "件）");
        }
    }

    @Transactional
    public int delete(Long id)
    {
        if (bundleMapper.countByCut(id) > 0)
        {
            throw new ServiceException("该裁货单已分扎，不能删除");
        }
        BizCutOrder order = cutOrderMapper.selectCutOrderById(id);
        cutOrderMapper.deleteItems(id);
        int rows = cutOrderMapper.deleteCutOrder(id);
        // 删除未分扎的裁货单时，已领用的物资自动退回库存
        if (order != null && !StringUtils.isEmpty(order.getCutNo())
                && materialMapper.countLog(order.getCutNo(), "生产领料") > 0)
        {
            changeBomStock(order, 1);
        }
        return rows;
    }

    /**
     * 按层次明细自动分扎，每扎默认17件，余数单独成扎
     */
    @Transactional
    public int autoBundle(Long cutId, int perBundle)
    {
        BizCutOrder order = cutOrderMapper.selectCutOrderById(cutId);
        if (order == null)
        {
            throw new ServiceException("裁货单不存在");
        }
        if (perBundle <= 0)
        {
            perBundle = 17;
        }
        List<BizCutOrderItem> items = cutOrderMapper.selectItems(cutId);
        List<BizBundle> exists = bundleMapper.selectByCutId(cutId);
        int count = 0;
        for (BizCutOrderItem item : items)
        {
            int already = 0;
            for (BizBundle b : exists)
            {
                if (item.getColor().equals(b.getColor()) && item.getSize().equals(b.getSize()))
                {
                    already += b.getQty();
                }
            }
            int remaining = item.getQty() - already;
            while (remaining > 0)
            {
                int qty = Math.min(perBundle, remaining);
                addBundleInternal(order, item.getColor(), item.getSize(), qty);
                remaining -= qty;
                count++;
            }
        }
        return count;
    }

    /**
     * 手工新增一扎
     */
    @Transactional
    public BizBundle addBundle(Long cutId, String color, String size, Integer qty)
    {
        BizCutOrder order = cutOrderMapper.selectCutOrderById(cutId);
        if (order == null)
        {
            throw new ServiceException("裁货单不存在");
        }
        if (qty == null || qty <= 0)
        {
            throw new ServiceException("数量必须大于0");
        }
        return addBundleInternal(order, color, size, qty);
    }

    private BizBundle addBundleInternal(BizCutOrder order, String color, String size, int qty)
    {
        BizSku sku = null;
        for (BizSku s : skuMapper.selectSkuByStyleNo(order.getStyleNo()))
        {
            if (s.getColor().equals(color) && s.getSize().equals(size))
            {
                sku = s;
                break;
            }
        }
        if (sku == null)
        {
            throw new ServiceException("未找到商品资料：" + order.getStyleNo() + "-" + color + "-" + size + "，请先生成SKU");
        }
        String no = "Z" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE)
                + String.format("%04d", bundleMapper.countAll() + 1);
        BizBundle bundle = new BizBundle();
        bundle.setBundleNo(no);
        bundle.setBarcode(no);
        bundle.setCutId(order.getCutId());
        bundle.setCutNo(order.getCutNo());
        bundle.setSkuId(sku.getSkuId());
        bundle.setStyleNo(order.getStyleNo());
        bundle.setCnName(order.getCnName());
        bundle.setColor(color);
        bundle.setSize(size);
        bundle.setQty(qty);
        bundle.setDeliveredQty(0);
        bundle.setPackedQty(0);
        bundle.setDefectQty(0);
        bundle.setStatus("0");
        bundle.setCreateBy(SecurityUtils.getUsername());
        bundleMapper.insertBundle(bundle);
        return bundle;
    }

    /**
     * 扫扎卡，裁片入库（红色）
     */
    @Transactional
    public BizBundle stockIn(String code)
    {
        BizBundle bundle = bundleMapper.selectByCode(code);
        if (bundle == null)
        {
            throw new ServiceException("扎卡不存在：" + code);
        }
        if (!"0".equals(bundle.getStatus()))
        {
            throw new ServiceException("该扎已入库或已流转，不能重复入库");
        }
        stockService.change(bundle.getSkuId(), GarmentStockService.CUT, bundle.getQty(), "裁片入库",
                bundle.getCutNo(), bundle.getBundleNo(), "裁片扫码入库");
        bundle.setStatus("1");
        bundleMapper.updatePickup(bundle.getBundleId(), "", "1");
        BizCutOrder order = cutOrderMapper.selectCutOrderById(bundle.getCutId());
        if (order != null && bundleMapper.countNotStocked(order.getCutId()) == 0)
        {
            cutOrderMapper.updateStatus(order.getCutId(), "1");
        }
        return bundle;
    }

    /**
     * 补片 / 报废
     */
    @Transactional
    public int patch(Long bundleId, Integer qty, String defectType, String handler, String remark)
    {
        BizBundle bundle = bundleMapper.selectBundleById(bundleId);
        if (bundle == null)
        {
            throw new ServiceException("扎不存在");
        }
        if (qty == null || qty <= 0)
        {
            throw new ServiceException("数量必须大于0");
        }
        if ("补片".equals(defectType))
        {
            stockService.change(bundle.getSkuId(), GarmentStockService.CUT, qty, "补片入库",
                    bundle.getCutNo(), bundle.getBundleNo(), "补片");
        }
        else if ("报废".equals(defectType))
        {
            stockService.change(bundle.getSkuId(), GarmentStockService.CUT, -qty, "报废出库",
                    bundle.getCutNo(), bundle.getBundleNo(), "裁片报废");
        }
        BizDefect defect = new BizDefect();
        defect.setBundleId(bundleId);
        defect.setBundleNo(bundle.getBundleNo());
        defect.setSkuId(bundle.getSkuId());
        defect.setQty(qty);
        defect.setDefectType(defectType);
        defect.setHandler(StringUtils.isEmpty(handler) ? SecurityUtils.getUsername() : handler);
        defect.setRemark(remark);
        return defectMapper.insertDefect(defect);
    }

    public int deleteBundle(Long bundleId)
    {
        BizBundle bundle = bundleMapper.selectBundleById(bundleId);
        if (bundle == null)
        {
            return 0;
        }
        if (!"0".equals(bundle.getStatus()))
        {
            throw new ServiceException("已入库的扎不能删除");
        }
        return bundleMapper.deleteBundle(bundleId);
    }

    public List<BizBundle> selectBundles(BizBundle bundle)
    {
        return bundleMapper.selectBundleList(bundle);
    }

    public BizBundle selectBundleByCode(String code)
    {
        return bundleMapper.selectByCode(code);
    }

    public BizBundle selectBundleById(Long bundleId)
    {
        return bundleMapper.selectBundleById(bundleId);
    }
}
