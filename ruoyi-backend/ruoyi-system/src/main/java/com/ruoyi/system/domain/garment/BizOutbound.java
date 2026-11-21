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
 * 成品出库单 biz_outbound
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizOutbound extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long outboundId;

    @Excel(name = "出库单号")
    private String outboundNo;

    @Excel(name = "客户")
    private String customer;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "出库日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date outboundDate;

    @Excel(name = "总数量")
    private Integer totalQty;

    @Excel(name = "总金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal totalAmount;

    @Excel(name = "操作人")
    private String operator;

    private String status;

    private String cancelReason;

    private List<BizOutboundItem> items;
}
