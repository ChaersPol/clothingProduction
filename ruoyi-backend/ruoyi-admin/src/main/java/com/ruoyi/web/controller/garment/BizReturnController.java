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
import com.ruoyi.system.domain.garment.BizReturn;
import com.ruoyi.system.service.garment.WarehouseService;

/**
 * 退货入库
 */
@RestController
@RequestMapping("/garment/return")
public class BizReturnController extends BaseController
{
    @Autowired
    private WarehouseService warehouseService;

    @PreAuthorize("@ss.hasPermi('garment:return:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizReturn query)
    {
        startPage();
        return getDataTable(warehouseService.selectReturnList(query));
    }

    @PreAuthorize("@ss.hasPermi('garment:return:list')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizReturn query)
    {
        List<BizReturn> list = warehouseService.selectReturnList(query);
        ExcelUtil<BizReturn> util = new ExcelUtil<>(BizReturn.class);
        util.exportExcel(response, list, "退货入库");
    }

    @PreAuthorize("@ss.hasPermi('garment:return:add')")
    @Log(title = "退货入库", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizReturn ret)
    {
        return toAjax(warehouseService.saveReturn(ret));
    }

    @PreAuthorize("@ss.hasPermi('garment:return:remove')")
    @Log(title = "退货入库", businessType = BusinessType.DELETE)
    @DeleteMapping("/{returnIds}")
    public AjaxResult remove(@PathVariable Long[] returnIds)
    {
        for (Long returnId : returnIds)
        {
            warehouseService.deleteReturn(returnId);
        }
        return toAjax(returnIds.length);
    }
}
