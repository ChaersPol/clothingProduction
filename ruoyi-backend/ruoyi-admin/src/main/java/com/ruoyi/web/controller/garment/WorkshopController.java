package com.ruoyi.web.controller.garment;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.garment.BizBundle;
import com.ruoyi.system.domain.garment.BizDelivery;
import com.ruoyi.system.domain.garment.BizPick;
import com.ruoyi.system.service.garment.ReportService;
import com.ruoyi.system.service.garment.WorkshopService;

/**
 * 车间管理：扫码领片、产线库存、完工交后道
 */
@RestController
@RequestMapping("/garment/workshop")
public class WorkshopController extends BaseController
{
    @Autowired
    private WorkshopService workshopService;
    @Autowired
    private ReportService reportService;

    @PreAuthorize("@ss.hasPermi('garment:pick:list')")
    @PostMapping("/pick")
    public AjaxResult pick(@RequestBody Map<String, Object> body)
    {
        String code = String.valueOf(body.get("code"));
        String employee = String.valueOf(body.get("employee"));
        return success(workshopService.pick(code, employee));
    }

    @PreAuthorize("@ss.hasPermi('garment:pick:list')")
    @GetMapping("/picks")
    public TableDataInfo picks(BizPick pick)
    {
        startPage();
        return getDataTable(workshopService.selectPickList(pick));
    }

    @PreAuthorize("@ss.hasPermi('garment:linestock:list')")
    @GetMapping("/linestock")
    public TableDataInfo lineStock(BizBundle bundle)
    {
        if (bundle.getParams() == null)
        {
            bundle.setParams(new java.util.HashMap<>());
        }
        bundle.getParams().put("online", true);
        startPage();
        return getDataTable(workshopService.selectBundles(bundle));
    }

    @PreAuthorize("@ss.hasPermi('garment:delivery:list')")
    @PostMapping("/deliver")
    public AjaxResult deliver(@RequestBody Map<String, Object> body)
    {
        Long bundleId = Long.valueOf(String.valueOf(body.get("bundleId")));
        Integer qty = Integer.valueOf(String.valueOf(body.get("qty")));
        Integer defect = body.get("defectQty") == null ? 0 : Integer.valueOf(String.valueOf(body.get("defectQty")));
        String employee = body.get("employee") == null ? "" : String.valueOf(body.get("employee"));
        return success(workshopService.deliver(bundleId, qty, defect, employee));
    }

    @PreAuthorize("@ss.hasPermi('garment:delivery:list')")
    @GetMapping("/deliveries")
    public TableDataInfo deliveries(BizDelivery delivery)
    {
        startPage();
        return getDataTable(workshopService.selectDeliveryList(delivery));
    }

    @PreAuthorize("@ss.hasPermi('garment:pick:list')")
    @GetMapping("/pickstat")
    public AjaxResult pickStat(@RequestParam(required = false) String date)
    {
        return success(reportService.pickByEmployee(date == null ? java.time.LocalDate.now().toString() : date));
    }

    @PreAuthorize("@ss.hasPermi('garment:delivery:list')")
    @GetMapping("/deliverystat")
    public AjaxResult deliveryStat(@RequestParam(required = false) String date)
    {
        return success(reportService.deliveryByEmployee(date == null ? java.time.LocalDate.now().toString() : date));
    }
}
