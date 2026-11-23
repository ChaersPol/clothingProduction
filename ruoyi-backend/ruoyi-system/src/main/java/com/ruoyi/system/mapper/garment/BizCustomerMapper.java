package com.ruoyi.system.mapper.garment;

import java.util.List;
import org.apache.ibatis.annotations.*;
import com.ruoyi.system.domain.garment.BizCustomer;

@Mapper
public interface BizCustomerMapper
{
    @Select("""
        <script>
        select * from biz_customer
        <where>
        <if test="customerName != null and customerName != ''"> and customer_name like concat('%',#{customerName},'%')</if>
        <if test="contact != null and contact != ''"> and contact like concat('%',#{contact},'%')</if>
        <if test="phone != null and phone != ''"> and phone like concat('%',#{phone},'%')</if>
        <if test="status != null and status != ''"> and status = #{status}</if>
        </where>
        order by customer_id desc
        </script>
        """)
    List<BizCustomer> selectCustomerList(BizCustomer customer);

    @Select("select * from biz_customer where status='0' order by customer_name")
    List<BizCustomer> selectCustomerAll();

    @Select("select * from biz_customer where customer_id=#{customerId}")
    BizCustomer selectCustomerById(Long customerId);

    @Select("select * from biz_customer where customer_name=#{customerName} limit 1")
    BizCustomer selectByName(String customerName);

    @Select("select count(1) from biz_customer where customer_name=#{customerName} and customer_id<>#{customerId}")
    int countByName(@Param("customerName") String customerName, @Param("customerId") Long customerId);

    /** 统计该客户被业务单据引用的次数，被引用时不允许删除 */
    @Select("""
        select (select count(1) from biz_cut_order where customer=#{name})
             + (select count(1) from biz_shipment where customer=#{name})
             + (select count(1) from biz_outbound where customer=#{name})
             + (select count(1) from biz_return where customer=#{name})
        """)
    int countRefs(String name);

    @Insert("insert into biz_customer(customer_name,contact,phone,address,status,create_by,create_time,remark) values(#{customerName},#{contact},#{phone},#{address},#{status},#{createBy},sysdate(),#{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "customerId")
    int insertCustomer(BizCustomer customer);

    @Update("""
        update biz_customer set customer_name=#{customerName},contact=#{contact},phone=#{phone},address=#{address},
          status=#{status},update_by=#{updateBy},update_time=sysdate(),remark=#{remark}
        where customer_id=#{customerId}
        """)
    int updateCustomer(BizCustomer customer);

    @Delete("delete from biz_customer where customer_id=#{customerId}")
    int deleteCustomerById(Long customerId);
}
