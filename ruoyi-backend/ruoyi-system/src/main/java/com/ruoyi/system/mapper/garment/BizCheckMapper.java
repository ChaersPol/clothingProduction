package com.ruoyi.system.mapper.garment;

import java.util.List;
import org.apache.ibatis.annotations.*;
import com.ruoyi.system.domain.garment.BizCheck;
import com.ruoyi.system.domain.garment.BizCheckItem;

@Mapper
public interface BizCheckMapper
{
    @Select("""
        <script>
        select * from biz_check
        <where>
        <if test="checkNo != null and checkNo != ''"> and check_no like concat('%',#{checkNo},'%')</if>
        <if test="status != null and status != ''"> and status = #{status}</if>
        </where>
        order by check_id desc
        </script>
        """)
    List<BizCheck> selectCheckList(BizCheck check);

    @Select("select * from biz_check where check_id=#{checkId}")
    BizCheck selectCheckById(Long checkId);

    @Select("""
        select i.*, k.style_no, k.cn_name, k.color, k.size from biz_check_item i
        join biz_sku k on k.sku_id = i.sku_id where i.check_id=#{checkId} order by i.item_id
        """)
    List<BizCheckItem> selectItems(Long checkId);

    @Insert("insert into biz_check(check_no,check_date,checker,status,create_by,create_time,remark) values(#{checkNo},#{checkDate},#{checker},#{status},#{createBy},sysdate(),#{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "checkId")
    int insertCheck(BizCheck check);

    @Insert("insert into biz_check_item(check_id,sku_id,stock_type,book_qty,actual_qty,diff_qty) values(#{checkId},#{skuId},#{stockType},#{bookQty},#{actualQty},#{diffQty})")
    int insertItem(BizCheckItem item);

    @Update("update biz_check_item set actual_qty=#{actualQty}, diff_qty=#{diffQty} where item_id=#{itemId}")
    int updateItem(BizCheckItem item);

    @Update("update biz_check set status=#{status}, update_time=sysdate() where check_id=#{checkId}")
    int updateStatus(@Param("checkId") Long checkId, @Param("status") String status);

    @Delete("delete from biz_check_item where check_id=#{checkId}")
    int deleteItems(Long checkId);

    @Delete("delete from biz_check where check_id=#{checkId}")
    int deleteCheck(Long checkId);

    @Select("select count(1) from biz_check")
    int countAll();
}
