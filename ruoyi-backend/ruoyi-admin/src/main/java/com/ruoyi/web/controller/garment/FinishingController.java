package com.ruoyi.web.controller.garment;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.garment.BizDefect;
import com.ruoyi.system.domain.garment.BizPack;
import com.ruoyi.system.domain.garment.BizQc;
import com.ruoyi.system.service.garment.FinishingService;
import com.ruoyi.system.service.garment.ReportService;

/**
 * 后道管理：检验、包装、成品入库、次品处理
 */
@RestController
@RequestMapping("/garment/finishing")
public class FinishingController extends BaseController
{
    @Autowired
    private FinishingService finishingService;
    @Autowired
    private ReportService reportService;

    @PreAuthorize("@ss.hasPermi('garment:qc:add')")
    @PostMapping("/qc")
    public AjaxResult qc(@RequestBody BizQc qc)
    {
        return toAjax(finishingService.saveQc(qc));
    }

    @PreAuthorize("@ss.hasPermi('garment:qc:list')")
    @GetMapping("/qc/list")
    public TableDataInfo qcList(BizQc qc)
    {
        startPage();
        return getDataTable(finishingService.selectQcList(qc));
    }

    @PreAuthorize("@ss.hasPermi('garment:defect:add')")
    @PostMapping("/defect")
    public AjaxResult defect(@RequestBody BizDefect defect)
    {
        return toAjax(finishingService.saveDefect(defect));
    }

    @PreAuthorize("@ss.hasPermi('garment:qc:list')")
    @GetMapping("/defect/list")
    public TableDataInfo defectList(BizDefect defect)
    {
        startPage();
        return getDataTable(finishingService.selectDefectList(defect));
    }

    @PreAuthorize("@ss.hasPermi('garment:packing:add')")
    @PostMapping("/pack")
    public AjaxResult pack(@RequestBody BizPack pack)
    {
        return toAjax(finishingService.savePack(pack));
    }

    @PreAuthorize("@ss.hasPermi('garment:packing:list')")
    @GetMapping("/pack/list")
    public TableDataInfo packList(BizPack pack)
    {
        startPage();
        return getDataTable(finishingService.selectPackList(pack));
    }

    /**
     * 成品扫码入库（产线蓝 -> 成品黑）
     */
    @PreAuthorize("@ss.hasPermi('garment:packing:add')")
    @PostMapping("/stockin")
    public AjaxResult stockIn(@RequestBody Map<String, Object> body)
    {
        String code = String.valueOf(body.get("code"));
        Integer qty = Integer.valueOf(String.valueOf(body.get("qty")));
        String method = body.get("packMethod") == null ? "袋装" : String.valueOf(body.get("packMethod"));
        Integer perBag = body.get("perBag") == null ? 1 : Integer.valueOf(String.valueOf(body.get("perBag")));
        String packer = body.get("packer") == null ? "" : String.valueOf(body.get("packer"));
        String packCode = body.get("packCode") == null ? "" : String.valueOf(body.get("packCode"));
        return success(finishingService.stockIn(code, qty, method, perBag, packer, packCode));
    }

    @PreAuthorize("@ss.hasPermi('garment:finishingstat:list')")
    @GetMapping("/stat")
    public AjaxResult stat(@RequestParam(required = false) String date)
    {
        return success(finishingService.statByPacker(date == null ? java.time.LocalDate.now().toString() : date));
    }

    @PreAuthorize("@ss.hasPermi('garment:finishingstat:list')")
    @GetMapping("/daily")
    public AjaxResult daily(@RequestParam(required = false) String beginDate, @RequestParam(required = false) String endDate)
    {
        return success(reportService.cutByDay(beginDate, endDate));
    }
}
