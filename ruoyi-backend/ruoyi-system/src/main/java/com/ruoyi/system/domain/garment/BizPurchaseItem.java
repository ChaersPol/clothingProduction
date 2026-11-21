package com.ruoyi.system.domain.garment;

import java.math.BigDecimal;
import lombok.Data;

/**
 * 采购明细对象 biz_purchase_item
 */
@Data
public class BizPurchaseItem
{
    private Long itemId;
    private Long purchaseId;
    private String materialType;
    private String materialName;
    private String spec;
    private String unit;
    private BigDecimal price;
    private BigDecimal qty;
    private BigDecimal receivedQty;
    private BigDecimal amount;
    private String remark;
}
