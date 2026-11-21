package com.ruoyi.system.domain.garment;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 员工档案 biz_employee
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizEmployee extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long employeeId;

    @Excel(name = "姓名")
    private String name;

    @Excel(name = "岗位/工种")
    private String position;

    @Excel(name = "排序")
    private Integer sort;

    /** 0在职 1离职 */
    private String status;
}
