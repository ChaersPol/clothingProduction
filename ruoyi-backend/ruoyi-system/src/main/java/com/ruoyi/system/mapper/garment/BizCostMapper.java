package com.ruoyi.system.mapper.garment;

import java.util.List;
import org.apache.ibatis.annotations.*;
import com.ruoyi.system.domain.garment.BizCost;

@Mapper
public interface BizCostMapper
{
    @Select("""
        <script>
        select c.*, s.cn_name
        from biz_cost c left join biz_style s on s.style_no = c.style_no
        <where>
        <if test="styleNo != null and styleNo != ''"> and c.style_no like concat('%',#{styleNo},'%')</if>
        </where>
        order by c.cost_id desc
        </script>
        """)
    List<BizCost> selectCostList(BizCost cost);

    @Select("select c.*, s.cn_name from biz_cost c left join biz_style s on s.style_no=c.style_no where c.style_no=#{styleNo} limit 1")
    BizCost selectCostByStyleNo(String styleNo);

    @Select("select * from biz_cost where cost_id=#{costId}")
    BizCost selectCostById(Long costId);

    @Insert("""
        insert into biz_cost(style_no,fabric_cost,lining_cost,accessory_cost,trim_cost,labor_cost,pattern_cost,shoot_cost,operate_cost,
          first_freight,last_freight,sea_freight,other_cost,total_cost,ex_factory_price,suggest_price,create_by,create_time,remark)
        values(#{styleNo},#{fabricCost},#{liningCost},#{accessoryCost},#{trimCost},#{laborCost},#{patternCost},#{shootCost},#{operateCost},
          #{firstFreight},#{lastFreight},#{seaFreight},#{otherCost},#{totalCost},#{exFactoryPrice},#{suggestPrice},#{createBy},sysdate(),#{remark})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "costId")
    int insertCost(BizCost cost);

    @Update("""
        update biz_cost set fabric_cost=#{fabricCost},lining_cost=#{liningCost},accessory_cost=#{accessoryCost},trim_cost=#{trimCost},
          labor_cost=#{laborCost},pattern_cost=#{patternCost},shoot_cost=#{shootCost},operate_cost=#{operateCost},
          first_freight=#{firstFreight},last_freight=#{lastFreight},sea_freight=#{seaFreight},other_cost=#{otherCost},
          total_cost=#{totalCost},ex_factory_price=#{exFactoryPrice},suggest_price=#{suggestPrice},
          update_by=#{updateBy},update_time=sysdate(),remark=#{remark}
        where cost_id=#{costId}
        """)
    int updateCost(BizCost cost);

    @Delete("delete from biz_cost where cost_id=#{costId}")
    int deleteCostById(Long costId);
}
