package com.ruoyi.system.mapper.garment;

import java.util.List;
import org.apache.ibatis.annotations.*;
import com.ruoyi.system.domain.garment.BizSku;

@Mapper
public interface BizSkuMapper
{
    String JOIN_COLUMNS = """
        k.*,
        coalesce((select qty from biz_inventory i where i.sku_id=k.sku_id and i.stock_type='CUT'),0) as cut_qty,
        coalesce((select qty from biz_inventory i where i.sku_id=k.sku_id and i.stock_type='LINE'),0) as line_qty,
        coalesce((select qty from biz_inventory i where i.sku_id=k.sku_id and i.stock_type='FINISH'),0) as finish_qty,
        coalesce((select qty from biz_inventory i where i.sku_id=k.sku_id and i.stock_type='CUT'),0)
        + coalesce((select qty from biz_inventory i where i.sku_id=k.sku_id and i.stock_type='LINE'),0)
        + coalesce((select qty from biz_inventory i where i.sku_id=k.sku_id and i.stock_type='FINISH'),0) as total_qty
        """;

    @Select("""
        <script>
        select
        """ + JOIN_COLUMNS + """
        from biz_sku k
        where 1=1
        <if test="styleNo != null and styleNo != ''"> and k.style_no like concat('%',#{styleNo},'%')</if>
        <if test="cnName != null and cnName != ''"> and k.cn_name like concat('%',#{cnName},'%')</if>
        <if test="color != null and color != ''"> and k.color = #{color}</if>
        <if test="size != null and size != ''"> and k.size = #{size}</if>
        <if test="barcode != null and barcode != ''"> and k.barcode like concat('%',#{barcode},'%')</if>
        <if test="status != null and status != ''"> and k.status = #{status}</if>
        <if test="minStock != null and minStock == 1"> and k.min_stock > 0 and coalesce((select qty from biz_inventory i where i.sku_id=k.sku_id and i.stock_type='FINISH'),0) &lt; k.min_stock</if>
        order by k.style_no, k.color, k.size
        </script>
        """)
    List<BizSku> selectSkuList(BizSku sku);

    @Select("select * from biz_sku where sku_id=#{skuId}")
    BizSku selectSkuById(Long skuId);

    @Select("select * from biz_sku where barcode=#{barcode} limit 1")
    BizSku selectSkuByBarcode(String barcode);

    @Select("select * from biz_sku where style_no=#{styleNo} order by color,size")
    List<BizSku> selectSkuByStyleNo(String styleNo);

    @Select("select * from biz_sku where style_no=#{styleNo} and color=#{color} and size=#{size} and status='0' limit 1")
    BizSku selectSkuByStyleColorSize(@Param("styleNo") String styleNo, @Param("color") String color, @Param("size") String size);

    @Select("select count(1) from biz_sku where style_no=#{styleNo}")
    int countByStyleNo(String styleNo);

    @Select("select count(1) from biz_sku where size=#{size}")
    int countBySize(String size);

    @Select("select count(1) from biz_sku where color=#{color}")
    int countByColor(String color);

    @Insert("""
        insert into biz_sku(style_id,style_no,parent_style_no,cn_name,color,size,barcode,cost_price,sale_price,min_stock,status,create_by,create_time,remark)
        values(#{styleId},#{styleNo},#{parentStyleNo},#{cnName},#{color},#{size},#{barcode},#{costPrice},#{salePrice},#{minStock},#{status},#{createBy},sysdate(),#{remark})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "skuId")
    int insertSku(BizSku sku);

    @Update("""
        update biz_sku set style_id=#{styleId},style_no=#{styleNo},parent_style_no=#{parentStyleNo},cn_name=#{cnName},
          color=#{color},size=#{size},barcode=#{barcode},cost_price=#{costPrice},sale_price=#{salePrice},min_stock=#{minStock},
          status=#{status},update_by=#{updateBy},update_time=sysdate(),remark=#{remark}
        where sku_id=#{skuId}
        """)
    int updateSku(BizSku sku);

    @Update("update biz_sku set cost_price=#{costPrice}, sale_price=#{salePrice}, update_time=sysdate() where style_no=#{styleNo}")
    int updatePriceByStyleNo(@Param("styleNo") String styleNo, @Param("costPrice") java.math.BigDecimal costPrice, @Param("salePrice") java.math.BigDecimal salePrice);

    @Update("update biz_sku set min_stock=#{minStock}, update_time=sysdate() where sku_id=#{skuId}")
    int updateMinStock(@Param("skuId") Long skuId, @Param("minStock") Integer minStock);

    @Delete("delete from biz_sku where sku_id=#{skuId}")
    int deleteSkuById(Long skuId);
}
