package com.ruoyi.system.mapper.garment;

import java.util.List;
import org.apache.ibatis.annotations.*;
import com.ruoyi.system.domain.garment.BizDefect;

@Mapper
public interface BizDefectMapper
{
    @Select("""
        <script>
        select d.*, b.style_no, b.color, b.size
        from biz_defect d left join biz_bundle b on b.bundle_id = d.bundle_id
        <where>
        <if test="styleNo != null and styleNo != ''"> and b.style_no like concat('%',#{styleNo},'%')</if>
        <if test="bundleNo != null and bundleNo != ''"> and d.bundle_no like concat('%',#{bundleNo},'%')</if>
        <if test="defectType != null and defectType != ''"> and d.defect_type = #{defectType}</if>
        </where>
        order by d.defect_id desc
        </script>
        """)
    List<BizDefect> selectDefectList(BizDefect defect);

    @Insert("insert into biz_defect(bundle_id,bundle_no,sku_id,qty,defect_type,handler,handle_time,remark) values(#{bundleId},#{bundleNo},#{skuId},#{qty},#{defectType},#{handler},sysdate(),#{remark})")
    int insertDefect(BizDefect defect);
}
