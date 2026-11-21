package com.ruoyi.system.domain.garment;

import java.math.BigDecimal;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;

/**
 * 采购明细导出对象
 */
@Data
public class BizPurchaseItemExport
{
    @Excel(name = "采购单号")
    private String purchaseNo;

    @Excel(name = "类型")
    private String purchaseType;

    @Excel(name = "供应商")
    private String supplierName;

    @Excel(name = "物料类型")
    private String materialType;

    @Excel(name = "名称")
    private String materialName;

    @Excel(name = "规格")
    private String spec;

    @Excel(name = "单位")
    private String unit;

    @Excel(name = "单价", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal price;

    @Excel(name = "数量", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal qty;

    @Excel(name = "已入库", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal receivedQty;

    @Excel(name = "金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal amount;
}
