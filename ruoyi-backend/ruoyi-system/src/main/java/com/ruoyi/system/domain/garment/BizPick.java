package com.ruoyi.system.domain.garment;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 车间领片记录 biz_pick
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizPick extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long pickId;
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

    @Excel(name = "数量")
    private Integer qty;

    @Excel(name = "员工")
    private String employee;

    /** 领片人用户ID，统计按人聚合，避免改名/重名导致产量串人 */
    private Long userId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "领片时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date pickTime;

    private String remark;
}
