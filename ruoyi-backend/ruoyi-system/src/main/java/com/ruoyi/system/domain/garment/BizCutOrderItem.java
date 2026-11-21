package com.ruoyi.system.domain.garment;

import lombok.Data;

/**
 * 裁货单层次明细对象 biz_cut_order_item
 */
@Data
public class BizCutOrderItem
{
    private Long itemId;
    private Long cutId;
    private Integer layerNo;
    private String color;
    private String size;
    private Integer qty;
}
