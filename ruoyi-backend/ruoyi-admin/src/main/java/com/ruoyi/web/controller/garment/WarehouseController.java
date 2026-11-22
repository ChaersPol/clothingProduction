package com.ruoyi.web.controller.garment;

import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.garment.*;
import com.ruoyi.system.service.garment.WarehouseService;

/**
 * 仓库管理：实时库存、流水、预警、盘点
 */
@RestController
@RequestMapping("/garment")
public class WarehouseController extends BaseController
{
    @Autowired
    private WarehouseService warehouseService;

    @PreAuthorize("@ss.hasPermi('garment:inventory:list')")
    @GetMapping("/inventory/list")
    public TableDataInfo inventory(BizInventory query)
    {
        startPage();
        return getDataTable(warehouseService.selectInventory(query));
    }

    @PreAuthorize("@ss.hasPermi('garment:inventory:export')")
    @PostMapping("/inventory/export")
    public void exportInventory(HttpServletResponse response, BizInventory query)
    {
        List<BizInventory> list = warehouseService.selectInventory(query);
        ExcelUtil<BizInventory> util = new ExcelUtil<>(BizInventory.class);
        util.exportExcel(response, list, "库存明细");
    }

    @PreAuthorize("@ss.hasPermi('garment:stocklog:list')")
    @GetMapping("/stocklog/list")
    public TableDataInfo stockLog(BizStockLog log)
    {
        startPage();
        return getDataTable(warehouseService.selectLogs(log));
    }

    @PreAuthorize("@ss.hasPermi('garment:stocklog:export')")
    @PostMapping("/stocklog/export")
    public void exportLog(HttpServletResponse response, BizStockLog log)
    {
        List<BizStockLog> list = warehouseService.selectLogs(log);
        ExcelUtil<BizStockLog> util = new ExcelUtil<>(BizStockLog.class);
        util.exportExcel(response, list, "库存流水");
    }

    @PreAuthorize("@ss.hasPermi('garment:warning:list')")
    @GetMapping("/warning/list")
    public TableDataInfo warning(@RequestParam(required = false) String styleNo)
    {
        startPage();
        return getDataTable(warehouseService.selectWarnings(styleNo));
    }

    @PreAuthorize("@ss.hasPermi('garment:warning:list')")
    @GetMapping("/warning/suggestion")
    public AjaxResult suggestion(@RequestParam(required = false) String styleNo)
    {
        return success(warehouseService.cutSuggestion(styleNo));
    }

    @PreAuthorize("@ss.hasPermi('garment:warning:edit')")
    @PutMapping("/warning/minstock")
    public AjaxResult minStock(@RequestBody Map<String, Object> body)
    {
        Long skuId = Long.valueOf(String.valueOf(body.get("skuId")));
        Integer minStock = Integer.valueOf(String.valueOf(body.get("minStock")));
        return toAjax(warehouseService.updateMinStock(skuId, minStock));
    }

    /* ---------------- 盘点 ---------------- */
    @PreAuthorize("@ss.hasPermi('garment:check:list')")
    @GetMapping("/check/list")
    public TableDataInfo checkList(BizCheck check)
    {
        startPage();
        return getDataTable(warehouseService.selectCheckList(check));
    }

    @PreAuthorize("@ss.hasPermi('garment:check:list')")
    @GetMapping("/check/{checkId}")
    public AjaxResult checkInfo(@PathVariable Long checkId)
    {
        return success(warehouseService.selectCheckById(checkId));
    }

    @PreAuthorize("@ss.hasPermi('garment:check:add')")
    @PostMapping("/check")
    public AjaxResult addCheck(@RequestBody BizCheck check, @RequestParam(required = false) String stockType)
    {
        return success(warehouseService.createCheck(check, stockType));
    }

    @PreAuthorize("@ss.hasPermi('garment:check:edit')")
    @PutMapping("/check/items")
    public AjaxResult saveCheckItems(@RequestBody Map<String, Object> body)
    {
        Long checkId = Long.valueOf(String.valueOf(body.get("checkId")));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> raw = (List<Map<String, Object>>) body.get("items");
        List<BizCheckItem> items = new java.util.ArrayList<>();
        if (raw != null)
        {
            for (Map<String, Object> row : raw)
            {
                BizCheckItem item = new BizCheckItem();
                item.setItemId(Long.valueOf(String.valueOf(row.get("itemId"))));
                item.setBookQty(Integer.valueOf(String.valueOf(row.get("bookQty"))));
                item.setActualQty(Integer.valueOf(String.valueOf(row.get("actualQty"))));
                items.add(item);
            }
        }
        return toAjax(warehouseService.saveCheckItems(checkId, items));
    }

    @PreAuthorize("@ss.hasPermi('garment:check:edit')")
    @PostMapping("/check/{checkId}/complete")
    public AjaxResult completeCheck(@PathVariable Long checkId)
    {
        return toAjax(warehouseService.completeCheck(checkId));
    }

    @PreAuthorize("@ss.hasPermi('garment:check:remove')")
    @DeleteMapping("/check/{checkIds}")
    public AjaxResult deleteCheck(@PathVariable Long[] checkIds)
    {
        for (Long checkId : checkIds)
        {
            warehouseService.deleteCheck(checkId);
        }
        return toAjax(checkIds.length);
    }
}
