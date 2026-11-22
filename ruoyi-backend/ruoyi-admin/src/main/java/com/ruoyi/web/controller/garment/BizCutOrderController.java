package com.ruoyi.web.controller.garment;

import java.util.List;
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
import com.ruoyi.system.domain.garment.BizCutOrder;
import com.ruoyi.system.service.garment.CuttingService;

/**
 * 裁货开单
 */
@RestController
@RequestMapping("/garment/cutorder")
public class BizCutOrderController extends BaseController
{
    @Autowired
    private CuttingService cuttingService;

    @PreAuthorize("@ss.hasPermi('garment:cutorder:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizCutOrder order)
    {
        startPage();
        return getDataTable(cuttingService.selectList(order));
    }

    @PreAuthorize("@ss.hasPermi('garment:cutorder:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizCutOrder order)
    {
        List<BizCutOrder> list = cuttingService.selectList(order);
        ExcelUtil<BizCutOrder> util = new ExcelUtil<>(BizCutOrder.class);
        util.exportExcel(response, list, "裁货单");
    }

    @PreAuthorize("@ss.hasPermi('garment:cutorder:list')")
    @GetMapping("/{cutId}")
    public AjaxResult getInfo(@PathVariable Long cutId)
    {
        return success(cuttingService.selectById(cutId));
    }

    @PreAuthorize("@ss.hasPermi('garment:cutorder:add')")
    @Log(title = "裁货开单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizCutOrder order)
    {
        return toAjax(cuttingService.save(order));
    }

    @PreAuthorize("@ss.hasPermi('garment:cutorder:edit')")
    @Log(title = "裁货开单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizCutOrder order)
    {
        return toAjax(cuttingService.save(order));
    }

    @PreAuthorize("@ss.hasPermi('garment:cutorder:remove')")
    @Log(title = "裁货开单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{cutIds}")
    public AjaxResult remove(@PathVariable Long[] cutIds)
    {
        for (Long cutId : cutIds)
        {
            cuttingService.delete(cutId);
        }
        return toAjax(cutIds.length);
    }
}
