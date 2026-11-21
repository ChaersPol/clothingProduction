package com.ruoyi.system.mapper.garment;

import java.util.List;
import org.apache.ibatis.annotations.*;
import com.ruoyi.system.domain.garment.BizColor;

@Mapper
public interface BizColorMapper
{
    @Select("""
        <script>
        select * from biz_color
        <where>
        <if test="cnName != null and cnName != ''"> and (cn_name like concat('%',#{cnName},'%') or en_name like concat('%',#{cnName},'%'))</if>
        <if test="status != null and status != ''"> and status = #{status}</if>
        </where>
        order by sort, color_id
        </script>
        """)
    List<BizColor> selectColorList(BizColor color);

    @Select("select * from biz_color where status='0' order by sort, color_id")
    List<BizColor> selectColorAll();

    @Select("select * from biz_color where color_id=#{colorId}")
    BizColor selectColorById(Long colorId);

    @Insert("insert into biz_color(cn_name,en_name,color_code,sort,status,create_by,create_time,remark) values(#{cnName},#{enName},#{colorCode},#{sort},#{status},#{createBy},sysdate(),#{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "colorId")
    int insertColor(BizColor color);

    @Update("update biz_color set cn_name=#{cnName},en_name=#{enName},color_code=#{colorCode},sort=#{sort},status=#{status},update_by=#{updateBy},update_time=sysdate(),remark=#{remark} where color_id=#{colorId}")
    int updateColor(BizColor color);

    @Delete("delete from biz_color where color_id=#{colorId}")
    int deleteColorById(Long colorId);
}
