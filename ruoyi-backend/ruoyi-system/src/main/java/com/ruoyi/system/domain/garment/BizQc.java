package com.ruoyi.system.domain.garment;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 后道检验记录 biz_qc
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizQc extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long qcId;
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

    @Excel(name = "检验数")
    private Integer checkQty;

    @Excel(name = "合格数")
    private Integer qualifiedQty;

    @Excel(name = "次品数")
    private Integer defectQty;

    @Excel(name = "次品原因")
    private String defectReason;

    @Excel(name = "检验人")
    private String inspector;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "检验时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date checkTime;

    private String remark;
}
