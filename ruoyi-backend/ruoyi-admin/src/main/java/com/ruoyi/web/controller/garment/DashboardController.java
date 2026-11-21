package com.ruoyi.web.controller.garment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.garment.ReportService;

/**
 * 首页看板 / 消息提醒 / 裁床产量
 */
@RestController
@RequestMapping("/garment")
public class DashboardController extends BaseController
{
    @Autowired
    private ReportService reportService;

    @GetMapping("/dashboard")
    public AjaxResult dashboard()
    {
        return success(reportService.dashboard());
    }

    @PreAuthorize("@ss.hasPermi('garment:message:list')")
    @GetMapping("/message/list")
    public AjaxResult messages()
    {
        return success(reportService.messages());
    }

    @PreAuthorize("@ss.hasPermi('garment:cutstat:list')")
    @GetMapping("/cutstat/cutter")
    public AjaxResult cutByCutter(@RequestParam(required = false) String beginDate, @RequestParam(required = false) String endDate)
    {
        return success(reportService.cutByCutter(beginDate, endDate));
    }

    @PreAuthorize("@ss.hasPermi('garment:cutstat:list')")
    @GetMapping("/cutstat/day")
    public AjaxResult cutByDay(@RequestParam(required = false) String beginDate, @RequestParam(required = false) String endDate)
    {
        return success(reportService.cutByDay(beginDate, endDate));
    }
}
