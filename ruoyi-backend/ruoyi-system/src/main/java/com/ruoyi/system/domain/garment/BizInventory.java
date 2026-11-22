package com.ruoyi.system.domain.garment;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 三色库存对象 biz_inventory（联表SKU）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizInventory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long inventoryId;
    private Long skuId;

    /** CUT 裁片 / LINE 产线 / FINISH 成品 */
    private String stockType;

    @Excel(name = "数量", isStatistics = true)
    private Integer qty;

    @Excel(name = "款号")
    private String styleNo;

    @Excel(name = "父体款号")
    private String parentStyleNo;

    @Excel(name = "中文品名")
    private String cnName;

    @Excel(name = "颜色")
    private String color;

    @Excel(name = "尺码")
    private String size;

    @Excel(name = "条码")
    private String barcode;

    private BigDecimal costPrice;
    private BigDecimal salePrice;
    private Integer minStock;

    @Excel(name = "库存货值", cellType = Excel.ColumnType.NUMERIC, isStatistics = true)
    private BigDecimal stockValue;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /** 库龄(天) */
    @Excel(name = "库龄(天)")
    private Integer ageDays;
}
