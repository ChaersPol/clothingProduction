package com.ruoyi.system.domain.garment;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户档案对象 biz_customer
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizCustomer extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long customerId;

    @Excel(name = "客户名称")
    private String customerName;

    @Excel(name = "联系人")
    private String contact;

    @Excel(name = "电话")
    private String phone;

    @Excel(name = "地址")
    private String address;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;
}
