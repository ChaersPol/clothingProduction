package com.ruoyi.system.domain.garment;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 盘点单 biz_check
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizCheck extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long checkId;

    @Excel(name = "盘点单号")
    private String checkNo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "盘点日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date checkDate;

    @Excel(name = "盘点人")
    private String checker;

    /** 0进行中 1已完成 */
    private String status;

    private List<BizCheckItem> items;
}
