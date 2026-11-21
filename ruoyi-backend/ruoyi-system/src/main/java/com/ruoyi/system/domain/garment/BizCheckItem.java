package com.ruoyi.system.domain.garment;

import lombok.Data;

/**
 * 盘点明细 biz_check_item
 */
@Data
public class BizCheckItem
{
    private Long itemId;
    private Long checkId;
    private Long skuId;
    private String stockType;
    private Integer bookQty;
    private Integer actualQty;
    private Integer diffQty;

    /** 展示字段 */
    private String styleNo;
    private String cnName;
    private String color;
    private String size;
}
