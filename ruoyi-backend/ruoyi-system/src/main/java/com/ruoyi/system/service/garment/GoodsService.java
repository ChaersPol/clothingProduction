package com.ruoyi.system.service.garment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.garment.*;
import com.ruoyi.system.mapper.garment.*;

/**
 * 商品资料 + 研发设计 服务
 */
@Service
public class GoodsService
{
    @Autowired
    private BizStyleMapper styleMapper;
    @Autowired
    private BizColorMapper colorMapper;
    @Autowired
    private BizSizeMapper sizeMapper;
    @Autowired
    private BizSkuMapper skuMapper;
    @Autowired
    private BizBomMapper bomMapper;
    @Autowired
    private BizCostMapper costMapper;
    @Autowired
    private BizMaterialMapper materialMapper;
    @Autowired
    private GarmentStockService stockService;

    /* ---------------- 款号 ---------------- */
    public List<BizStyle> selectStyleList(BizStyle style)
    {
        return styleMapper.selectStyleList(style);
    }

    public BizStyle selectStyleById(Long id)
    {
        return styleMapper.selectStyleById(id);
    }

    public int saveStyle(BizStyle style)
    {
        // 尺码表只允许基础资料里已维护的尺码，并按尺码排序统一存储
        if (StringUtils.isNotEmpty(style.getSizeTable()))
        {
            style.setSizeTable(normalizeSizeTable(style.getSizeTable()));
        }
        if (StringUtils.isEmpty(style.getParentStyleNo()))
        {
            style.setParentStyleNo(style.getStyleNo());
        }
        if (StringUtils.isEmpty(style.getStatus()))
        {
            style.setStatus("0");
        }
        if (style.getStyleId() == null)
        {
            if (styleMapper.selectStyleByNo(style.getStyleNo()) != null)
            {
                throw new ServiceException("款号已存在：" + style.getStyleNo());
            }
            style.setCreateBy(SecurityUtils.getUsername());
            return styleMapper.insertStyle(style);
        }
        BizStyle old = styleMapper.selectStyleByNo(style.getStyleNo());
        if (old != null && !old.getStyleId().equals(style.getStyleId()))
        {
            throw new ServiceException("款号已存在：" + style.getStyleNo());
        }
        style.setUpdateBy(SecurityUtils.getUsername());
        return styleMapper.updateStyle(style);
    }

    public int deleteStyle(Long id)
    {
        BizStyle style = styleMapper.selectStyleById(id);
        if (style == null)
        {
            return 0;
        }
        if (skuMapper.countByStyleNo(style.getStyleNo()) > 0)
        {
            throw new ServiceException("该款号已生成商品，不能删除");
        }
        return styleMapper.deleteStyleById(id);
    }

    /* ---------------- 颜色 / 尺码 ---------------- */
    public List<BizColor> selectColorList(BizColor color)
    {
        return colorMapper.selectColorList(color);
    }

    public int saveColor(BizColor color)
    {
        if (color.getColorId() == null)
        {
            color.setCreateBy(SecurityUtils.getUsername());
            return colorMapper.insertColor(color);
        }
        color.setUpdateBy(SecurityUtils.getUsername());
        return colorMapper.updateColor(color);
    }

    public int deleteColor(Long id)
    {
        BizColor color = colorMapper.selectColorById(id);
        if (color == null)
        {
            return 0;
        }
        int used = skuMapper.countByColor(color.getCnName());
        if (used > 0)
        {
            throw new ServiceException("颜色「" + color.getCnName() + "」已被 " + used + " 个商品使用，不能删除");
        }
        return colorMapper.deleteColorById(id);
    }

    public List<BizSize> selectSizeList(BizSize size)
    {
        return sizeMapper.selectSizeList(size);
    }

    public int saveSize(BizSize size)
    {
        if (size.getSizeId() == null)
        {
            size.setCreateBy(SecurityUtils.getUsername());
            return sizeMapper.insertSize(size);
        }
        size.setUpdateBy(SecurityUtils.getUsername());
        return sizeMapper.updateSize(size);
    }

    public int deleteSize(Long id)
    {
        BizSize size = sizeMapper.selectSizeById(id);
        if (size == null)
        {
            return 0;
        }
        int used = skuMapper.countBySize(size.getSizeName());
        if (used > 0)
        {
            throw new ServiceException("尺码「" + size.getSizeName() + "」已被 " + used + " 个商品使用，不能删除");
        }
        int styles = styleMapper.countBySizeTable(size.getSizeName());
        if (styles > 0)
        {
            throw new ServiceException("尺码「" + size.getSizeName() + "」仍被 " + styles + " 个款号的尺码表引用，请先修改这些款号的尺码表");
        }
        return sizeMapper.deleteSizeById(id);
    }

    /**
     * 款号尺码表规范化：只允许基础资料尺码管理中启用的尺码，并按尺码排序输出逗号串
     */
    private String normalizeSizeTable(String sizeTable)
    {
        String[] tokens = sizeTable.split("[,，、;；\\s]+");
        Set<String> requested = new HashSet<>();
        for (String t : tokens)
        {
            if (StringUtils.isNotEmpty(t.trim()))
            {
                requested.add(t.trim());
            }
        }
        if (requested.isEmpty())
        {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Set<String> matched = new HashSet<>();
        for (BizSize s : sizeMapper.selectSizeAll())
        {
            if (requested.contains(s.getSizeName()))
            {
                if (sb.length() > 0)
                {
                    sb.append(",");
                }
                sb.append(s.getSizeName());
                matched.add(s.getSizeName());
            }
        }
        requested.removeAll(matched);
        if (!requested.isEmpty())
        {
            throw new ServiceException("尺码「" + String.join("、", requested) + "」不在基础资料尺码管理中，请先到基础资料新增该尺码");
        }
        return sb.toString();
    }

    /* ---------------- SKU ---------------- */
    public List<BizSku> selectSkuList(BizSku sku)
    {
        return skuMapper.selectSkuList(sku);
    }

    public BizSku selectSkuById(Long id)
    {
        return skuMapper.selectSkuById(id);
    }

    public BizSku selectSkuByBarcode(String barcode)
    {
        return skuMapper.selectSkuByBarcode(barcode);
    }

    public int saveSku(BizSku sku)
    {
        if (StringUtils.isEmpty(sku.getBarcode()))
        {
            sku.setBarcode(sku.getStyleNo() + "-" + sku.getColor() + "-" + sku.getSize());
        }
        if (sku.getStatus() == null)
        {
            sku.setStatus("0");
        }
        if (sku.getMinStock() == null)
        {
            sku.setMinStock(0);
        }
        if (sku.getSkuId() == null)
        {
            BizSku exist = skuMapper.selectSkuByBarcode(sku.getBarcode());
            if (exist != null)
            {
                throw new ServiceException("条码已存在：" + sku.getBarcode());
            }
            sku.setCreateBy(SecurityUtils.getUsername());
            return skuMapper.insertSku(sku);
        }
        sku.setUpdateBy(SecurityUtils.getUsername());
        return skuMapper.updateSku(sku);
    }

    public int deleteSku(Long id)
    {
        return skuMapper.deleteSkuById(id);
    }

    /**
     * 按 款号 + 颜色 + 尺码 组合批量生成 SKU（已存在则跳过）
     */
    @Transactional
    public int generateSkus(String styleNo, List<String> colors, List<String> sizes)
    {
        BizStyle style = styleMapper.selectStyleByNo(styleNo);
        if (style == null)
        {
            throw new ServiceException("款号不存在：" + styleNo);
        }
        if (colors == null || colors.isEmpty() || sizes == null || sizes.isEmpty())
        {
            throw new ServiceException("请选择颜色和尺码");
        }
        BizCost cost = costMapper.selectCostByStyleNo(styleNo);
        BigDecimal costPrice = BigDecimal.ZERO;
        BigDecimal salePrice = BigDecimal.ZERO;
        if (cost != null)
        {
            costPrice = cost.getExFactoryPrice() != null && cost.getExFactoryPrice().compareTo(BigDecimal.ZERO) > 0
                    ? cost.getExFactoryPrice() : cost.getTotalCost();
            salePrice = cost.getSuggestPrice();
        }
        Set<String> exists = new HashSet<>();
        for (BizSku s : skuMapper.selectSkuByStyleNo(styleNo))
        {
            exists.add(s.getColor() + "|" + s.getSize());
        }
        int count = 0;
        for (String color : colors)
        {
            for (String size : sizes)
            {
                String key = color + "|" + size;
                if (exists.contains(key))
                {
                    continue;
                }
                BizSku sku = new BizSku();
                sku.setStyleId(style.getStyleId());
                sku.setStyleNo(styleNo);
                sku.setParentStyleNo(StringUtils.isEmpty(style.getParentStyleNo()) ? styleNo : style.getParentStyleNo());
                sku.setCnName(style.getCnName());
                sku.setColor(color);
                sku.setSize(size);
                sku.setBarcode(styleNo + "-" + color + "-" + size);
                sku.setCostPrice(costPrice == null ? BigDecimal.ZERO : costPrice);
                sku.setSalePrice(salePrice == null ? BigDecimal.ZERO : salePrice);
                sku.setMinStock(0);
                sku.setStatus("0");
                sku.setCreateBy(SecurityUtils.getUsername());
                skuMapper.insertSku(sku);
                count++;
            }
        }
        return count;
    }

    /**
     * 初始库存一次性导入
     */
    @Transactional
    public int importInitialStock(List<BizInitialStock> rows)
    {
        // 先整体校验，全部通过后再统一入库，避免部分导入后又回滚造成提示与实际不符
        List<String> errors = new ArrayList<>();
        List<BizInitialStock> validRows = new ArrayList<>();
        List<BizSku> validSkus = new ArrayList<>();
        for (BizInitialStock row : rows)
        {
            if (StringUtils.isEmpty(row.getBarcode()) || row.getQty() == null || row.getQty() == 0)
            {
                continue;
            }
            String barcode = row.getBarcode().trim();
            if (row.getQty() < 0)
            {
                errors.add(barcode + " 数量不能为负数");
                continue;
            }
            BizSku sku = skuMapper.selectSkuByBarcode(barcode);
            if (sku == null)
            {
                errors.add(barcode + " 未找到对应商品");
                continue;
            }
            validRows.add(row);
            validSkus.add(sku);
        }
        if (!errors.isEmpty())
        {
            String detail = String.join("；", errors.subList(0, Math.min(errors.size(), 20)));
            if (errors.size() > 20)
            {
                detail += "；…等共 " + errors.size() + " 行有误";
            }
            throw new ServiceException("导入失败，未导入任何数据：" + detail);
        }
        for (int i = 0; i < validRows.size(); i++)
        {
            BizInitialStock row = validRows.get(i);
            stockService.change(validSkus.get(i).getSkuId(), GarmentStockService.FINISH, row.getQty(),
                    "初始库存", "INIT", "", StringUtils.isEmpty(row.getRemark()) ? "Excel导入初始库存" : row.getRemark());
        }
        return validRows.size();
    }

    /* ---------------- BOM ---------------- */
    public List<BizBom> selectBomList(BizBom bom)
    {
        return bomMapper.selectBomList(bom);
    }

    public int saveBom(BizBom bom)
    {
        // 物料必须来自物资档案，单位/规格/类型以档案为准，避免 BOM 与库存口径不一致
        if (StringUtils.isEmpty(bom.getMaterialName()))
        {
            throw new ServiceException("请选择物料名称");
        }
        BizMaterial material = materialMapper.selectByKey(bom.getMaterialName().trim(),
                bom.getSpec() == null ? "" : bom.getSpec().trim());
        if (material == null)
        {
            throw new ServiceException("物资档案中不存在「" + bom.getMaterialName()
                    + (StringUtils.isEmpty(bom.getSpec()) ? "" : " / " + bom.getSpec()) + "」，请先到「物资库存」新增该物资");
        }
        bom.setMaterialName(material.getMaterialName());
        bom.setSpec(material.getSpec());
        bom.setUnit(material.getUnit());
        bom.setMaterialType(material.getMaterialType());
        if (bom.getQtyPerPiece() == null)
        {
            bom.setQtyPerPiece(BigDecimal.ZERO);
        }
        if (bom.getLossRate() == null)
        {
            bom.setLossRate(BigDecimal.ZERO);
        }
        // 单价统一取物资档案价格，避免 BOM 与物资库存价格口径不一致
        bom.setUnitPrice(material.getPrice() == null ? BigDecimal.ZERO : material.getPrice());
        BigDecimal amount = bom.getQtyPerPiece()
                .multiply(BigDecimal.ONE.add(bom.getLossRate().divide(new BigDecimal("100"))))
                .multiply(bom.getUnitPrice()).setScale(2, java.math.RoundingMode.HALF_UP);
        bom.setAmount(amount);
        if (bom.getBomId() == null)
        {
            bom.setCreateBy(SecurityUtils.getUsername());
            return bomMapper.insertBom(bom);
        }
        bom.setUpdateBy(SecurityUtils.getUsername());
        return bomMapper.updateBom(bom);
    }

    public int deleteBom(Long id)
    {
        return bomMapper.deleteBomById(id);
    }

    /* ---------------- 成本 ---------------- */
    public List<BizCost> selectCostList(BizCost cost)
    {
        return costMapper.selectCostList(cost);
    }

    public BizCost selectCostByStyleNo(String styleNo)
    {
        return costMapper.selectCostByStyleNo(styleNo);
    }

    public int deleteCost(Long costId)
    {
        return costMapper.deleteCostById(costId);
    }

    public int saveCost(BizCost cost)
    {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal[] values = { cost.getFabricCost(), cost.getLiningCost(), cost.getAccessoryCost(), cost.getTrimCost(),
                cost.getLaborCost(), cost.getPatternCost(), cost.getShootCost(), cost.getOperateCost(),
                cost.getFirstFreight(), cost.getLastFreight(), cost.getSeaFreight(), cost.getOtherCost() };
        for (BigDecimal v : values)
        {
            if (v != null)
            {
                total = total.add(v);
            }
        }
        cost.setTotalCost(total.setScale(2, java.math.RoundingMode.HALF_UP));
        if (cost.getExFactoryPrice() == null || cost.getExFactoryPrice().compareTo(BigDecimal.ZERO) == 0)
        {
            cost.setExFactoryPrice(cost.getTotalCost());
        }
        if (cost.getSuggestPrice() == null || cost.getSuggestPrice().compareTo(BigDecimal.ZERO) == 0)
        {
            cost.setSuggestPrice(cost.getExFactoryPrice().multiply(new BigDecimal("1.6"))
                    .setScale(2, java.math.RoundingMode.HALF_UP));
        }
        BizCost old = costMapper.selectCostByStyleNo(cost.getStyleNo());
        if (cost.getCostId() == null)
        {
            if (old != null)
            {
                cost.setCostId(old.getCostId());
                cost.setUpdateBy(SecurityUtils.getUsername());
                costMapper.updateCost(cost);
            }
            else
            {
                cost.setCreateBy(SecurityUtils.getUsername());
                costMapper.insertCost(cost);
            }
        }
        else
        {
            cost.setUpdateBy(SecurityUtils.getUsername());
            costMapper.updateCost(cost);
        }
        // 同步已生成商品的成本价与售价
        skuMapper.updatePriceByStyleNo(cost.getStyleNo(), cost.getExFactoryPrice(), cost.getSuggestPrice());
        return 1;
    }
}
