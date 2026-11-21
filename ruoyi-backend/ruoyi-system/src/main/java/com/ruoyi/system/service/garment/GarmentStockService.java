package com.ruoyi.system.service.garment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.garment.BizInventory;
import com.ruoyi.system.domain.garment.BizSku;
import com.ruoyi.system.domain.garment.BizStockLog;
import com.ruoyi.system.mapper.garment.BizInventoryMapper;
import com.ruoyi.system.mapper.garment.BizSkuMapper;
import com.ruoyi.system.mapper.garment.BizStockLogMapper;

/**
 * 三色库存流转核心服务：裁片(红) -> 产线(蓝) -> 成品(黑)
 */
@Service
public class GarmentStockService
{
    public static final String CUT = "CUT";
    public static final String LINE = "LINE";
    public static final String FINISH = "FINISH";

    @Autowired
    private BizInventoryMapper inventoryMapper;
    @Autowired
    private BizSkuMapper skuMapper;
    @Autowired
    private BizStockLogMapper logMapper;

    /**
     * 库存变动（正数入库、负数出库），带行锁，保证多人并发不出错
     */
    @Transactional
    public void change(Long skuId, String stockType, int delta, String bizType, String bizNo, String bundleNo, String remark)
    {
        if (delta == 0)
        {
            return;
        }
        BizSku sku = skuMapper.selectSkuById(skuId);
        if (sku == null)
        {
            throw new ServiceException("商品不存在，无法变更库存");
        }
        BizInventory row = inventoryMapper.lockRow(skuId, stockType);
        int before = row == null ? 0 : row.getQty();
        int after = before + delta;
        if (after < 0)
        {
            throw new ServiceException("库存不足：" + sku.getStyleNo() + " " + sku.getColor() + " " + sku.getSize()
                    + " 当前" + before + "，需扣减" + (-delta));
        }
        if (row == null)
        {
            inventoryMapper.insertRow(skuId, stockType, after);
        }
        else
        {
            inventoryMapper.updateQty(skuId, stockType, after);
        }
        writeLog(sku, stockType, delta, before, after, bizType, bizNo, bundleNo, remark);
    }

    /**
     * 盘点：把库存直接调整到实际数量
     */
    @Transactional
    public void adjustTo(Long skuId, String stockType, int actualQty, String bizType, String bizNo, String remark)
    {
        BizSku sku = skuMapper.selectSkuById(skuId);
        if (sku == null)
        {
            throw new ServiceException("商品不存在");
        }
        BizInventory row = inventoryMapper.lockRow(skuId, stockType);
        int before = row == null ? 0 : row.getQty();
        if (actualQty < 0)
        {
            throw new ServiceException("实盘数量不能小于0");
        }
        if (row == null)
        {
            inventoryMapper.insertRow(skuId, stockType, actualQty);
        }
        else
        {
            inventoryMapper.updateQty(skuId, stockType, actualQty);
        }
        if (actualQty != before)
        {
            writeLog(sku, stockType, actualQty - before, before, actualQty, bizType, bizNo, "", remark);
        }
    }

    public int getQty(Long skuId, String stockType)
    {
        BizInventory row = inventoryMapper.selectRow(skuId, stockType);
        return row == null ? 0 : row.getQty();
    }

    private void writeLog(BizSku sku, String stockType, int delta, int before, int after, String bizType,
            String bizNo, String bundleNo, String remark)
    {
        BizStockLog log = new BizStockLog();
        log.setSkuId(sku.getSkuId());
        log.setStyleNo(sku.getStyleNo());
        log.setCnName(sku.getCnName());
        log.setColor(sku.getColor());
        log.setSize(sku.getSize());
        log.setStockType(stockType);
        log.setChangeQty(delta);
        log.setBeforeQty(before);
        log.setAfterQty(after);
        log.setBizType(bizType);
        log.setBizNo(bizNo);
        log.setBundleNo(bundleNo);
        log.setOperator(SecurityUtils.getUsername());
        log.setRemark(remark);
        logMapper.insertLog(log);
    }
}
