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
import com.ruoyi.system.domain.garment.BizSupplier;
import com.ruoyi.system.mapper.garment.BizSupplierMapper;
import com.ruoyi.system.service.garment.PurchaseService;

/**
 * 供应商管理 / 对账
 */
@RestController
@RequestMapping("/garment/supplier")
public class BizSupplierController extends BaseController
{
    @Autowired
    private BizSupplierMapper supplierMapper;

    @PreAuthorize("@ss.hasPermi('garment:supplier:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizSupplier supplier)
    {
        startPage();
        return getDataTable(supplierMapper.selectSupplierList(supplier));
    }

    @GetMapping("/all")
    public AjaxResult all()
    {
        return success(supplierMapper.selectSupplierAll());
    }

    @PreAuthorize("@ss.hasPermi('garment:supplier:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizSupplier supplier)
    {
        List<BizSupplier> list = supplierMapper.selectSupplierList(supplier);
        ExcelUtil<BizSupplier> util = new ExcelUtil<>(BizSupplier.class);
        util.exportExcel(response, list, "供应商");
    }

    @PreAuthorize("@ss.hasPermi('garment:supplier:list')")
    @GetMapping("/reconciliation")
    public TableDataInfo reconciliation()
    {
        startPage();
        return getDataTable(supplierMapper.selectReconciliation());
    }

    @PreAuthorize("@ss.hasPermi('garment:supplier:list')")
    @GetMapping("/{supplierId}")
    public AjaxResult getInfo(@PathVariable Long supplierId)
    {
        return success(supplierMapper.selectSupplierById(supplierId));
    }

    @PreAuthorize("@ss.hasPermi('garment:supplier:add')")
    @PostMapping
    public AjaxResult add(@RequestBody BizSupplier supplier)
    {
        if (supplier.getStatus() == null)
        {
            supplier.setStatus("0");
        }
        return toAjax(supplierMapper.insertSupplier(supplier));
    }

    @PreAuthorize("@ss.hasPermi('garment:supplier:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody BizSupplier supplier)
    {
        return toAjax(supplierMapper.updateSupplier(supplier));
    }

    @PreAuthorize("@ss.hasPermi('garment:supplier:remove')")
    @DeleteMapping("/{supplierIds}")
    public AjaxResult remove(@PathVariable Long[] supplierIds)
    {
        for (Long supplierId : supplierIds)
        {
            supplierMapper.deleteSupplierById(supplierId);
        }
        return toAjax(supplierIds.length);
    }
}
