package com.ruoyi.system.mapper.garment;

import java.util.List;
import org.apache.ibatis.annotations.*;
import com.ruoyi.system.domain.garment.BizStyle;

@Mapper
public interface BizStyleMapper
{
    @Select("""
        <script>
        select s.*, (select count(1) from biz_sku k where k.style_no = s.style_no) as sku_count
        from biz_style s
        where s.del_flag = '0'
        <if test="styleNo != null and styleNo != ''"> and s.style_no like concat('%', #{styleNo}, '%')</if>
        <if test="cnName != null and cnName != ''"> and s.cn_name like concat('%', #{cnName}, '%')</if>
        <if test="category != null and category != ''"> and s.category = #{category}</if>
        <if test="designer != null and designer != ''"> and s.designer like concat('%', #{designer}, '%')</if>
        <if test="status != null and status != ''"> and s.status = #{status}</if>
        order by s.style_id desc
        </script>
        """)
    List<BizStyle> selectStyleList(BizStyle style);

    @Select("select * from biz_style where style_id = #{styleId} and del_flag = '0'")
    BizStyle selectStyleById(Long styleId);

    @Select("select * from biz_style where style_no = #{styleNo} and del_flag = '0' limit 1")
    BizStyle selectStyleByNo(String styleNo);

    @Select("select * from biz_style where del_flag = '0' and status = '0' order by style_no")
    List<BizStyle> selectStyleAll();

    @Insert("""
        insert into biz_style(style_no,parent_style_no,cn_name,style_name,category,season,image,designer,size_table,
          ref_image,expect_price,pattern_file,sample_image,design_remark,status,del_flag,create_by,create_time,remark)
        values(#{styleNo},#{parentStyleNo},#{cnName},#{styleName},#{category},#{season},#{image},#{designer},#{sizeTable},
          #{refImage},#{expectPrice},#{patternFile},#{sampleImage},#{designRemark},#{status},'0',#{createBy},sysdate(),#{remark})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "styleId")
    int insertStyle(BizStyle style);

    @Update("""
        update biz_style set style_no=#{styleNo}, parent_style_no=#{parentStyleNo}, cn_name=#{cnName}, style_name=#{styleName},
          category=#{category}, season=#{season}, image=#{image}, designer=#{designer}, size_table=#{sizeTable},
          ref_image=#{refImage}, expect_price=#{expectPrice}, pattern_file=#{patternFile}, sample_image=#{sampleImage},
          design_remark=#{designRemark}, status=#{status}, update_by=#{updateBy}, update_time=sysdate(), remark=#{remark}
        where style_id=#{styleId}
        """)
    int updateStyle(BizStyle style);

    @Update("update biz_style set del_flag='2', update_time=sysdate() where style_id=#{styleId}")
    int deleteStyleById(Long styleId);
}
