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
import com.ruoyi.system.domain.garment.BizShipment;
import com.ruoyi.system.service.garment.WarehouseService;

/**
 * 出货表
 */
@RestController
@RequestMapping("/garment/shipment")
public class BizShipmentController extends BaseController
{
    @Autowired
    private WarehouseService warehouseService;

    @PreAuthorize("@ss.hasPermi('garment:shipment:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizShipment query)
    {
        startPage();
        return getDataTable(warehouseService.selectShipmentList(query));
    }

    @PreAuthorize("@ss.hasPermi('garment:shipment:list')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizShipment query)
    {
        List<BizShipment> list = warehouseService.selectShipmentList(query);
        ExcelUtil<BizShipment> util = new ExcelUtil<>(BizShipment.class);
        util.exportExcel(response, list, "出货表");
    }

    @PreAuthorize("@ss.hasPermi('garment:shipment:list')")
    @GetMapping("/summary")
    public AjaxResult summary(@RequestParam(required = false) String beginDate, @RequestParam(required = false) String endDate)
    {
        return success(warehouseService.shipmentSummary(beginDate, endDate));
    }

    @PreAuthorize("@ss.hasPermi('garment:shipment:list')")
    @Log(title = "出货表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizShipment shipment)
    {
        return toAjax(warehouseService.saveShipment(shipment));
    }

    @PreAuthorize("@ss.hasPermi('garment:shipment:list')")
    @Log(title = "出货表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizShipment shipment)
    {
        return toAjax(warehouseService.saveShipment(shipment));
    }

    @PreAuthorize("@ss.hasPermi('garment:shipment:list')")
    @Log(title = "出货表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{shipIds}")
    public AjaxResult remove(@PathVariable Long[] shipIds)
    {
        for (Long shipId : shipIds)
        {
            warehouseService.deleteShipment(shipId);
        }
        return toAjax(shipIds.length);
    }
}
