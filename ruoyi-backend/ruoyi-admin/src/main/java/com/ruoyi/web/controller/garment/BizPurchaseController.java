package com.ruoyi.web.controller.garment;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.ruoyi.system.domain.garment.BizPurchase;
import com.ruoyi.system.domain.garment.BizPurchaseItemExport;
import com.ruoyi.system.service.garment.PurchaseService;

/**
 * 采购管理
 */
@RestController
@RequestMapping("/garment/purchase")
public class BizPurchaseController extends BaseController
{
    @Autowired
    private PurchaseService purchaseService;

    @PreAuthorize("@ss.hasPermi('garment:purchase:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizPurchase purchase)
    {
        startPage();
        return getDataTable(purchaseService.selectList(purchase));
    }

    @PreAuthorize("@ss.hasPermi('garment:purchase:list')")
    @GetMapping("/items")
    public TableDataInfo items(BizPurchase purchase)
    {
        startPage();
        return getDataTable(purchaseService.selectItemList(purchase));
    }

    @PreAuthorize("@ss.hasPermi('garment:purchase:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizPurchase purchase)
    {
        List<BizPurchase> list = purchaseService.selectList(purchase);
        ExcelUtil<BizPurchase> util = new ExcelUtil<>(BizPurchase.class);
        util.exportExcel(response, list, "采购单");
    }

    @PreAuthorize("@ss.hasPermi('garment:purchase:export')")
    @PostMapping("/items/export")
    public void exportItems(HttpServletResponse response, BizPurchase purchase)
    {
        List<Map<String, Object>> rows = purchaseService.selectItemList(purchase);
        List<BizPurchaseItemExport> list = new ArrayList<>();
        for (Map<String, Object> r : rows)
        {
            BizPurchaseItemExport e = new BizPurchaseItemExport();
            e.setPurchaseNo(str(r.get("purchase_no")));
            e.setPurchaseType(str(r.get("purchase_type")));
            e.setSupplierName(str(r.get("supplier_name")));
            e.setMaterialType(str(r.get("material_type")));
            e.setMaterialName(str(r.get("material_name")));
            e.setSpec(str(r.get("spec")));
            e.setUnit(str(r.get("unit")));
            e.setPrice(dec(r.get("price")));
            e.setQty(dec(r.get("qty")));
            e.setReceivedQty(dec(r.get("received_qty")));
            e.setAmount(dec(r.get("amount")));
            list.add(e);
        }
        ExcelUtil<BizPurchaseItemExport> util = new ExcelUtil<>(BizPurchaseItemExport.class);
        util.exportExcel(response, list, "采购明细");
    }

    private static String str(Object v)
    {
        return v == null ? null : v.toString();
    }

    private static BigDecimal dec(Object v)
    {
        return v == null ? null : new BigDecimal(v.toString());
    }

    @PreAuthorize("@ss.hasPermi('garment:purchase:list')")
    @GetMapping("/{purchaseId}")
    public AjaxResult getInfo(@PathVariable Long purchaseId)
    {
        return success(purchaseService.selectById(purchaseId));
    }

    @PreAuthorize("@ss.hasPermi('garment:purchase:add')")
    @Log(title = "采购登记", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizPurchase purchase)
    {
        return toAjax(purchaseService.save(purchase));
    }

    @PreAuthorize("@ss.hasPermi('garment:purchase:edit')")
    @Log(title = "采购登记", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizPurchase purchase)
    {
        return toAjax(purchaseService.save(purchase));
    }

    @PreAuthorize("@ss.hasPermi('garment:purchase:receive')")
    @PostMapping("/receive")
    public AjaxResult receive(@RequestBody Map<String, Object> body)
    {
        Long itemId = Long.valueOf(String.valueOf(body.get("itemId")));
        Long purchaseId = Long.valueOf(String.valueOf(body.get("purchaseId")));
        BigDecimal qty = new BigDecimal(String.valueOf(body.get("qty")));
        return toAjax(purchaseService.receive(itemId, qty, purchaseId));
    }

    @PreAuthorize("@ss.hasPermi('garment:purchase:remove')")
    @Log(title = "采购登记", businessType = BusinessType.DELETE)
    @DeleteMapping("/{purchaseIds}")
    public AjaxResult remove(@PathVariable Long[] purchaseIds)
    {
        for (Long purchaseId : purchaseIds)
        {
            purchaseService.delete(purchaseId);
        }
        return toAjax(purchaseIds.length);
    }
}
