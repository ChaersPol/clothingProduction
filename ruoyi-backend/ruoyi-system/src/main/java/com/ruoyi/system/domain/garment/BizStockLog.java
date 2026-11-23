package com.ruoyi.system.domain.garment;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 库存流水对象 biz_stock_log
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizStockLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long logId;
    private Long skuId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "操作时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date operateTime;

    @Excel(name = "款号")
    private String styleNo;

    private String cnName;

    private String color;

    private String size;

    @Excel(name = "颜色/尺码")
    private String colorSize;

    /** CUT/LINE/FINISH */
    @Excel(name = "库存类型", readConverterExp = "CUT=裁片,LINE=产线,FINISH=成品")
    private String stockType;

    private String stockTypeLabel;

    @Excel(name = "变动")
    private Integer changeQty;

    @Excel(name = "变动前")
    private Integer beforeQty;

    @Excel(name = "变动后")
    private Integer afterQty;

    @Excel(name = "业务类型")
    private String bizType;

    @Excel(name = "业务单号")
    private String bizNo;

    @Excel(name = "扎号")
    private String bundleNo;

    @Excel(name = "操作人")
    private String operator;

    @Excel(name = "备注")
    private String remark;
}
