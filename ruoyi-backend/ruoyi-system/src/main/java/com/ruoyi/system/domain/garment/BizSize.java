package com.ruoyi.system.domain.garment;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 尺码对象 biz_size
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizSize extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long sizeId;

    @Excel(name = "尺码")
    private String sizeName;

    @Excel(name = "排序")
    private Integer sort;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;
}
