package com.ruoyi.system.mapper.garment;

import java.util.List;
import org.apache.ibatis.annotations.*;
import com.ruoyi.system.domain.garment.BizQc;

@Mapper
public interface BizQcMapper
{
    @Select("""
        <script>
        select q.*, b.style_no, b.color, b.size
        from biz_qc q left join biz_bundle b on b.bundle_id = q.bundle_id
        <where>
        <if test="styleNo != null and styleNo != ''"> and b.style_no like concat('%',#{styleNo},'%')</if>
        <if test="bundleNo != null and bundleNo != ''"> and q.bundle_no like concat('%',#{bundleNo},'%')</if>
        <if test="inspector != null and inspector != ''"> and q.inspector = #{inspector}</if>
        <if test="params.beginDate != null and params.beginDate != ''"> and q.check_time &gt;= concat(#{params.beginDate},' 00:00:00')</if>
        <if test="params.endDate != null and params.endDate != ''"> and q.check_time &lt;= concat(#{params.endDate},' 23:59:59')</if>
        </where>
        order by q.qc_id desc
        </script>
        """)
    List<BizQc> selectQcList(BizQc qc);

    @Insert("insert into biz_qc(bundle_id,bundle_no,sku_id,check_qty,qualified_qty,defect_qty,defect_reason,inspector,check_time,remark) values(#{bundleId},#{bundleNo},#{skuId},#{checkQty},#{qualifiedQty},#{defectQty},#{defectReason},#{inspector},sysdate(),#{remark})")
    int insertQc(BizQc qc);
}
