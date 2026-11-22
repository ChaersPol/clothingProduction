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
import com.ruoyi.system.domain.garment.BizBundle;
import com.ruoyi.system.service.garment.CuttingService;

/**
 * 分扎、扎卡、裁片入库、补片报废
 */
@RestController
@RequestMapping("/garment/bundle")
public class BizBundleController extends BaseController
{
    @Autowired
    private CuttingService cuttingService;

    @PreAuthorize("@ss.hasPermi('garment:bundle:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizBundle bundle)
    {
        startPage();
        return getDataTable(cuttingService.selectBundles(bundle));
    }

    @PreAuthorize("@ss.hasPermi('garment:bundle:list')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizBundle bundle)
    {
        List<BizBundle> list = cuttingService.selectBundles(bundle);
        ExcelUtil<BizBundle> util = new ExcelUtil<>(BizBundle.class);
        util.exportExcel(response, list, "扎包明细");
    }

    @PreAuthorize("@ss.hasPermi('garment:bundle:list')")
    @GetMapping("/{bundleId}")
    public AjaxResult getInfo(@PathVariable Long bundleId)
    {
        return success(cuttingService.selectBundleById(bundleId));
    }

    @PreAuthorize("@ss.hasPermi('garment:bundle:list')")
    @GetMapping("/code/{code}")
    public AjaxResult getByCode(@PathVariable String code)
    {
        return success(cuttingService.selectBundleByCode(code));
    }

    @PreAuthorize("@ss.hasPermi('garment:bundle:add')")
    @PostMapping("/auto")
    public AjaxResult auto(@RequestBody Map<String, Object> body)
    {
        Long cutId = Long.valueOf(String.valueOf(body.get("cutId")));
        int perBundle = body.get("perBundle") == null ? 17 : Integer.parseInt(String.valueOf(body.get("perBundle")));
        return success("自动生成 " + cuttingService.autoBundle(cutId, perBundle) + " 扎");
    }

    @PreAuthorize("@ss.hasPermi('garment:bundle:add')")
    @PostMapping
    public AjaxResult add(@RequestBody Map<String, Object> body)
    {
        Long cutId = Long.valueOf(String.valueOf(body.get("cutId")));
        String color = String.valueOf(body.get("color"));
        String size = String.valueOf(body.get("size"));
        Integer qty = Integer.valueOf(String.valueOf(body.get("qty")));
        return success(cuttingService.addBundle(cutId, color, size, qty));
    }

    @PreAuthorize("@ss.hasPermi('garment:bundle:edit')")
    @PostMapping("/stockin")
    public AjaxResult stockIn(@RequestBody Map<String, Object> body)
    {
        return success(cuttingService.stockIn(String.valueOf(body.get("code"))));
    }

    @PreAuthorize("@ss.hasPermi('garment:bundle:patch')")
    @PostMapping("/patch")
    public AjaxResult patch(@RequestBody Map<String, Object> body)
    {
        Long bundleId = Long.valueOf(String.valueOf(body.get("bundleId")));
        Integer qty = Integer.valueOf(String.valueOf(body.get("qty")));
        String defectType = String.valueOf(body.get("defectType"));
        String handler = body.get("handler") == null ? "" : String.valueOf(body.get("handler"));
        String remark = body.get("remark") == null ? "" : String.valueOf(body.get("remark"));
        return toAjax(cuttingService.patch(bundleId, qty, defectType, handler, remark));
    }

    @PreAuthorize("@ss.hasPermi('garment:bundle:remove')")
    @DeleteMapping("/{bundleIds}")
    public AjaxResult remove(@PathVariable Long[] bundleIds)
    {
        for (Long bundleId : bundleIds)
        {
            cuttingService.deleteBundle(bundleId);
        }
        return toAjax(bundleIds.length);
    }
}
