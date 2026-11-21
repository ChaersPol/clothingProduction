package com.ruoyi.system.domain.garment;

import java.math.BigDecimal;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用料清单对象 biz_bom
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizBom extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long bomId;

    @Excel(name = "款号")
    private String styleNo;

    @Excel(name = "物料类型")
    private String materialType;

    @Excel(name = "物料名称")
    private String materialName;

    @Excel(name = "规格")
    private String spec;

    @Excel(name = "单位")
    private String unit;

    @Excel(name = "单件用量", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal qtyPerPiece;

    @Excel(name = "损耗率%", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal lossRate;

    @Excel(name = "单价", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal unitPrice;

    @Excel(name = "小计", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal amount;
}
