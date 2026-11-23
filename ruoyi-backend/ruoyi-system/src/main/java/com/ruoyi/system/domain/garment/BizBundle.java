package com.ruoyi.system.domain.garment;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 裁片扎包对象 biz_bundle
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizBundle extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long bundleId;

    @Excel(name = "扎号")
    private String bundleNo;

    /** 扎卡条码 */
    private String barcode;

    private Long cutId;

    @Excel(name = "裁货单号")
    private String cutNo;

    private Long skuId;

    @Excel(name = "款号")
    private String styleNo;

    @Excel(name = "中文品名")
    private String cnName;

    @Excel(name = "颜色")
    private String color;

    @Excel(name = "尺码")
    private String size;

    @Excel(name = "数量")
    private Integer qty;

    @Excel(name = "已交数")
    private Integer deliveredQty;

    @Excel(name = "已入库数")
    private Integer packedQty;

    @Excel(name = "次品数")
    private Integer defectQty;

    /** 0已开扎 1裁片在库 2产线中 3已完工 */
    private String status;

    private String pickupUser;

    /** 领片人用户ID，用于"只能交本人领的扎"判断 */
    private Long pickupUserId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date pickupTime;

    private String packCode;

    private String statusLabel;

    private String skuBarcode;

    /** 在产线停留天数 */
    private Integer onlineDays;
}
