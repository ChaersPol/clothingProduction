package com.ruoyi.system.mapper.garment;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.*;

@Mapper
public interface BizStatMapper
{
    /** SKU 出库排行 */
    @Select("""
        <script>
        select k.sku_id, k.style_no, k.parent_style_no, k.cn_name, k.color, k.size, k.barcode,
               sum(i.qty) as qty, sum(i.amount) as amount
        from biz_outbound_item i
        join biz_outbound o on o.outbound_id = i.outbound_id
        left join biz_sku k on k.sku_id = i.sku_id
        <where>
        <if test="beginDate != null and beginDate != ''"> and o.outbound_date &gt;= #{beginDate}</if>
        <if test="endDate != null and endDate != ''"> and o.outbound_date &lt;= #{endDate}</if>
        </where>
        group by i.sku_id, k.style_no, k.parent_style_no, k.cn_name, k.color, k.size, k.barcode
        order by
        <choose>
          <when test="sortBy == 'amount'">amount desc</when>
          <otherwise>qty desc</otherwise>
        </choose>
        limit 100
        </script>
        """)
    List<Map<String, Object>> outboundRankSku(@Param("beginDate") String beginDate, @Param("endDate") String endDate,
            @Param("sortBy") String sortBy);

    /** 父体款号出库排行 */
    @Select("""
        <script>
        select coalesce(nullif(k.parent_style_no,''), k.style_no) as style_no, max(k.cn_name) as cn_name,
               sum(i.qty) as qty, sum(i.amount) as amount
        from biz_outbound_item i
        join biz_outbound o on o.outbound_id = i.outbound_id
        left join biz_sku k on k.sku_id = i.sku_id
        <where>
        <if test="beginDate != null and beginDate != ''"> and o.outbound_date &gt;= #{beginDate}</if>
        <if test="endDate != null and endDate != ''"> and o.outbound_date &lt;= #{endDate}</if>
        </where>
        group by coalesce(nullif(k.parent_style_no,''), k.style_no)
        order by
        <choose>
          <when test="sortBy == 'amount'">amount desc</when>
          <otherwise>qty desc</otherwise>
        </choose>
        limit 100
        </script>
        """)
    List<Map<String, Object>> outboundRankStyle(@Param("beginDate") String beginDate, @Param("endDate") String endDate,
            @Param("sortBy") String sortBy);

    /** 三色库存汇总 */
    @Select("""
        select i.stock_type as stockType, sum(i.qty) as qty, sum(i.qty * k.cost_price) as value
        from biz_inventory i join biz_sku k on k.sku_id = i.sku_id
        group by i.stock_type
        """)
    List<Map<String, Object>> inventorySummary();

    /** 库存详情（含库龄、货值） */
    @Select("""
        <script>
        select k.style_no, k.cn_name, k.color, k.size, i.stock_type as stockType, i.qty,
               k.cost_price as costPrice, (i.qty * k.cost_price) as amount,
               datediff(sysdate(), date(i.update_time)) as ageDays
        from biz_inventory i join biz_sku k on k.sku_id = i.sku_id
        where i.qty &gt; 0
        <if test="styleNo != null and styleNo != ''"> and k.style_no like concat('%',#{styleNo},'%')</if>
        <if test="stockType != null and stockType != ''"> and i.stock_type = #{stockType}</if>
        order by amount desc
        </script>
        """)
    List<Map<String, Object>> inventoryDetail(@Param("styleNo") String styleNo, @Param("stockType") String stockType);

    /** 利润统计（按款） */
    @Select("""
        <script>
        select t.style_no, t.cn_name, t.revenue, t.cost,
               (select coalesce(sum(e.amount),0) from biz_expense e where e.style_no = t.style_no and ifnull(e.status,'0') &lt;&gt; '9') as expense,
               (t.revenue - t.cost - (select coalesce(sum(e.amount),0) from biz_expense e where e.style_no = t.style_no and ifnull(e.status,'0') &lt;&gt; '9')) as profit
        from (
          select coalesce(nullif(k.parent_style_no,''), k.style_no) as style_no, max(k.cn_name) as cn_name,
                 sum(i.amount) as revenue, sum(i.qty * k.cost_price) as cost
          from biz_outbound_item i
          join biz_outbound o on o.outbound_id = i.outbound_id
          left join biz_sku k on k.sku_id = i.sku_id
          <where>
          <if test="beginDate != null and beginDate != ''"> and o.outbound_date &gt;= #{beginDate}</if>
          <if test="endDate != null and endDate != ''"> and o.outbound_date &lt;= #{endDate}</if>
          </where>
          group by coalesce(nullif(k.parent_style_no,''), k.style_no)
        ) t
        order by profit desc
        </script>
        """)
    List<Map<String, Object>> profitByStyle(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    /** 退货统计（含退货率 = 退货数量 / 同期出库数量） */
    @Select("""
        <script>
        select g.*, case when ifnull(o.outQty,0) &gt; 0 then round(g.qty * 100 / o.outQty, 2) else 0 end as rate
        from (
          select coalesce(nullif(k.parent_style_no,''), r.style_no) as style_no, max(k.cn_name) as cn_name,
                 sum(r.qty) as qty, sum(r.good_qty) as goodQty, sum(r.bad_qty) as badQty, count(1) as times
          from biz_return r left join biz_sku k on k.sku_id = r.sku_id
          <where>
          <if test="beginDate != null and beginDate != ''"> and r.return_date &gt;= #{beginDate}</if>
          <if test="endDate != null and endDate != ''"> and r.return_date &lt;= #{endDate}</if>
          </where>
          group by coalesce(nullif(k.parent_style_no,''), r.style_no)
        ) g
        left join (
          select coalesce(nullif(k2.parent_style_no,''), k2.style_no) as style_no, sum(i.qty) as outQty
          from biz_outbound_item i
          join biz_outbound o2 on o2.outbound_id = i.outbound_id
          left join biz_sku k2 on k2.sku_id = i.sku_id
          <where>
          <if test="beginDate != null and beginDate != ''"> and o2.outbound_date &gt;= #{beginDate}</if>
          <if test="endDate != null and endDate != ''"> and o2.outbound_date &lt;= #{endDate}</if>
          </where>
          group by coalesce(nullif(k2.parent_style_no,''), k2.style_no)
        ) o on o.style_no = g.style_no
        order by g.qty desc
        </script>
        """)
    List<Map<String, Object>> returnByStyle(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    /** 退货原因统计 */
    @Select("""
        <script>
        select reason, sum(qty) as qty, count(1) as times from biz_return
        <where>
        <if test="beginDate != null and beginDate != ''"> and return_date &gt;= #{beginDate}</if>
        <if test="endDate != null and endDate != ''"> and return_date &lt;= #{endDate}</if>
        </where>
        group by reason order by qty desc
        </script>
        """)
    List<Map<String, Object>> returnByReason(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    /** 首页统计 */
    @Select("select (select count(1) from biz_style where del_flag='0') as styleCount, (select count(1) from biz_sku where status='0') as skuCount")
    Map<String, Object> baseCount();

    @Select("select coalesce(sum(total_qty),0) from biz_cut_order where ifnull(status,'0') != '9' and date_format(cut_date,'%Y-%m-%d')=curdate()")
    Integer todayCutQty();

    @Select("select coalesce(sum(total_qty),0) from biz_outbound where outbound_date=curdate()")
    Integer todayOutQty();

    @Select("select coalesce(sum(total_amount),0) from biz_outbound where outbound_date=curdate()")
    java.math.BigDecimal todayOutAmount();

    @Select("select coalesce(sum(change_qty),0) from biz_stock_log where biz_type='成品入库' and date_format(operate_time,'%Y-%m-%d')=curdate()")
    Integer todayInQty();

    @Select("select count(1) from biz_bundle where status='1'")
    Integer cutStockBundles();

    @Select("select count(1) from biz_bundle where status='2' and delivered_qty < qty")
    Integer onlineBundles();

    @Select("select count(1) from biz_bundle where status='2' and delivered_qty < qty and datediff(sysdate(), date(pickup_time)) > 3")
    Integer overdueBundles();

    /** 最近7天出库趋势 */
    @Select("""
        select date_format(outbound_date,'%m-%d') as day, sum(total_qty) as qty, sum(total_amount) as amount
        from biz_outbound where outbound_date >= date_sub(curdate(), interval 6 day)
        group by outbound_date order by outbound_date
        """)
    List<Map<String, Object>> outboundTrend();

    /** 裁床产量 */
    @Select("""
        <script>
        select max(cutter) as cutter, count(1) as orders, sum(total_qty) as qty, sum(layer_count) as layers
        from biz_cut_order
        <where>
        ifnull(status,'0') &lt;&gt; '9'
        <if test="beginDate != null and beginDate != ''"> and cut_date &gt;= #{beginDate}</if>
        <if test="endDate != null and endDate != ''"> and cut_date &lt;= #{endDate}</if>
        </where>
        group by ifnull(cutter_id, 0) order by qty desc
        </script>
        """)
    List<Map<String, Object>> cutStatByCutter(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("""
        <script>
        select cut_date as day, count(1) as beds, sum(total_qty) as qty
        from biz_cut_order
        <where>
        ifnull(status,'0') &lt;&gt; '9'
        <if test="beginDate != null and beginDate != ''"> and cut_date &gt;= #{beginDate}</if>
        <if test="endDate != null and endDate != ''"> and cut_date &lt;= #{endDate}</if>
        </where>
        group by cut_date order by cut_date desc
        </script>
        """)
    List<Map<String, Object>> cutStatByDay(@Param("beginDate") String beginDate, @Param("endDate") String endDate);
}
