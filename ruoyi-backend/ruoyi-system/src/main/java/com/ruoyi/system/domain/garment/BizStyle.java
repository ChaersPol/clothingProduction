package com.ruoyi.system.domain.garment;

import java.math.BigDecimal;
import java.util.List;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 款号档案对象 biz_style
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BizStyle extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long styleId;

    /** 款号 */
    @Excel(name = "款号")
    private String styleNo;

    /** 父体款号 */
    private String parentStyleNo;

    /** 中文品名 */
    @Excel(name = "品名")
    private String cnName;

    /** 名称 */
    private String styleName;

    /** 品类 */
    @Excel(name = "品类")
    private String category;

    /** 季节 */
    @Excel(name = "季节")
    private String season;

    private String image;

    /** 设计人 */
    @Excel(name = "设计人")
    private String designer;

    /** 尺码表(逗号分隔) */
    @Excel(name = "尺码表", width = 20)
    private String sizeTable;

    private String refImage;

    @Excel(name = "预计卖价", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal expectPrice;

    private String patternFile;

    private String sampleImage;

    private String designRemark;

    /** 已生成SKU数 */
    @Excel(name = "SKU数", cellType = Excel.ColumnType.NUMERIC, width = 8)
    private Integer skuCount;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    private String delFlag;
}
