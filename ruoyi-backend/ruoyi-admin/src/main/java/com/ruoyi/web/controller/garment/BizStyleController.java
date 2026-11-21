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
import com.ruoyi.system.domain.garment.BizStyle;
import com.ruoyi.system.service.garment.GoodsService;

/**
 * 款号档案
 */
@RestController
@RequestMapping("/garment/style")
public class BizStyleController extends BaseController
{
    @Autowired
    private GoodsService goodsService;

    @PreAuthorize("@ss.hasPermi('garment:style:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizStyle style)
    {
        startPage();
        List<BizStyle> list = goodsService.selectStyleList(style);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('garment:style:list')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizStyle style)
    {
        List<BizStyle> list = goodsService.selectStyleList(style);
        ExcelUtil<BizStyle> util = new ExcelUtil<>(BizStyle.class);
        util.exportExcel(response, list, "款号档案");
    }

    @PreAuthorize("@ss.hasPermi('garment:style:list')")
    @GetMapping("/{styleId}")
    public AjaxResult getInfo(@PathVariable Long styleId)
    {
        return success(goodsService.selectStyleById(styleId));
    }

    @PreAuthorize("@ss.hasPermi('garment:style:add')")
    @Log(title = "款号档案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizStyle style)
    {
        return toAjax(goodsService.saveStyle(style));
    }

    @PreAuthorize("@ss.hasPermi('garment:style:edit')")
    @Log(title = "款号档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizStyle style)
    {
        return toAjax(goodsService.saveStyle(style));
    }

    @PreAuthorize("@ss.hasPermi('garment:style:remove')")
    @Log(title = "款号档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{styleIds}")
    public AjaxResult remove(@PathVariable Long[] styleIds)
    {
        int count = 0;
        for (Long id : styleIds)
        {
            count += goodsService.deleteStyle(id);
        }
        return toAjax(count);
    }
}
