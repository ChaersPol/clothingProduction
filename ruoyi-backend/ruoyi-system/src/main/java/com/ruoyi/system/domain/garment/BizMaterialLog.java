package com.ruoyi.system.domain.garment;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 物资出入库流水 biz_material_log
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizMaterialLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long logId;

    private Long materialId;

    private String materialName;

    private String spec;

    private String unit;

    private BigDecimal changeQty;

    private BigDecimal beforeQty;

    private BigDecimal afterQty;

    /** 采购入库 / 生产领料 / 领料退回 / 盘点调整 */
    private String bizType;

    private String bizNo;

    private String operator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operateTime;
}
