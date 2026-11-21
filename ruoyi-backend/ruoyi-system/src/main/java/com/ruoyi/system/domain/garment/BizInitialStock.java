package com.ruoyi.system.domain.garment;

import java.math.BigDecimal;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;

/**
 * 初始库存导入行
 */
@Data
public class BizInitialStock
{
    @Excel(name = "商品条码")
    private String barcode;

    @Excel(name = "初始库存")
    private Integer qty;

    @Excel(name = "备注")
    private String remark;
}
