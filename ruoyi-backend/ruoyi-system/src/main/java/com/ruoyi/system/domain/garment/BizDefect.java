package com.ruoyi.system.domain.garment;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 次品处理 biz_defect（补片/报废/返修）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizDefect extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long defectId;
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

    @Excel(name = "处理方式")
    private String defectType;

    @Excel(name = "处理人")
    private String handler;

    /** 处理人用户ID */
    private Long userId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "处理时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date handleTime;

    private String remark;
}
