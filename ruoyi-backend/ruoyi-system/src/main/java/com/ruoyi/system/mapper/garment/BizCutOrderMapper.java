package com.ruoyi.system.mapper.garment;

import java.util.List;
import org.apache.ibatis.annotations.*;
import com.ruoyi.system.domain.garment.BizCutOrder;
import com.ruoyi.system.domain.garment.BizCutOrderItem;

@Mapper
public interface BizCutOrderMapper
{
    @Select("""
        <script>
        select c.*, (select count(1) from biz_bundle b where b.cut_id=c.cut_id) as bundle_count
        from biz_cut_order c
        <where>
        <if test="cutNo != null and cutNo != ''"> and c.cut_no like concat('%',#{cutNo},'%')</if>
        <if test="styleNo != null and styleNo != ''"> and c.style_no like concat('%',#{styleNo},'%')</if>
        <if test="cutter != null and cutter != ''"> and c.cutter = #{cutter}</if>
        <if test="status != null and status != ''"> and c.status = #{status}</if>
        <if test="params.beginDate != null and params.beginDate != ''"> and c.cut_date &gt;= #{params.beginDate}</if>
        <if test="params.endDate != null and params.endDate != ''"> and c.cut_date &lt;= #{params.endDate}</if>
        </where>
        order by c.cut_date desc, c.cut_id desc
        </script>
        """)
    List<BizCutOrder> selectCutOrderList(BizCutOrder order);

    @Select("select * from biz_cut_order where cut_id=#{cutId}")
    BizCutOrder selectCutOrderById(Long cutId);

    @Select("select * from biz_cut_order_item where cut_id=#{cutId} order by layer_no, color, size")
    List<BizCutOrderItem> selectItems(Long cutId);

    @Insert("""
        insert into biz_cut_order(cut_no,style_id,style_no,cn_name,customer,fabric,lining,total_qty,layer_count,cutter,cutter_id,cut_date,status,create_by,create_time,remark)
        values(#{cutNo},#{styleId},#{styleNo},#{cnName},#{customer},#{fabric},#{lining},#{totalQty},#{layerCount},#{cutter},#{cutterId},#{cutDate},#{status},#{createBy},sysdate(),#{remark})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "cutId")
    int insertCutOrder(BizCutOrder order);

    @Insert("insert into biz_cut_order_item(cut_id,layer_no,color,size,qty) values(#{cutId},#{layerNo},#{color},#{size},#{qty})")
    int insertItem(BizCutOrderItem item);

    @Delete("delete from biz_cut_order_item where cut_id=#{cutId}")
    int deleteItems(Long cutId);

    @Update("""
        update biz_cut_order set cut_no=#{cutNo},style_id=#{styleId},style_no=#{styleNo},cn_name=#{cnName},customer=#{customer},
          fabric=#{fabric},lining=#{lining},total_qty=#{totalQty},layer_count=#{layerCount},cutter=#{cutter},cut_date=#{cutDate},
          status=#{status},cutter_id=#{cutterId},update_by=#{updateBy},update_time=sysdate(),remark=#{remark}
        where cut_id=#{cutId}
        """)
    int updateCutOrder(BizCutOrder order);

    @Update("update biz_cut_order set status=#{status}, update_time=sysdate() where cut_id=#{cutId}")
    int updateStatus(@Param("cutId") Long cutId, @Param("status") String status);

    @Delete("delete from biz_cut_order where cut_id=#{cutId}")
    int deleteCutOrder(Long cutId);

    @Select("select count(1) from biz_cut_order where date_format(cut_date,'%Y%m%d')=date_format(sysdate(),'%Y%m%d')")
    int countToday();

    /** 取指定前缀下最大流水号，避免删除单据后单号重复 */
    @Select("select coalesce(max(cast(right(cut_no,3) as unsigned)),0) from biz_cut_order where cut_no like concat(#{prefix},'%')")
    int maxSeq(@Param("prefix") String prefix);

    @Select("select count(1) from biz_cut_order where status=#{status}")
    int countByStatus(@Param("status") String status);
}
