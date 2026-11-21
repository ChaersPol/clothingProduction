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
            throw new ServiceException("该款号已生成商品，不能删除，请先停用");
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
        return sizeMapper.deleteSizeById(id);
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
        int success = 0;
        List<String> errors = new ArrayList<>();
        for (BizInitialStock row : rows)
        {
            if (StringUtils.isEmpty(row.getBarcode()) || row.getQty() == null || row.getQty() == 0)
            {
                continue;
            }
            BizSku sku = skuMapper.selectSkuByBarcode(row.getBarcode().trim());
            if (sku == null)
            {
                errors.add(row.getBarcode() + " 未找到");
                continue;
            }
            stockService.change(sku.getSkuId(), GarmentStockService.FINISH, row.getQty(),
                    "初始库存", "INIT", "", StringUtils.isEmpty(row.getRemark()) ? "Excel导入初始库存" : row.getRemark());
            success++;
        }
        if (!errors.isEmpty())
        {
            throw new ServiceException("成功" + success + "条，以下条码未找到：" + String.join("、", errors));
        }
        return success;
    }

    /* ---------------- BOM ---------------- */
    public List<BizBom> selectBomList(BizBom bom)
    {
        return bomMapper.selectBomList(bom);
    }

    public int saveBom(BizBom bom)
    {
        if (bom.getQtyPerPiece() == null)
        {
            bom.setQtyPerPiece(BigDecimal.ZERO);
        }
        if (bom.getLossRate() == null)
        {
            bom.setLossRate(BigDecimal.ZERO);
        }
        if (bom.getUnitPrice() == null)
        {
            bom.setUnitPrice(BigDecimal.ZERO);
        }
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
