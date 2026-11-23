package com.ruoyi.system.mapper.garment;

import java.util.List;
import org.apache.ibatis.annotations.*;
import com.ruoyi.system.domain.garment.BizExpense;

@Mapper
public interface BizExpenseMapper
{
    @Select("""
        <script>
        select * from biz_expense
        <where>
        <if test="expenseType != null and expenseType != ''"> and expense_type = #{expenseType}</if>
        <if test="styleNo != null and styleNo != ''"> and style_no = #{styleNo}</if>
        <if test="params.beginDate != null and params.beginDate != ''"> and expense_date &gt;= #{params.beginDate}</if>
        <if test="params.endDate != null and params.endDate != ''"> and expense_date &lt;= #{params.endDate}</if>
        <if test="status != null and status != ''"> and status = #{status}</if>
        </where>
        order by expense_date desc, expense_id desc
        </script>
        """)
    List<BizExpense> selectExpenseList(BizExpense expense);

    @Select("select * from biz_expense where expense_id=#{expenseId}")
    BizExpense selectExpenseById(Long expenseId);

    @Insert("insert into biz_expense(expense_type,style_no,amount,expense_date,create_by,create_time,remark) values(#{expenseType},#{styleNo},#{amount},#{expenseDate},#{createBy},sysdate(),#{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "expenseId")
    int insertExpense(BizExpense expense);

    @Update("update biz_expense set expense_type=#{expenseType},style_no=#{styleNo},amount=#{amount},expense_date=#{expenseDate},remark=#{remark} where expense_id=#{expenseId}")
    int updateExpense(BizExpense expense);

    @Update("update biz_expense set status='9', cancel_reason=#{reason}, remark=concat(ifnull(remark,''),' 作废:',#{reason}) where expense_id=#{expenseId}")
    int voidExpense(@Param("expenseId") Long expenseId, @Param("reason") String reason);

    @Delete("delete from biz_expense where expense_id=#{expenseId}")
    int deleteExpense(Long expenseId);
}
