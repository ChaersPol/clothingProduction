package com.ruoyi.system.mapper.garment;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.*;
import com.ruoyi.system.domain.garment.BizInventory;

@Mapper
public interface BizInventoryMapper
{
    @Select("""
        <script>
        select i.*, k.style_no, k.parent_style_no, k.cn_name, k.color, k.size, k.barcode, k.cost_price, k.sale_price, k.min_stock,
               (i.qty * k.cost_price) as stock_value, datediff(sysdate(), date(i.update_time)) as age_days
        from biz_inventory i join biz_sku k on k.sku_id = i.sku_id
        <where>
        i.qty &gt; 0
        <if test="stockType != null and stockType != ''"> and i.stock_type = #{stockType}</if>
        <if test="styleNo != null and styleNo != ''"> and k.style_no like concat('%',#{styleNo},'%')</if>
        <if test="cnName != null and cnName != ''"> and k.cn_name like concat('%',#{cnName},'%')</if>
        <if test="color != null and color != ''"> and k.color = #{color}</if>
        <if test="size != null and size != ''"> and k.size = #{size}</if>
        <if test="barcode != null and barcode != ''"> and k.barcode like concat('%',#{barcode},'%')</if>
        </where>
        order by k.style_no, k.color, k.size, i.stock_type
        </script>
        """)
    List<BizInventory> selectInventoryList(BizInventory query);

    @Select("select * from biz_inventory where sku_id=#{skuId} and stock_type=#{stockType} for update")
    BizInventory lockRow(@Param("skuId") Long skuId, @Param("stockType") String stockType);

    @Select("select * from biz_inventory where sku_id=#{skuId} and stock_type=#{stockType}")
    BizInventory selectRow(@Param("skuId") Long skuId, @Param("stockType") String stockType);

    @Insert("insert into biz_inventory(sku_id,stock_type,qty,update_time) values(#{skuId},#{stockType},#{qty},sysdate())")
    int insertRow(@Param("skuId") Long skuId, @Param("stockType") String stockType, @Param("qty") Integer qty);

    @Update("update biz_inventory set qty=#{qty}, update_time=sysdate() where sku_id=#{skuId} and stock_type=#{stockType}")
    int updateQty(@Param("skuId") Long skuId, @Param("stockType") String stockType, @Param("qty") Integer qty);

    @Select("""
        <script>
        select k.sku_id, k.style_no, k.cn_name, k.color, k.size, k.barcode, k.min_stock,
               coalesce((select qty from biz_inventory i where i.sku_id=k.sku_id and i.stock_type='FINISH'),0) as finish_qty,
               coalesce((select qty from biz_inventory i where i.sku_id=k.sku_id and i.stock_type='LINE'),0) as line_qty,
               coalesce((select qty from biz_inventory i where i.sku_id=k.sku_id and i.stock_type='CUT'),0) as cut_qty
        from biz_sku k
        where k.min_stock &gt; 0
          and coalesce((select qty from biz_inventory i where i.sku_id=k.sku_id and i.stock_type='FINISH'),0) &lt; k.min_stock
        <if test="styleNo != null and styleNo != ''"> and k.style_no like concat('%',#{styleNo},'%')</if>
        order by (k.min_stock - coalesce((select qty from biz_inventory i where i.sku_id=k.sku_id and i.stock_type='FINISH'),0)) desc
        </script>
        """)
    List<Map<String, Object>> selectWarnings(@Param("styleNo") String styleNo);

    @Select("select stock_type as stockType, coalesce(sum(qty),0) as qty from biz_inventory group by stock_type")
    List<Map<String, Object>> sumByType();
}
