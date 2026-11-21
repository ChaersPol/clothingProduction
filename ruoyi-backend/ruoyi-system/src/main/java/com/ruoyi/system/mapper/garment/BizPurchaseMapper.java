package com.ruoyi.system.mapper.garment;

import java.util.List;
import org.apache.ibatis.annotations.*;
import com.ruoyi.system.domain.garment.BizPurchase;
import com.ruoyi.system.domain.garment.BizPurchaseItem;

@Mapper
public interface BizPurchaseMapper
{
    @Select("""
        <script>
        select * from biz_purchase
        <where>
        <if test="purchaseNo != null and purchaseNo != ''"> and purchase_no like concat('%',#{purchaseNo},'%')</if>
        <if test="purchaseType != null and purchaseType != ''"> and purchase_type = #{purchaseType}</if>
        <if test="category != null and category != ''"> and category = #{category}</if>
        <if test="styleNo != null and styleNo != ''"> and style_no = #{styleNo}</if>
        <if test="supplierId != null"> and supplier_id = #{supplierId}</if>
        <if test="status != null and status != ''"> and status = #{status}</if>
        <if test="params.beginDate != null and params.beginDate != ''"> and purchase_date &gt;= #{params.beginDate}</if>
        <if test="params.endDate != null and params.endDate != ''"> and purchase_date &lt;= #{params.endDate}</if>
        </where>
        order by purchase_date desc, purchase_id desc
        </script>
        """)
    List<BizPurchase> selectPurchaseList(BizPurchase purchase);

    @Select("select * from biz_purchase where purchase_id=#{purchaseId}")
    BizPurchase selectPurchaseById(Long purchaseId);

    @Select("select * from biz_purchase_item where purchase_id=#{purchaseId} order by item_id")
    List<BizPurchaseItem> selectItems(Long purchaseId);

    @Select("select * from biz_purchase_item where item_id=#{itemId}")
    BizPurchaseItem selectItemById(Long itemId);

    @Select("""
        <script>
        select i.*, p.purchase_no, p.purchase_type, p.supplier_name
        from biz_purchase_item i join biz_purchase p on p.purchase_id = i.purchase_id
        <where>
        <if test="purchaseNo != null and purchaseNo != ''"> and p.purchase_no like concat('%',#{purchaseNo},'%')</if>
        <if test="purchaseType != null and purchaseType != ''"> and p.purchase_type = #{purchaseType}</if>
        <if test="materialName != null and materialName != ''"> and i.material_name like concat('%',#{materialName},'%')</if>
        <if test="supplierId != null"> and p.supplier_id = #{supplierId}</if>
        <if test="params.beginDate != null and params.beginDate != ''"> and p.purchase_date &gt;= #{params.beginDate}</if>
        <if test="params.endDate != null and params.endDate != ''"> and p.purchase_date &lt;= #{params.endDate}</if>
        </where>
        order by p.purchase_date desc, i.item_id desc
        </script>
        """)
    List<java.util.Map<String, Object>> selectItemList(BizPurchase purchase);

    @Insert("""
        insert into biz_purchase(purchase_no,purchase_type,category,style_no,supplier_id,supplier_name,purchase_date,total_amount,paid_amount,status,create_by,create_time,remark)
        values(#{purchaseNo},#{purchaseType},#{category},#{styleNo},#{supplierId},#{supplierName},#{purchaseDate},#{totalAmount},#{paidAmount},#{status},#{createBy},sysdate(),#{remark})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "purchaseId")
    int insertPurchase(BizPurchase purchase);

    @Insert("""
        insert into biz_purchase_item(purchase_id,material_type,material_name,spec,unit,price,qty,received_qty,amount,remark)
        values(#{purchaseId},#{materialType},#{materialName},#{spec},#{unit},#{price},#{qty},#{receivedQty},#{amount},#{remark})
        """)
    int insertItem(BizPurchaseItem item);

    @Delete("delete from biz_purchase_item where purchase_id=#{purchaseId}")
    int deleteItems(Long purchaseId);

    @Update("""
        update biz_purchase set purchase_type=#{purchaseType},category=#{category},style_no=#{styleNo},supplier_id=#{supplierId},
          supplier_name=#{supplierName},purchase_date=#{purchaseDate},total_amount=#{totalAmount},paid_amount=#{paidAmount},
          status=#{status},update_by=#{updateBy},update_time=sysdate(),remark=#{remark}
        where purchase_id=#{purchaseId}
        """)
    int updatePurchase(BizPurchase purchase);

    @Update("update biz_purchase_item set received_qty=received_qty + #{qty} where item_id=#{itemId}")
    int receiveItem(@Param("itemId") Long itemId, @Param("qty") java.math.BigDecimal qty);

    @Update("update biz_purchase set status=#{status}, update_time=sysdate() where purchase_id=#{purchaseId}")
    int updateStatus(@Param("purchaseId") Long purchaseId, @Param("status") String status);

    @Delete("delete from biz_purchase where purchase_id=#{purchaseId}")
    int deletePurchase(Long purchaseId);

    @Select("select count(1) from biz_purchase where purchase_type=#{type} and date_format(create_time,'%Y%m%d')=date_format(sysdate(),'%Y%m%d')")
    int countToday(@Param("type") String type);

    @Select("select count(1) from biz_purchase where date_format(create_time,'%Y%m%d')=date_format(sysdate(),'%Y%m%d')")
    int countTodayAll();
}
