package com.ruoyi.web.controller.garment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.garment.BizExpense;
import com.ruoyi.system.service.garment.ReportService;

/**
 * 财务看板
 */
@RestController
@RequestMapping("/garment")
public class FinanceController extends BaseController
{
    @Autowired
    private ReportService reportService;

    @PreAuthorize("@ss.hasPermi('garment:finance:list')")
    @GetMapping("/finance/rankSku")
    public AjaxResult rankSku(@RequestParam(required = false) String beginDate, @RequestParam(required = false) String endDate)
    {
        return success(reportService.rankSku(beginDate, endDate));
    }

    @PreAuthorize("@ss.hasPermi('garment:finance:list')")
    @GetMapping("/finance/rankStyle")
    public AjaxResult rankStyle(@RequestParam(required = false) String beginDate, @RequestParam(required = false) String endDate)
    {
        return success(reportService.rankStyle(beginDate, endDate));
    }

    @PreAuthorize("@ss.hasPermi('garment:finance:list')")
    @GetMapping("/finance/inventoryDetail")
    public AjaxResult inventoryDetail(@RequestParam(required = false) String styleNo,
            @RequestParam(required = false) String stockType)
    {
        return success(reportService.inventoryDetail(styleNo, stockType));
    }

    @PreAuthorize("@ss.hasPermi('garment:finance:list')")
    @GetMapping("/finance/profit")
    public AjaxResult profit(@RequestParam(required = false) String beginDate, @RequestParam(required = false) String endDate)
    {
        return success(reportService.profit(beginDate, endDate));
    }

    @PreAuthorize("@ss.hasPermi('garment:finance:list')")
    @GetMapping("/finance/returnStats")
    public AjaxResult returnStats(@RequestParam(required = false) String beginDate, @RequestParam(required = false) String endDate)
    {
        return success(reportService.returnStats(beginDate, endDate));
    }

    /* ---------------- 费用登记 ---------------- */
    @PreAuthorize("@ss.hasPermi('garment:expense:list')")
    @GetMapping("/expense/list")
    public TableDataInfo expenseList(BizExpense expense)
    {
        startPage();
        return getDataTable(reportService.selectExpenseList(expense));
    }

    @PreAuthorize("@ss.hasPermi('garment:expense:add')")
    @Log(title = "费用登记", businessType = BusinessType.INSERT)
    @PostMapping("/expense")
    public AjaxResult addExpense(@RequestBody BizExpense expense)
    {
        return toAjax(reportService.saveExpense(expense));
    }

    @PreAuthorize("@ss.hasPermi('garment:expense:edit')")
    @Log(title = "费用登记", businessType = BusinessType.UPDATE)
    @PutMapping("/expense")
    public AjaxResult editExpense(@RequestBody BizExpense expense)
    {
        return toAjax(reportService.saveExpense(expense));
    }

    @PreAuthorize("@ss.hasPermi('garment:expense:remove')")
    @Log(title = "费用登记", businessType = BusinessType.DELETE)
    @DeleteMapping("/expense/{expenseIds}")
    public AjaxResult removeExpense(@PathVariable Long[] expenseIds)
    {
        for (Long expenseId : expenseIds)
        {
            reportService.deleteExpense(expenseId);
        }
        return toAjax(expenseIds.length);
    }
}
