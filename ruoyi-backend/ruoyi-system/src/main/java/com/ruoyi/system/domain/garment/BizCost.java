package com.ruoyi.system.domain.garment;

import java.math.BigDecimal;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 成本核算对象 biz_cost
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizCost extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long costId;

    @Excel(name = "款号")
    private String styleNo;

    private BigDecimal fabricCost;
    private BigDecimal liningCost;
    private BigDecimal accessoryCost;
    private BigDecimal trimCost;
    private BigDecimal laborCost;
    private BigDecimal patternCost;
    private BigDecimal shootCost;
    private BigDecimal operateCost;
    private BigDecimal firstFreight;
    private BigDecimal lastFreight;
    private BigDecimal seaFreight;
    private BigDecimal otherCost;

    @Excel(name = "合计成本", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal totalCost;

    @Excel(name = "出厂价", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal exFactoryPrice;

    @Excel(name = "建议售价", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal suggestPrice;

    /** 品名 */
    private String cnName;
}
