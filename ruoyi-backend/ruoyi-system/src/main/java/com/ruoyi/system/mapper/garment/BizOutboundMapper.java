package com.ruoyi.system.mapper.garment;

import java.util.List;
import org.apache.ibatis.annotations.*;
import com.ruoyi.system.domain.garment.BizOutbound;
import com.ruoyi.system.domain.garment.BizOutboundItem;

@Mapper
public interface BizOutboundMapper
{
    @Select("""
        <script>
        select * from biz_outbound
        <where>
        <if test="outboundNo != null and outboundNo != ''"> and outbound_no like concat('%',#{outboundNo},'%')</if>
        <if test="customer != null and customer != ''"> and customer like concat('%',#{customer},'%')</if>
        <if test="status != null and status != ''"> and status = #{status}</if>
        <if test="params.beginDate != null and params.beginDate != ''"> and outbound_date &gt;= #{params.beginDate}</if>
        <if test="params.endDate != null and params.endDate != ''"> and outbound_date &lt;= #{params.endDate}</if>
        </where>
        order by outbound_date desc, outbound_id desc
        </script>
        """)
    List<BizOutbound> selectOutboundList(BizOutbound outbound);

    @Select("select * from biz_outbound where outbound_id=#{outboundId}")
    BizOutbound selectOutboundById(Long outboundId);

    @Select("select * from biz_outbound_item where outbound_id=#{outboundId} order by item_id")
    List<BizOutboundItem> selectItems(Long outboundId);

    @Insert("""
        insert into biz_outbound(outbound_no,customer,outbound_date,total_qty,total_amount,operator,create_by,create_time,remark)
        values(#{outboundNo},#{customer},#{outboundDate},#{totalQty},#{totalAmount},#{operator},#{createBy},sysdate(),#{remark})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "outboundId")
    int insertOutbound(BizOutbound outbound);

    @Insert("""
        insert into biz_outbound_item(outbound_id,sku_id,style_no,color,size,qty,price,amount,box_no)
        values(#{outboundId},#{skuId},#{styleNo},#{color},#{size},#{qty},#{price},#{amount},#{boxNo})
        """)
    int insertItem(BizOutboundItem item);

    @Delete("delete from biz_outbound_item where outbound_id=#{outboundId}")
    int deleteItems(Long outboundId);

    @Delete("delete from biz_outbound where outbound_id=#{outboundId}")
    int deleteOutbound(Long outboundId);

    @Select("select count(1) from biz_outbound")
    int countAll();

    @Update("update biz_outbound set status='1', cancel_reason=#{reason}, update_time=sysdate() where outbound_id=#{outboundId}")
    int cancel(@Param("outboundId") Long outboundId, @Param("reason") String reason);
}
