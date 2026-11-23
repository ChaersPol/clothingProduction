package com.ruoyi.system.mapper.garment;

import java.util.List;
import org.apache.ibatis.annotations.*;
import com.ruoyi.system.domain.garment.BizStockLog;

@Mapper
public interface BizStockLogMapper
{
    @Select("""
        <script>
        select *, concat(ifnull(color,''),'/',ifnull(size,'')) as color_size from biz_stock_log
        <where>
        <if test="styleNo != null and styleNo != ''"> and style_no like concat('%',#{styleNo},'%')</if>
        <if test="color != null and color != ''"> and color = #{color}</if>
        <if test="size != null and size != ''"> and size = #{size}</if>
        <if test="stockType != null and stockType != ''"> and stock_type = #{stockType}</if>
        <if test="bizType != null and bizType != ''"> and biz_type = #{bizType}</if>
        <if test="bundleNo != null and bundleNo != ''"> and bundle_no like concat('%',#{bundleNo},'%')</if>
        <if test="operator != null and operator != ''"> and operator like concat('%',#{operator},'%')</if>
        <if test="params.beginDate != null and params.beginDate != ''"> and operate_time &gt;= concat(#{params.beginDate},' 00:00:00')</if>
        <if test="params.endDate != null and params.endDate != ''"> and operate_time &lt;= concat(#{params.endDate},' 23:59:59')</if>
        </where>
        order by log_id desc
        </script>
        """)
    List<BizStockLog> selectLogList(BizStockLog log);

    @Insert("""
        insert into biz_stock_log(sku_id,style_no,cn_name,color,size,stock_type,change_qty,before_qty,after_qty,biz_type,biz_no,bundle_no,operator,operate_time,remark)
        values(#{skuId},#{styleNo},#{cnName},#{color},#{size},#{stockType},#{changeQty},#{beforeQty},#{afterQty},#{bizType},#{bizNo},#{bundleNo},#{operator},sysdate(),#{remark})
        """)
    int insertLog(BizStockLog log);
}
