package com.ruoyi.system.domain.garment;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 包装登记 biz_pack
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizPack extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long packId;
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

    @Excel(name = "包装数量")
    private Integer qty;

    @Excel(name = "包装方式")
    private String packMethod;

    @Excel(name = "每袋件数")
    private Integer perBag;

    @Excel(name = "包装人")
    private String packer;

    /** 包装人用户ID，包装统计按人聚合 */
    private Long userId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "包装时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date packTime;

    private String remark;
}
