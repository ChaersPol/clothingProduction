package com.ruoyi.system.mapper.garment;

import java.util.List;
import org.apache.ibatis.annotations.*;
import com.ruoyi.system.domain.garment.BizSupplier;

@Mapper
public interface BizSupplierMapper
{
    @Select("""
        <script>
        select * from biz_supplier
        <where>
        <if test="supplierName != null and supplierName != ''"> and supplier_name like concat('%',#{supplierName},'%')</if>
        <if test="contact != null and contact != ''"> and contact like concat('%',#{contact},'%')</if>
        <if test="supplyContent != null and supplyContent != ''"> and supply_content like concat('%',#{supplyContent},'%')</if>
        <if test="status != null and status != ''"> and status = #{status}</if>
        </where>
        order by supplier_id desc
        </script>
        """)
    List<BizSupplier> selectSupplierList(BizSupplier supplier);

    @Select("select * from biz_supplier where status='0' order by supplier_name")
    List<BizSupplier> selectSupplierAll();

    @Select("select * from biz_supplier where supplier_id=#{supplierId}")
    BizSupplier selectSupplierById(Long supplierId);

    @Select("""
        select s.*, coalesce(sum(p.total_amount),0) total_amount, coalesce(sum(p.paid_amount),0) paid_amount,
               coalesce(sum(p.total_amount - p.paid_amount),0) unpaid_amount, count(p.purchase_id) order_count
        from biz_supplier s left join biz_purchase p on p.supplier_id = s.supplier_id
        group by s.supplier_id order by s.supplier_id
        """)
    List<BizSupplier> selectReconciliation();

    @Insert("insert into biz_supplier(supplier_name,contact,phone,supply_content,address,status,create_by,create_time,remark) values(#{supplierName},#{contact},#{phone},#{supplyContent},#{address},#{status},#{createBy},sysdate(),#{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "supplierId")
    int insertSupplier(BizSupplier supplier);

    @Update("""
        update biz_supplier set supplier_name=#{supplierName},contact=#{contact},phone=#{phone},supply_content=#{supplyContent},
          address=#{address},status=#{status},update_by=#{updateBy},update_time=sysdate(),remark=#{remark}
        where supplier_id=#{supplierId}
        """)
    int updateSupplier(BizSupplier supplier);

    @Delete("delete from biz_supplier where supplier_id=#{supplierId}")
    int deleteSupplierById(Long supplierId);
}
