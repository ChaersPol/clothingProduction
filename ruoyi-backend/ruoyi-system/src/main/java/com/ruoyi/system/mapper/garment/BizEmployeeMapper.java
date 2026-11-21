package com.ruoyi.system.mapper.garment;

import java.util.List;
import org.apache.ibatis.annotations.*;
import com.ruoyi.system.domain.garment.BizEmployee;

@Mapper
public interface BizEmployeeMapper
{
    @Select("""
        <script>
        select * from biz_employee
        <where>
        <if test="name != null and name != ''"> and name like concat('%',#{name},'%')</if>
        <if test="position != null and position != ''"> and position like concat('%',#{position},'%')</if>
        <if test="status != null and status != ''"> and status = #{status}</if>
        </where>
        order by sort, employee_id
        </script>
        """)
    List<BizEmployee> selectEmployeeList(BizEmployee employee);

    @Select("select * from biz_employee where status='0' order by sort, employee_id")
    List<BizEmployee> selectEmployeeAll();

    @Select("select * from biz_employee where employee_id=#{employeeId}")
    BizEmployee selectEmployeeById(Long employeeId);

    @Insert("insert into biz_employee(name,position,sort,status,create_by,create_time,remark) values(#{name},#{position},#{sort},#{status},#{createBy},sysdate(),#{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "employeeId")
    int insertEmployee(BizEmployee employee);

    @Update("update biz_employee set name=#{name},position=#{position},sort=#{sort},status=#{status},update_by=#{updateBy},update_time=sysdate(),remark=#{remark} where employee_id=#{employeeId}")
    int updateEmployee(BizEmployee employee);

    @Delete("delete from biz_employee where employee_id=#{employeeId}")
    int deleteEmployeeById(Long employeeId);
}
