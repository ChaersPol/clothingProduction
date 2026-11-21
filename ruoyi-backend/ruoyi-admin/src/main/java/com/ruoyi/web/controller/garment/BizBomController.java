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
import com.ruoyi.system.domain.garment.BizBom;
import com.ruoyi.system.service.garment.GoodsService;

/**
 * 用料清单 BOM
 */
@RestController
@RequestMapping("/garment/bom")
public class BizBomController extends BaseController
{
    @Autowired
    private GoodsService goodsService;

    @PreAuthorize("@ss.hasPermi('garment:bom:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizBom bom)
    {
        startPage();
        return getDataTable(goodsService.selectBomList(bom));
    }

    @PreAuthorize("@ss.hasPermi('garment:bom:list')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizBom bom)
    {
        List<BizBom> list = goodsService.selectBomList(bom);
        ExcelUtil<BizBom> util = new ExcelUtil<>(BizBom.class);
        util.exportExcel(response, list, "用料清单");
    }

    @PreAuthorize("@ss.hasPermi('garment:bom:add')")
    @Log(title = "用料清单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizBom bom)
    {
        return toAjax(goodsService.saveBom(bom));
    }

    @PreAuthorize("@ss.hasPermi('garment:bom:edit')")
    @Log(title = "用料清单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizBom bom)
    {
        return toAjax(goodsService.saveBom(bom));
    }

    @PreAuthorize("@ss.hasPermi('garment:bom:remove')")
    @Log(title = "用料清单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{bomIds}")
    public AjaxResult remove(@PathVariable Long[] bomIds)
    {
        int count = 0;
        for (Long id : bomIds)
        {
            count += goodsService.deleteBom(id);
        }
        return toAjax(count);
    }
}
