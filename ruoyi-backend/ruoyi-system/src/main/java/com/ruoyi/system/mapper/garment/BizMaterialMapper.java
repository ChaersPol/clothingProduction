package com.ruoyi.system.mapper.garment;

import java.util.List;
import org.apache.ibatis.annotations.*;
import com.ruoyi.system.domain.garment.BizMaterial;
import com.ruoyi.system.domain.garment.BizMaterialLog;

@Mapper
public interface BizMaterialMapper
{
    @Select("""
        <script>
        select * from biz_material
        <where>
        del_flag = '0'
        <if test="materialName != null and materialName != ''"> and material_name like concat('%',#{materialName},'%')</if>
        <if test="materialType != null and materialType != ''"> and material_type = #{materialType}</if>
        <if test='params.warning != null and params.warning == "1"'> and stock_qty &lt;= min_stock and min_stock &gt; 0</if>
        </where>
        order by material_type, material_name
        </script>
        """)
    List<BizMaterial> selectMaterialList(BizMaterial query);

    @Select("select * from biz_material where material_id=#{materialId}")
    BizMaterial selectMaterialById(Long materialId);

    @Select("""
        select * from biz_material
        where material_name=#{name} and ifnull(spec,'')=ifnull(#{spec},'') and del_flag='0' limit 1
        """)
    BizMaterial selectByKey(@Param("name") String name, @Param("spec") String spec);

    @Select("select * from biz_material where material_id=#{materialId} for update")
    BizMaterial lockById(Long materialId);

    @Insert("""
        insert into biz_material(material_code,material_name,spec,unit,material_type,stock_qty,min_stock,price,supplier_id,supplier_name,del_flag,create_by,create_time)
        values(#{materialCode},#{materialName},#{spec},#{unit},#{materialType},#{stockQty},#{minStock},#{price},#{supplierId},#{supplierName},'0',#{createBy},sysdate())
        """)
    @Options(useGeneratedKeys = true, keyProperty = "materialId")
    int insertMaterial(BizMaterial material);

    @Update("update biz_material set stock_qty=#{stockQty}, update_time=sysdate() where material_id=#{materialId}")
    int updateStock(@Param("materialId") Long materialId, @Param("stockQty") java.math.BigDecimal stockQty);

    @Update("""
        update biz_material set material_name=#{materialName},spec=#{spec},unit=#{unit},material_type=#{materialType},
          min_stock=#{minStock},price=#{price},update_by=#{updateBy},update_time=sysdate()
        where material_id=#{materialId}
        """)
    int updateMaterial(BizMaterial material);

    @Update("update biz_material set min_stock=#{minStock}, update_time=sysdate() where material_id=#{materialId}")
    int updateMinStock(@Param("materialId") Long materialId, @Param("minStock") java.math.BigDecimal minStock);

    @Update("update biz_material set price=#{price}, update_time=sysdate() where material_id=#{materialId}")
    int updatePrice(@Param("materialId") Long materialId, @Param("price") java.math.BigDecimal price);

    @Select("""
        <script>
        select * from biz_material_log
        <where>
        <if test="materialName != null and materialName != ''"> and material_name like concat('%',#{materialName},'%')</if>
        <if test="bizType != null and bizType != ''"> and biz_type = #{bizType}</if>
        <if test="bizNo != null and bizNo != ''"> and biz_no like concat('%',#{bizNo},'%')</if>
        <if test="params.beginDate != null and params.beginDate != ''"> and operate_time &gt;= #{params.beginDate}</if>
        <if test="params.endDate != null and params.endDate != ''"> and operate_time &lt; #{params.endDate} + interval 1 day</if>
        </where>
        order by log_id desc
        </script>
        """)
    List<BizMaterialLog> selectLogList(BizMaterialLog query);

    @Insert("""
        insert into biz_material_log(material_id,material_name,spec,unit,change_qty,before_qty,after_qty,biz_type,biz_no,operator,operate_time,remark)
        values(#{materialId},#{materialName},#{spec},#{unit},#{changeQty},#{beforeQty},#{afterQty},#{bizType},#{bizNo},#{operator},sysdate(),#{remark})
        """)
    int insertLog(BizMaterialLog log);

    @Select("select count(1) from biz_material_log where biz_no=#{bizNo} and biz_type=#{bizType}")
    int countLog(@Param("bizNo") String bizNo, @Param("bizType") String bizType);
}
