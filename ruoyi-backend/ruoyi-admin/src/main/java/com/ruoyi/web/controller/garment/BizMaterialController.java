package com.ruoyi.web.controller.garment;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.garment.BizMaterial;
import com.ruoyi.system.domain.garment.BizMaterialLog;
import com.ruoyi.system.service.garment.MaterialService;

/**
 * 物资库存（面辅料/耗材）：采购入库自动增加、裁货自动领料扣减
 */
@RestController
@RequestMapping("/garment/material")
public class BizMaterialController extends BaseController
{
    @Autowired
    private MaterialService materialService;

    @PreAuthorize("@ss.hasPermi('garment:material:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizMaterial query)
    {
        startPage();
        return getDataTable(materialService.selectList(query));
    }

    @PreAuthorize("@ss.hasPermi('garment:material:list')")
    @GetMapping("/logs")
    public TableDataInfo logs(BizMaterialLog query)
    {
        startPage();
        return getDataTable(materialService.selectLogs(query));
    }

    @PreAuthorize("@ss.hasPermi('garment:material:list')")
    @PutMapping("/minstock")
    public AjaxResult minStock(@RequestBody Map<String, Object> body)
    {
        Long materialId = body.get("materialId") == null ? null : Long.valueOf(String.valueOf(body.get("materialId")));
        BigDecimal minStock = body.get("minStock") == null ? BigDecimal.ZERO : new BigDecimal(String.valueOf(body.get("minStock")));
        return toAjax(materialService.updateMinStock(materialId, minStock));
    }
}
