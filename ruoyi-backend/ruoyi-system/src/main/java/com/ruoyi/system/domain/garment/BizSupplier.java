package com.ruoyi.system.domain.garment;

import java.math.BigDecimal;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 供应商对象 biz_supplier
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizSupplier extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long supplierId;

    @Excel(name = "供应商名称")
    private String supplierName;

    @Excel(name = "联系人")
    private String contact;

    @Excel(name = "电话")
    private String phone;

    @Excel(name = "供货内容")
    private String supplyContent;

    @Excel(name = "地址")
    private String address;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 对账：累计供货金额 */
    private BigDecimal totalAmount;
    /** 对账：累计已付 */
    private BigDecimal paidAmount;
    /** 对账：未付 */
    private BigDecimal unpaidAmount;
    /** 对账：采购单数 */
    private Integer orderCount;
}
