package com.ruoyi.system.mapper.garment;

import java.util.List;
import org.apache.ibatis.annotations.*;
import com.ruoyi.system.domain.garment.BizSize;

@Mapper
public interface BizSizeMapper
{
    @Select("""
        <script>
        select * from biz_size
        <where>
        <if test="sizeName != null and sizeName != ''"> and size_name like concat('%',#{sizeName},'%')</if>
        <if test="status != null and status != ''"> and status = #{status}</if>
        </where>
        order by sort, size_id
        </script>
        """)
    List<BizSize> selectSizeList(BizSize size);

    @Select("select * from biz_size where status='0' order by sort, size_id")
    List<BizSize> selectSizeAll();

    @Select("select * from biz_size where size_id=#{sizeId}")
    BizSize selectSizeById(Long sizeId);

    @Insert("insert into biz_size(size_name,sort,status,create_by,create_time,remark) values(#{sizeName},#{sort},#{status},#{createBy},sysdate(),#{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "sizeId")
    int insertSize(BizSize size);

    @Update("update biz_size set size_name=#{sizeName},sort=#{sort},status=#{status},update_by=#{updateBy},update_time=sysdate(),remark=#{remark} where size_id=#{sizeId}")
    int updateSize(BizSize size);

    @Delete("delete from biz_size where size_id=#{sizeId}")
    int deleteSizeById(Long sizeId);
}
