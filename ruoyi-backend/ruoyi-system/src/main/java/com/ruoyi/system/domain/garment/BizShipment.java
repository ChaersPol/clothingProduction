package com.ruoyi.system.domain.garment;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 出货表 biz_shipment
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizShipment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long shipId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "出货日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date shipDate;

    @Excel(name = "客户")
    private String customer;

    @Excel(name = "款号")
    private String styleNo;

    @Excel(name = "颜色")
    private String color;

    @Excel(name = "尺码")
    private String size;

    @Excel(name = "数量", isStatistics = true)
    private Integer qty;

    @Excel(name = "单价", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal price;

    @Excel(name = "金额", cellType = Excel.ColumnType.NUMERIC, isStatistics = true)
    private BigDecimal amount;

    @Excel(name = "海运费", cellType = Excel.ColumnType.NUMERIC, isStatistics = true)
    private BigDecimal seaFreight;

    /** 关联的成品出库单ID（出货保存时自动生成） */
    private Long outboundId;

    @Excel(name = "自动出库单")
    private String outboundNo;
}
