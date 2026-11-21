package com.ruoyi.web.controller.garment;

import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.garment.BizOutbound;
import com.ruoyi.system.service.garment.WarehouseService;

/**
 * 成品出库
 */
@RestController
@RequestMapping("/garment/outbound")
public class BizOutboundController extends BaseController
{
    @Autowired
    private WarehouseService warehouseService;

    @PreAuthorize("@ss.hasPermi('garment:outbound:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizOutbound query)
    {
        startPage();
        return getDataTable(warehouseService.selectOutboundList(query));
    }

    @PreAuthorize("@ss.hasPermi('garment:outbound:list')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizOutbound query)
    {
        List<BizOutbound> list = warehouseService.selectOutboundList(query);
        ExcelUtil<BizOutbound> util = new ExcelUtil<>(BizOutbound.class);
        util.exportExcel(response, list, "成品出库单");
    }

    @PreAuthorize("@ss.hasPermi('garment:outbound:list')")
    @GetMapping("/{outboundId}")
    public AjaxResult getInfo(@PathVariable Long outboundId)
    {
        return success(warehouseService.selectOutboundById(outboundId));
    }

    @PreAuthorize("@ss.hasPermi('garment:outbound:list')")
    @Log(title = "成品出库", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizOutbound outbound)
    {
        return success(warehouseService.saveOutbound(outbound));
    }

    @PreAuthorize("@ss.hasPermi('garment:outbound:list')")
    @Log(title = "成品出库作废", businessType = BusinessType.UPDATE)
    @PostMapping("/{outboundId}/cancel")
    public AjaxResult cancel(@PathVariable Long outboundId, @RequestBody Map<String, Object> body)
    {
        String reason = body.get("reason") == null ? "" : String.valueOf(body.get("reason"));
        return toAjax(warehouseService.cancelOutbound(outboundId, reason));
    }
}
