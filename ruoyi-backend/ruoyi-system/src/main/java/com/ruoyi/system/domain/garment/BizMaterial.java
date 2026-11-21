package com.ruoyi.system.domain.garment;

import java.math.BigDecimal;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 物资档案与库存 biz_material
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizMaterial extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long materialId;

    @Excel(name = "物资编码")
    private String materialCode;

    @Excel(name = "物资名称")
    private String materialName;

    @Excel(name = "规格")
    private String spec;

    @Excel(name = "单位")
    private String unit;

    @Excel(name = "物资类型")
    private String materialType;

    @Excel(name = "当前库存")
    private BigDecimal stockQty;

    @Excel(name = "安全库存")
    private BigDecimal minStock;

    @Excel(name = "单价")
    private BigDecimal price;

    private Long supplierId;

    @Excel(name = "供应商")
    private String supplierName;

    private String delFlag;
}
