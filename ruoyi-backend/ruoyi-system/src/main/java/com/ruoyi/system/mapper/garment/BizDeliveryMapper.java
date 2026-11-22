package com.ruoyi.system.mapper.garment;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.*;
import com.ruoyi.system.domain.garment.BizDelivery;

@Mapper
public interface BizDeliveryMapper
{
    @Select("""
        <script>
        select d.*, b.style_no, b.color, b.size
        from biz_delivery d left join biz_bundle b on b.bundle_id = d.bundle_id
        <where>
        <if test="styleNo != null and styleNo != ''"> and b.style_no like concat('%',#{styleNo},'%')</if>
        <if test="bundleNo != null and bundleNo != ''"> and d.bundle_no like concat('%',#{bundleNo},'%')</if>
        <if test="employee != null and employee != ''"> and d.employee = #{employee}</if>
        <if test="params.beginDate != null and params.beginDate != ''"> and d.delivery_time &gt;= concat(#{params.beginDate},' 00:00:00')</if>
        <if test="params.endDate != null and params.endDate != ''"> and d.delivery_time &lt;= concat(#{params.endDate},' 23:59:59')</if>
        </where>
        order by d.delivery_id desc
        </script>
        """)
    List<BizDelivery> selectDeliveryList(BizDelivery delivery);

    @Insert("insert into biz_delivery(bundle_id,bundle_no,sku_id,qty,defect_qty,employee,delivery_time,remark) values(#{bundleId},#{bundleNo},#{skuId},#{qty},#{defectQty},#{employee},sysdate(),#{remark})")
    int insertDelivery(BizDelivery delivery);

    @Select("""
        <script>
        select employee, sum(qty) as qty, sum(defect_qty) as defectQty, count(1) as times from biz_delivery
        where date_format(delivery_time,'%Y-%m-%d') = #{date}
        <if test="employee != null and employee != ''"> and employee = #{employee}</if>
        group by employee order by qty desc
        </script>
        """)
    List<Map<String, Object>> statByEmployee(@Param("date") String date, @Param("employee") String employee);
}
