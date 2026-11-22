package com.ruoyi.system.mapper.garment;

import java.util.List;
import org.apache.ibatis.annotations.*;
import com.ruoyi.system.domain.garment.BizBundle;

@Mapper
public interface BizBundleMapper
{
    @Select("""
        <script>
        select b.*, k.barcode as sku_barcode, datediff(sysdate(), date(b.pickup_time)) as online_days
        from biz_bundle b left join biz_sku k on k.sku_id = b.sku_id
        <where>
        <if test="cutId != null"> and b.cut_id = #{cutId}</if>
        <if test="cutNo != null and cutNo != ''"> and b.cut_no like concat('%',#{cutNo},'%')</if>
        <if test="styleNo != null and styleNo != ''"> and b.style_no like concat('%',#{styleNo},'%')</if>
        <if test="color != null and color != ''"> and b.color = #{color}</if>
        <if test="size != null and size != ''"> and b.size = #{size}</if>
        <if test="bundleNo != null and bundleNo != ''"> and b.bundle_no like concat('%',#{bundleNo},'%')</if>
        <if test="pickupUser != null and pickupUser != ''"> and b.pickup_user = #{pickupUser}</if>
        <if test="status != null and status != ''"> and b.status = #{status}</if>
        <if test="params.online != null"> and b.status='2' and b.delivered_qty &lt; b.qty</if>
        <if test="params.overdueDays != null"> and b.status='2' and b.delivered_qty &lt; b.qty and datediff(sysdate(), date(b.pickup_time)) &gt; #{params.overdueDays}</if>
        <if test="params.pendingPack != null"> and b.status in ('2','3') and b.delivered_qty &gt;= b.qty and b.packed_qty &lt; b.qty</if>
        </where>
        order by b.bundle_id desc
        </script>
        """)
    List<BizBundle> selectBundleList(BizBundle bundle);

    @Select("select * from biz_bundle where bundle_id=#{bundleId}")
    BizBundle selectBundleById(Long bundleId);

    @Select("select * from biz_bundle where barcode=#{code} or bundle_no=#{code} limit 1")
    BizBundle selectByCode(String code);

    @Select("select * from biz_bundle where cut_id=#{cutId} order by bundle_id")
    List<BizBundle> selectByCutId(Long cutId);

    @Select("select * from biz_bundle where status='1' order by bundle_id")
    List<BizBundle> selectInStockBundles();

    @Insert("""
        insert into biz_bundle(bundle_no,barcode,cut_id,cut_no,sku_id,style_no,cn_name,color,size,qty,delivered_qty,defect_qty,status,create_by,create_time,remark)
        values(#{bundleNo},#{barcode},#{cutId},#{cutNo},#{skuId},#{styleNo},#{cnName},#{color},#{size},#{qty},0,0,'0',#{createBy},sysdate(),#{remark})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "bundleId")
    int insertBundle(BizBundle bundle);

    @Update("""
        update biz_bundle set bundle_no=#{bundleNo},barcode=#{barcode},cut_id=#{cutId},cut_no=#{cutNo},sku_id=#{skuId},
          style_no=#{styleNo},cn_name=#{cnName},color=#{color},size=#{size},qty=#{qty},delivered_qty=#{deliveredQty},
          packed_qty=#{packedQty},defect_qty=#{defectQty},status=#{status},pack_code=#{packCode},update_by=#{updateBy},update_time=sysdate(),remark=#{remark}
        where bundle_id=#{bundleId}
        """)
    int updateBundle(BizBundle bundle);

    @Update("update biz_bundle set status=#{status}, pickup_user=#{user}, pickup_time=sysdate(), update_time=sysdate() where bundle_id=#{bundleId}")
    int updatePickup(@Param("bundleId") Long bundleId, @Param("user") String user, @Param("status") String status);

    @Update("update biz_bundle set delivered_qty = delivered_qty + #{qty}, defect_qty = defect_qty + #{defect}, status=#{status}, update_time=sysdate() where bundle_id=#{bundleId}")
    int addDelivery(@Param("bundleId") Long bundleId, @Param("qty") int qty, @Param("defect") int defect, @Param("status") String status);

    @Update("update biz_bundle set packed_qty = packed_qty + #{qty}, status=#{status}, update_time=sysdate() where bundle_id=#{bundleId}")
    int addPacked(@Param("bundleId") Long bundleId, @Param("qty") int qty, @Param("status") String status);

    @Delete("delete from biz_bundle where bundle_id=#{bundleId}")
    int deleteBundle(Long bundleId);

    @Select("select count(1) from biz_bundle where cut_id=#{cutId}")
    int countByCut(Long cutId);

    @Select("select count(1) from biz_bundle where cut_id=#{cutId} and status='0'")
    int countNotStocked(Long cutId);

    @Select("select count(1) from biz_bundle")
    int countAll();

    @Select("select count(1) from biz_bundle where status=#{status}")
    int countByStatus(@Param("status") String status);

    @Select("select count(1) from biz_bundle where status in ('2','3') and delivered_qty >= qty and packed_qty < qty")
    int countPendingPack();
}
