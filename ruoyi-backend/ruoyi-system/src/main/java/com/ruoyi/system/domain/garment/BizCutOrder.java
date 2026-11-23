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

    @Excel(name = "款号")
    private String styleNo;

    @Excel(name = "品名")
    private String cnName;

    @Excel(name = "客户")
    private String customer;

    @Excel(name = "面料")
    private String fabric;

    private String lining;

    @Excel(name = "总件数")
    private Integer totalQty;

    @Excel(name = "层数")
    private Integer layerCount;

    @Excel(name = "裁工")
    private String cutter;

    /** 裁工用户ID，裁货统计按人聚合 */
    private Long cutterId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "裁货日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date cutDate;

    @Excel(name = "扎数")
    private Integer bundleCount;

    /** 已分扎合计数量，用于判断裁货单是否已分完 */
    private Integer bundleQty;

    /** 0待分扎 1已入库 2已完成 9已作废 */
    @Excel(name = "状态", readConverterExp = "0=待分扎,1=已入库,2=已完成,9=已作废")
    private String status;

    /** 作废原因 */
    private String cancelReason;

    /** 是否允许超库存领料（需同时填写超领原因） */
    private Boolean allowOverdraft;

    /** 超领原因 */
    private String overdraftReason;

    private List<BizCutOrderItem> items;
}
