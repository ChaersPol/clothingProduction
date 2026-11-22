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
import com.ruoyi.system.domain.garment.BizCost;
import com.ruoyi.system.service.garment.GoodsService;

/**
 * 成本核算
 */
@RestController
@RequestMapping("/garment/cost")
public class BizCostController extends BaseController
{
    @Autowired
    private GoodsService goodsService;

    @PreAuthorize("@ss.hasPermi('garment:cost:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizCost cost)
    {
        startPage();
        return getDataTable(goodsService.selectCostList(cost));
    }

    @PreAuthorize("@ss.hasPermi('garment:cost:list')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizCost cost)
    {
        List<BizCost> list = goodsService.selectCostList(cost);
        ExcelUtil<BizCost> util = new ExcelUtil<>(BizCost.class);
        util.exportExcel(response, list, "成本核算");
    }

    @PreAuthorize("@ss.hasPermi('garment:cost:list')")
    @GetMapping("/style/{styleNo}")
    public AjaxResult getByStyle(@PathVariable String styleNo)
    {
        return success(goodsService.selectCostByStyleNo(styleNo));
    }

    @PreAuthorize("@ss.hasAnyPermi('garment:cost:add,garment:cost:edit')")
    @Log(title = "成本核算", businessType = BusinessType.UPDATE)
    @PostMapping
    public AjaxResult save(@RequestBody BizCost cost)
    {
        return toAjax(goodsService.saveCost(cost));
    }

    @PreAuthorize("@ss.hasPermi('garment:cost:remove')")
    @Log(title = "成本核算", businessType = BusinessType.DELETE)
    @DeleteMapping("/{costId}")
    public AjaxResult remove(@PathVariable Long costId)
    {
        return toAjax(goodsService.deleteCost(costId));
    }
}
