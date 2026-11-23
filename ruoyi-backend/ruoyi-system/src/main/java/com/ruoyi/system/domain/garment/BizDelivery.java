package com.ruoyi.system.domain.garment;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 完工交后道记录 biz_delivery
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizDelivery extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long deliveryId;
    private Long bundleId;

    @Excel(name = "扎号")
    private String bundleNo;

    private Long skuId;

    @Excel(name = "款号")
    private String styleNo;

    @Excel(name = "颜色")
    private String color;

    @Excel(name = "尺码")
    private String size;

    @Excel(name = "本次完工数")
    private Integer qty;

    @Excel(name = "次品数")
    private Integer defectQty;

    @Excel(name = "员工")
    private String employee;

    /** 交后道人用户ID */
    private Long userId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "交货时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date deliveryTime;

    private String remark;
}
