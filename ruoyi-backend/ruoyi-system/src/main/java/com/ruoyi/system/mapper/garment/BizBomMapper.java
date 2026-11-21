package com.ruoyi.system.mapper.garment;

import java.util.List;
import org.apache.ibatis.annotations.*;
import com.ruoyi.system.domain.garment.BizBom;

@Mapper
public interface BizBomMapper
{
    @Select("""
        <script>
        select * from biz_bom
        <where>
        <if test="styleNo != null and styleNo != ''"> and style_no = #{styleNo}</if>
        <if test="materialType != null and materialType != ''"> and material_type = #{materialType}</if>
        <if test="materialName != null and materialName != ''"> and material_name like concat('%',#{materialName},'%')</if>
        </where>
        order by style_no, material_type, bom_id
        </script>
        """)
    List<BizBom> selectBomList(BizBom bom);

    @Select("select * from biz_bom where bom_id=#{bomId}")
    BizBom selectBomById(Long bomId);

    @Insert("""
        insert into biz_bom(style_no,material_type,material_name,spec,unit,qty_per_piece,loss_rate,unit_price,amount,remark,create_by,create_time)
        values(#{styleNo},#{materialType},#{materialName},#{spec},#{unit},#{qtyPerPiece},#{lossRate},#{unitPrice},#{amount},#{remark},#{createBy},sysdate())
        """)
    @Options(useGeneratedKeys = true, keyProperty = "bomId")
    int insertBom(BizBom bom);

    @Update("""
        update biz_bom set style_no=#{styleNo},material_type=#{materialType},material_name=#{materialName},spec=#{spec},unit=#{unit},
          qty_per_piece=#{qtyPerPiece},loss_rate=#{lossRate},unit_price=#{unitPrice},amount=#{amount},remark=#{remark},
          update_by=#{updateBy},update_time=sysdate()
        where bom_id=#{bomId}
        """)
    int updateBom(BizBom bom);

    @Delete("delete from biz_bom where bom_id=#{bomId}")
    int deleteBomById(Long bomId);
}
