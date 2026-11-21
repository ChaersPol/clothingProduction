package com.ruoyi.system.domain.garment;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 裁货单对象 biz_cut_order
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizCutOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long cutId;

    @Excel(name = "裁货单号")
    private String cutNo;

    private Long styleId;

    @Excel(name = "货号")
    private String styleNo;

    @Excel(name = "品名")
    private String cnName;

    @Excel(name = "客户")
    private String customer;

    @Excel(name = "面料")
    private String fabric;

    @Excel(name = "里布")
    private String lining;

    @Excel(name = "总件数")
    private Integer totalQty;

    @Excel(name = "层数")
    private Integer layerCount;

    @Excel(name = "裁工")
    private String cutter;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "裁货日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date cutDate;

    /** 0待分扎 1已入库 2已完成 */
    private String status;

    private Integer bundleCount;

    private List<BizCutOrderItem> items;
}
