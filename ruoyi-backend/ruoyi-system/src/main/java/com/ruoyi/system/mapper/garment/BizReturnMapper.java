package com.ruoyi.system.mapper.garment;

import java.util.List;
import org.apache.ibatis.annotations.*;
import com.ruoyi.system.domain.garment.BizReturn;

@Mapper
public interface BizReturnMapper
{
    @Select("""
        <script>
        select * from biz_return
        <where>
        <if test="returnNo != null and returnNo != ''"> and return_no like concat('%',#{returnNo},'%')</if>
        <if test="customer != null and customer != ''"> and customer like concat('%',#{customer},'%')</if>
        <if test="styleNo != null and styleNo != ''"> and style_no like concat('%',#{styleNo},'%')</if>
        <if test="params.beginDate != null and params.beginDate != ''"> and return_date &gt;= #{params.beginDate}</if>
        <if test="params.endDate != null and params.endDate != ''"> and return_date &lt;= #{params.endDate}</if>
        </where>
        order by return_date desc, return_id desc
        </script>
        """)
    List<BizReturn> selectReturnList(BizReturn query);

    @Select("select * from biz_return where return_id=#{returnId}")
    BizReturn selectReturnById(Long returnId);

    @Insert("""
        insert into biz_return(return_no,customer,sku_id,style_no,color,size,qty,good_qty,bad_qty,reason,return_date,operator,create_time,remark)
        values(#{returnNo},#{customer},#{skuId},#{styleNo},#{color},#{size},#{qty},#{goodQty},#{badQty},#{reason},#{returnDate},#{operator},sysdate(),#{remark})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "returnId")
    int insertReturn(BizReturn ret);

    @Delete("delete from biz_return where return_id=#{returnId}")
    int deleteReturn(Long returnId);

    @Select("select count(1) from biz_return")
    int countAll();

    /** 取指定前缀下最大流水号，避免删除退货单后单号重复 */
    @Select("select coalesce(max(cast(right(return_no,3) as unsigned)),0) from biz_return where return_no like concat(#{prefix},'%')")
    int maxSeq(@Param("prefix") String prefix);
}
