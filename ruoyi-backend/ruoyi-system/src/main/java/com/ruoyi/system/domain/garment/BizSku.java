package com.ruoyi.system.domain.garment;

import java.math.BigDecimal;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品SKU对象 biz_sku
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizSku extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long skuId;

    private Long styleId;

    @Excel(name = "款号")
    private String styleNo;

    private String parentStyleNo;

    @Excel(name = "品名")
    private String cnName;

    @Excel(name = "颜色")
    private String color;

    @Excel(name = "尺码")
    private String size;

    /** 商品条码 */
    @Excel(name = "条码")
    private String barcode;

    @Excel(name = "成本价", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal costPrice;

    @Excel(name = "销售价", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal salePrice;

    @Excel(name = "最低库存")
    private Integer minStock;

    private String status;

    /** 裁片库存(红) */
    @Excel(name = "裁片")
    private Integer cutQty;

    /** 产线库存(蓝) */
    @Excel(name = "产线")
    private Integer lineQty;

    /** 成品库存(黑) */
    @Excel(name = "成品")
    private Integer finishQty;

    /** 总库存 */
    
    private Integer totalQty;

    /** 库存货值 */
    
    private BigDecimal stockValue;
}
