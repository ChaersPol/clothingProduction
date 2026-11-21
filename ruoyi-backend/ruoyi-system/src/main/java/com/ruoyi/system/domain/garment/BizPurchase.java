package com.ruoyi.system.domain.garment;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 采购单对象 biz_purchase
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizPurchase extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long purchaseId;

    @Excel(name = "采购单号")
    private String purchaseNo;

    /** COMMON 公用耗材 / MATERIAL 按款物料 */
    private String purchaseType;

    @Excel(name = "类别")
    private String category;

    @Excel(name = "款号")
    private String styleNo;

    private Long supplierId;

    @Excel(name = "供应商")
    private String supplierName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "采购日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date purchaseDate;

    @Excel(name = "采购金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal totalAmount;

    @Excel(name = "已付金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal paidAmount;

    /** 0待入库 1部分入库 2已入库 */
    private String status;

    private String statusLabel;

    /** 物料名称（采购明细查询条件） */
    private String materialName;

    private List<BizPurchaseItem> items;
}
