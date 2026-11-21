package com.ruoyi.system.mapper.garment;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.*;
import com.ruoyi.system.domain.garment.BizShipment;

@Mapper
public interface BizShipmentMapper
{
    @Select("""
        <script>
        select s.*, o.outbound_no from biz_shipment s left join biz_outbound o on o.outbound_id = s.outbound_id
        <where>
        <if test="styleNo != null and styleNo != ''"> and s.style_no like concat('%',#{styleNo},'%')</if>
        <if test="customer != null and customer != ''"> and s.customer like concat('%',#{customer},'%')</if>
        <if test="color != null and color != ''"> and s.color = #{color}</if>
        <if test="size != null and size != ''"> and s.size = #{size}</if>
        <if test="params.beginDate != null and params.beginDate != ''"> and s.ship_date &gt;= #{params.beginDate}</if>
        <if test="params.endDate != null and params.endDate != ''"> and s.ship_date &lt;= #{params.endDate}</if>
        </where>
        order by s.ship_date desc, s.ship_id desc
        </script>
        """)
    List<BizShipment> selectShipmentList(BizShipment shipment);

    @Select("""
        <script>
        select ship_date as shipDate, sum(qty) as qty, sum(amount) as amount, sum(sea_freight) as seaFreight
        from biz_shipment
        <where>
        <if test="beginDate != null and beginDate != ''"> and ship_date &gt;= #{beginDate}</if>
        <if test="endDate != null and endDate != ''"> and ship_date &lt;= #{endDate}</if>
        </where>
        group by ship_date order by ship_date desc
        </script>
        """)
    List<Map<String, Object>> sumByDay(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("select * from biz_shipment where ship_id=#{shipId}")
    BizShipment selectShipmentById(Long shipId);

    @Insert("""
        insert into biz_shipment(ship_date,customer,style_no,color,size,qty,price,amount,sea_freight,outbound_id,create_by,create_time,remark)
        values(#{shipDate},#{customer},#{styleNo},#{color},#{size},#{qty},#{price},#{amount},#{seaFreight},#{outboundId},#{createBy},sysdate(),#{remark})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "shipId")
    int insertShipment(BizShipment shipment);

    @Update("""
        update biz_shipment set ship_date=#{shipDate},customer=#{customer},style_no=#{styleNo},color=#{color},size=#{size},
          qty=#{qty},price=#{price},amount=#{amount},sea_freight=#{seaFreight},outbound_id=#{outboundId},
          update_by=#{updateBy},update_time=sysdate(),remark=#{remark}
        where ship_id=#{shipId}
        """)
    int updateShipment(BizShipment shipment);

    @Update("update biz_shipment set outbound_id=#{outboundId} where ship_id=#{shipId}")
    int updateOutboundId(@Param("shipId") Long shipId, @Param("outboundId") Long outboundId);

    @Delete("delete from biz_shipment where ship_id=#{shipId}")
    int deleteShipment(Long shipId);
}
