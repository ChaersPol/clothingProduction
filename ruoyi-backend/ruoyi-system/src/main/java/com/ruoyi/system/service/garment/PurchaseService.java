package com.ruoyi.system.service.garment;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.garment.BizPurchase;
import com.ruoyi.system.domain.garment.BizPurchaseItem;
import com.ruoyi.system.domain.garment.BizSupplier;
import com.ruoyi.system.domain.garment.BizMaterial;
import com.ruoyi.system.mapper.garment.BizMaterialMapper;
import com.ruoyi.system.mapper.garment.BizPurchaseMapper;
import com.ruoyi.system.mapper.garment.BizSupplierMapper;

/**
 * 采购管理服务
 */
@Service
public class PurchaseService
{
    @Autowired
    private BizPurchaseMapper purchaseMapper;
    @Autowired
    private BizSupplierMapper supplierMapper;
    @Autowired
    private BizMaterialMapper materialMapper;
    @Autowired
    private MaterialService materialService;

    public List<BizPurchase> selectList(BizPurchase purchase)
    {
        return purchaseMapper.selectPurchaseList(purchase);
    }

    public BizPurchase selectById(Long id)
    {
        BizPurchase purchase = purchaseMapper.selectPurchaseById(id);
        if (purchase != null)
        {
            purchase.setItems(purchaseMapper.selectItems(id));
        }
        return purchase;
    }

    public List<java.util.Map<String, Object>> selectItemList(BizPurchase purchase)
    {
        return purchaseMapper.selectItemList(purchase);
    }

    @Transactional
    public int save(BizPurchase purchase)
    {
        if (purchase.getItems() == null || purchase.getItems().isEmpty())
        {
            throw new ServiceException("请至少录入一条采购明细");
        }
        if (purchase.getSupplierId() != null)
        {
            BizSupplier supplier = supplierMapper.selectSupplierById(purchase.getSupplierId());
            if (supplier != null)
            {
                purchase.setSupplierName(supplier.getSupplierName());
            }
        }
        BigDecimal total = BigDecimal.ZERO;
        for (BizPurchaseItem item : purchase.getItems())
        {
            // 采购物料必须来自物资档案，规格/单位/类型以档案为准，保证入库时数量口径一致
            if (StringUtils.isEmpty(item.getMaterialName()))
            {
                throw new ServiceException("请选择采购物料");
            }
            BizMaterial material = materialMapper.selectByKey(item.getMaterialName().trim(),
                    item.getSpec() == null ? "" : item.getSpec().trim());
            if (material == null)
            {
                throw new ServiceException("物资档案中不存在「" + item.getMaterialName()
                        + (StringUtils.isEmpty(item.getSpec()) ? "" : " / " + item.getSpec()) + "」，请先到「物资库存」新增该物资");
            }
            item.setMaterialName(material.getMaterialName());
            item.setSpec(material.getSpec());
            item.setUnit(material.getUnit());
            item.setMaterialType(material.getMaterialType());
            if (item.getPrice() == null)
            {
                item.setPrice(BigDecimal.ZERO);
            }
            if (item.getQty() == null)
            {
                item.setQty(BigDecimal.ZERO);
            }
            BigDecimal amount = item.getPrice().multiply(item.getQty()).setScale(2, java.math.RoundingMode.HALF_UP);
            item.setAmount(amount);
            total = total.add(amount);
        }
        purchase.setTotalAmount(total.setScale(2, java.math.RoundingMode.HALF_UP));
        if (purchase.getPaidAmount() == null)
        {
            purchase.setPaidAmount(BigDecimal.ZERO);
        }
        if (purchase.getPurchaseType() == null)
        {
            purchase.setPurchaseType("COMMON");
        }
        purchase.setStatus(calcStatus(purchase.getItems()));
        if (purchase.getPurchaseId() == null)
        {
            if (StringUtils.isEmpty(purchase.getPurchaseNo()))
            {
                String prefix = "CG" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
                purchase.setPurchaseNo(prefix + String.format("%03d", purchaseMapper.maxSeq(prefix) + 1));
            }
            purchase.setCreateBy(SecurityUtils.getUsername());
            purchaseMapper.insertPurchase(purchase);
        }
        else
        {
            purchaseMapper.deleteItems(purchase.getPurchaseId());
            purchase.setUpdateBy(SecurityUtils.getUsername());
            purchaseMapper.updatePurchase(purchase);
        }
        for (BizPurchaseItem item : purchase.getItems())
        {
            item.setPurchaseId(purchase.getPurchaseId());
            if (item.getReceivedQty() == null)
            {
                item.setReceivedQty(BigDecimal.ZERO);
            }
            purchaseMapper.insertItem(item);
        }
        return 1;
    }

    /**
     * 采购入库（可分批）
     */
    @Transactional
    public int receive(Long itemId, BigDecimal qty, Long purchaseId)
    {
        if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("入库数量必须大于0");
        }
        BizPurchaseItem item = purchaseMapper.selectItemById(itemId);
        if (item == null)
        {
            throw new ServiceException("采购明细不存在");
        }
        BigDecimal received = item.getReceivedQty() == null ? BigDecimal.ZERO : item.getReceivedQty();
        BigDecimal orderQty = item.getQty() == null ? BigDecimal.ZERO : item.getQty();
        BigDecimal remaining = orderQty.subtract(received);
        if (remaining.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("该明细已全部入库，不能继续入库");
        }
        if (qty.compareTo(remaining) > 0)
        {
            throw new ServiceException("本次入库数量超过剩余可入库数量（剩余 " + remaining.stripTrailingZeros().toPlainString() + "）");
        }
        purchaseMapper.receiveItem(itemId, qty);
        // 采购入库自动增加物资库存并记流水
        BizPurchase purchase = purchaseMapper.selectPurchaseById(purchaseId);
        materialService.stockChange(item.getMaterialName(), item.getSpec(), item.getUnit(), item.getMaterialType(), qty,
                "采购入库", purchase == null ? "" : purchase.getPurchaseNo(),
                purchase == null ? "" : "供应商：" + (purchase.getSupplierName() == null ? "" : purchase.getSupplierName()),
                item.getPrice());
        List<BizPurchaseItem> items = purchaseMapper.selectItems(purchaseId);
        purchaseMapper.updateStatus(purchaseId, calcStatus(items));
        return 1;
    }

    private String calcStatus(List<BizPurchaseItem> items)
    {
        boolean all = true;
        boolean any = false;
        for (BizPurchaseItem item : items)
        {
            BigDecimal received = item.getReceivedQty() == null ? BigDecimal.ZERO : item.getReceivedQty();
            BigDecimal qty = item.getQty() == null ? BigDecimal.ZERO : item.getQty();
            if (received.compareTo(qty) >= 0)
            {
                any = true;
            }
            else
            {
                all = false;
                if (received.compareTo(BigDecimal.ZERO) > 0)
                {
                    any = true;
                }
            }
        }
        return all && any ? "2" : (any ? "1" : "0");
    }

    @Transactional
    public int delete(Long id)
    {
        purchaseMapper.deleteItems(id);
        return purchaseMapper.deletePurchase(id);
    }
}
