package com.ruoyi.system.domain.garment;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 费用登记 biz_expense
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizExpense extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long expenseId;

    @Excel(name = "费用类型")
    private String expenseType;

    @Excel(name = "款号")
    private String styleNo;

    @Excel(name = "金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal amount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "费用日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expenseDate;
}
