package com.ruoyi.system.mapper.garment;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.*;
import com.ruoyi.system.domain.garment.BizPack;

@Mapper
public interface BizPackMapper
{
    @Select("""
        <script>
        select p.*, b.style_no, b.color, b.size
        from biz_pack p left join biz_bundle b on b.bundle_id = p.bundle_id
        <where>
        <if test="styleNo != null and styleNo != ''"> and b.style_no like concat('%',#{styleNo},'%')</if>
        <if test="bundleNo != null and bundleNo != ''"> and p.bundle_no like concat('%',#{bundleNo},'%')</if>
        <if test="packer != null and packer != ''"> and p.packer = #{packer}</if>
        <if test="params.beginDate != null and params.beginDate != ''"> and p.pack_time &gt;= concat(#{params.beginDate},' 00:00:00')</if>
        <if test="params.endDate != null and params.endDate != ''"> and p.pack_time &lt;= concat(#{params.endDate},' 23:59:59')</if>
        </where>
        order by p.pack_id desc
        </script>
        """)
    List<BizPack> selectPackList(BizPack pack);

    @Insert("insert into biz_pack(bundle_id,bundle_no,sku_id,qty,pack_method,per_bag,packer,pack_time,remark) values(#{bundleId},#{bundleNo},#{skuId},#{qty},#{packMethod},#{perBag},#{packer},sysdate(),#{remark})")
    int insertPack(BizPack pack);

    @Select("select coalesce(sum(qty),0) from biz_pack where bundle_id=#{bundleId}")
    int sumPackQtyByBundle(Long bundleId);

    @Select("""
        select packer, sum(qty) as qty, count(1) as times from biz_pack
        where date_format(pack_time,'%Y-%m-%d') = #{date} group by packer order by qty desc
        """)
    List<Map<String, Object>> statByPacker(@Param("date") String date);
}
