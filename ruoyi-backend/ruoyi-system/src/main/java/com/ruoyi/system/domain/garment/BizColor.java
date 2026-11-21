package com.ruoyi.system.domain.garment;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 颜色对象 biz_color
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizColor extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long colorId;

    @Excel(name = "中文名")
    private String cnName;

    @Excel(name = "英文名")
    private String enName;

    /** 色卡/色值 */
    @Excel(name = "色卡")
    private String colorCode;

    @Excel(name = "排序")
    private Integer sort;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;
}
