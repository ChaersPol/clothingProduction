package com.ruoyi.system.mapper.garment;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.*;
import com.ruoyi.system.domain.garment.BizPick;

@Mapper
public interface BizPickMapper
{
    @Select("""
        <script>
        select * from biz_pick
        <where>
        <if test="styleNo != null and styleNo != ''"> and style_no like concat('%',#{styleNo},'%')</if>
        <if test="bundleNo != null and bundleNo != ''"> and bundle_no like concat('%',#{bundleNo},'%')</if>
        <if test="employee != null and employee != ''"> and employee = #{employee}</if>
        <if test="params.beginDate != null and params.beginDate != ''"> and pick_time &gt;= concat(#{params.beginDate},' 00:00:00')</if>
        <if test="params.endDate != null and params.endDate != ''"> and pick_time &lt;= concat(#{params.endDate},' 23:59:59')</if>
        </where>
        order by pick_id desc
        </script>
        """)
    List<BizPick> selectPickList(BizPick pick);

    @Insert("insert into biz_pick(bundle_id,bundle_no,sku_id,style_no,color,size,qty,employee,user_id,pick_time,remark) values(#{bundleId},#{bundleNo},#{skuId},#{styleNo},#{color},#{size},#{qty},#{employee},#{userId},sysdate(),#{remark})")
    int insertPick(BizPick pick);

    @Select("""
        <script>
        select max(employee) as employee, sum(qty) as qty, count(1) as bundles from biz_pick
        where date_format(pick_time,'%Y-%m-%d') = #{date}
        <if test="employee != null and employee != ''"> and employee = #{employee}</if>
        group by ifnull(user_id, 0) order by qty desc
        </script>
        """)
    List<Map<String, Object>> statByEmployee(@Param("date") String date, @Param("employee") String employee);
}
