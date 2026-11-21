package com.ruoyi.system.domain.garment;

import java.math.BigDecimal;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;

/**
 * 成品出库明细 biz_outbound_item
 */
@Data
public class BizOutboundItem
{
    private Long itemId;
    private Long outboundId;
    private Long skuId;

    @Excel(name = "款号")
    private String styleNo;

    @Excel(name = "颜色")
    private String color;

    @Excel(name = "尺码")
    private String size;

    @Excel(name = "数量")
    private Integer qty;

    @Excel(name = "单价", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal price;

    @Excel(name = "金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal amount;

    @Excel(name = "箱号")
    private String boxNo;

    /** 扫码/录入用 */
    private String barcode;
}
