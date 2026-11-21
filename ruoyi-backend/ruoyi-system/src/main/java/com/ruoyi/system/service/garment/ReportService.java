package com.ruoyi.system.service.garment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.garment.BizExpense;
import com.ruoyi.system.mapper.garment.*;

/**
 * 统计报表 / 首页看板 / 消息提醒 / 财务分析
 */
@Service
public class ReportService
{
    @Autowired
    private BizStatMapper statMapper;
    @Autowired
    private BizInventoryMapper inventoryMapper;
    @Autowired
    private BizBundleMapper bundleMapper;
    @Autowired
    private BizCutOrderMapper cutOrderMapper;
    @Autowired
    private BizPickMapper pickMapper;
    @Autowired
    private BizDeliveryMapper deliveryMapper;
    @Autowired
    private BizPackMapper packMapper;
    @Autowired
    private BizExpenseMapper expenseMapper;

    public Map<String, Object> dashboard()
    {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> base = statMapper.baseCount();
        if (base != null)
        {
            data.putAll(base);
        }
        data.put("inventory", statMapper.inventorySummary());
        data.put("todayCutQty", nvl(statMapper.todayCutQty()));
        data.put("todayOutQty", nvl(statMapper.todayOutQty()));
        data.put("todayOutAmount", statMapper.todayOutAmount() == null ? BigDecimal.ZERO : statMapper.todayOutAmount());
        data.put("todayInQty", nvl(statMapper.todayInQty()));
        data.put("cutStockBundles", nvl(statMapper.cutStockBundles()));
        data.put("onlineBundles", nvl(statMapper.onlineBundles()));
        data.put("overdueBundles", nvl(statMapper.overdueBundles()));
        data.put("trend", statMapper.outboundTrend());
        data.put("warnings", inventoryMapper.selectWarnings(null));
        return data;
    }

    public List<Map<String, Object>> messages()
    {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map<String, Object> row : inventoryMapper.selectWarnings(null))
        {
            int minStock = ((Number) row.get("min_stock")).intValue();
            int finish = ((Number) row.get("finish_qty")).intValue();
            list.add(message("warning", "库存不足提醒",
                    row.get("style_no") + " " + row.get("color") + " " + row.get("size")
                            + " 成品库存 " + finish + "，低于最低库存 " + minStock));
        }
        int unbundled = cutOrderMapper.countByStatus("0");
        if (unbundled > 0)
        {
            list.add(message("task", "裁货单待分扎", "有 " + unbundled + " 张裁货单尚未分扎，请前往裁床管理处理"));
        }
        int toPick = bundleMapper.countByStatus("1");
        if (toPick > 0)
        {
            list.add(message("task", "裁片待领片", "裁片库有 " + toPick + " 扎裁片等待车间扫码领片"));
        }
        int toPack = bundleMapper.countPendingPack();
        if (toPack > 0)
        {
            list.add(message("task", "完工待入库", "有 " + toPack + " 扎已完工待后道包装入库"));
        }
        int overdue = nvl(statMapper.overdueBundles());
        if (overdue > 0)
        {
            list.add(message("warning", "产线超期提醒", "有 " + overdue + " 扎在产线停留超过 3 天，请关注进度"));
        }
        if (list.isEmpty())
        {
            list.add(message("info", "暂无待办", "当前没有库存预警和待处理单据"));
        }
        return list;
    }

    private Map<String, Object> message(String type, String title, String content)
    {
        Map<String, Object> msg = new HashMap<>();
        msg.put("type", type);
        msg.put("title", title);
        msg.put("content", content);
        msg.put("time", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date()));
        return msg;
    }

    public List<Map<String, Object>> rankSku(String beginDate, String endDate)
    {
        return statMapper.outboundRankSku(beginDate, endDate);
    }

    public List<Map<String, Object>> rankStyle(String beginDate, String endDate)
    {
        return statMapper.outboundRankStyle(beginDate, endDate);
    }

    public List<Map<String, Object>> inventoryDetail(String styleNo)
    {
        return statMapper.inventoryDetail(styleNo);
    }

    public List<Map<String, Object>> profit(String beginDate, String endDate)
    {
        return statMapper.profitByStyle(beginDate, endDate);
    }

    public Map<String, Object> returnStats(String beginDate, String endDate)
    {
        Map<String, Object> data = new HashMap<>();
        data.put("byStyle", statMapper.returnByStyle(beginDate, endDate));
        data.put("byReason", statMapper.returnByReason(beginDate, endDate));
        return data;
    }

    public List<Map<String, Object>> cutByCutter(String beginDate, String endDate)
    {
        return statMapper.cutStatByCutter(beginDate, endDate);
    }

    public List<Map<String, Object>> cutByDay(String beginDate, String endDate)
    {
        return statMapper.cutStatByDay(beginDate, endDate);
    }

    public List<Map<String, Object>> pickByEmployee(String date)
    {
        return pickMapper.statByEmployee(date);
    }

    public List<Map<String, Object>> deliveryByEmployee(String date)
    {
        return deliveryMapper.statByEmployee(date);
    }

    public List<Map<String, Object>> packByPacker(String date)
    {
        return packMapper.statByPacker(date);
    }

    public List<BizExpense> selectExpenseList(BizExpense expense)
    {
        return expenseMapper.selectExpenseList(expense);
    }

    public int saveExpense(BizExpense expense)
    {
        if (expense.getExpenseId() == null)
        {
            expense.setCreateBy(SecurityUtils.getUsername());
            return expenseMapper.insertExpense(expense);
        }
        return expenseMapper.updateExpense(expense);
    }

    public int deleteExpense(Long id)
    {
        return expenseMapper.deleteExpense(id);
    }

    private int nvl(Integer value)
    {
        return value == null ? 0 : value;
    }
}
