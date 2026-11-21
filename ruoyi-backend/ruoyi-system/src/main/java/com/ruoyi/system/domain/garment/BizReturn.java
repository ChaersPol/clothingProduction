package com.ruoyi.system.domain.garment;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 退货入库 biz_return
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizReturn extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long returnId;

    @Excel(name = "退货单号")
    private String returnNo;

    @Excel(name = "客户")
    private String customer;

    private Long skuId;

    @Excel(name = "款号")
    private String styleNo;

    @Excel(name = "颜色")
    private String color;

    @Excel(name = "尺码")
    private String size;

    @Excel(name = "退货数量")
    private Integer qty;

    @Excel(name = "好货数量")
    private Integer goodQty;

    @Excel(name = "次品数量")
    private Integer badQty;

    @Excel(name = "退货原因")
    private String reason;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "退货日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date returnDate;

    @Excel(name = "操作人")
    private String operator;

    /** 扫码录入用 */
    private String barcode;
}
