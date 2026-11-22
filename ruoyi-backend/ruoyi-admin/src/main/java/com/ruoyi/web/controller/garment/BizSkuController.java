package com.ruoyi.web.controller.garment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.garment.BizInitialStock;
import com.ruoyi.system.domain.garment.BizSku;
import com.ruoyi.system.service.garment.GoodsService;

/**
 * 商品SKU / 条码 / 标签
 */
@RestController
@RequestMapping("/garment/sku")
public class BizSkuController extends BaseController
{
    @Autowired
    private GoodsService goodsService;

    @PreAuthorize("@ss.hasPermi('garment:sku:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizSku sku)
    {
        startPage();
        return getDataTable(goodsService.selectSkuList(sku));
    }

    @PreAuthorize("@ss.hasPermi('garment:label:list')")
    @GetMapping("/label/list")
    public TableDataInfo labelList(BizSku sku)
    {
        startPage();
        return getDataTable(goodsService.selectSkuList(sku));
    }

    @PreAuthorize("@ss.hasPermi('garment:sku:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizSku sku)
    {
        List<BizSku> list = goodsService.selectSkuList(sku);
        ExcelUtil<BizSku> util = new ExcelUtil<>(BizSku.class);
        util.exportExcel(response, list, "商品SKU");
    }

    @PreAuthorize("@ss.hasPermi('garment:sku:list')")
    @GetMapping("/{skuId}")
    public AjaxResult getInfo(@PathVariable Long skuId)
    {
        return success(goodsService.selectSkuById(skuId));
    }

    @PreAuthorize("@ss.hasPermi('garment:sku:add')")
    @Log(title = "商品SKU", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizSku sku)
    {
        return toAjax(goodsService.saveSku(sku));
    }

    @PreAuthorize("@ss.hasPermi('garment:sku:edit')")
    @Log(title = "商品SKU", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizSku sku)
    {
        return toAjax(goodsService.saveSku(sku));
    }

    @PreAuthorize("@ss.hasPermi('garment:sku:remove')")
    @Log(title = "商品SKU", businessType = BusinessType.DELETE)
    @DeleteMapping("/{skuIds}")
    public AjaxResult remove(@PathVariable Long[] skuIds)
    {
        int count = 0;
        for (Long id : skuIds)
        {
            count += goodsService.deleteSku(id);
        }
        return toAjax(count);
    }

    /**
     * 按颜色×尺码批量生成商品
     */
    @PreAuthorize("@ss.hasPermi('garment:sku:add')")
    @Log(title = "批量生成SKU", businessType = BusinessType.INSERT)
    @PostMapping("/generate")
    @SuppressWarnings("unchecked")
    public AjaxResult generate(@RequestBody Map<String, Object> body)
    {
        String styleNo = (String) body.get("styleNo");
        List<String> colors = (List<String>) body.get("colors");
        List<String> sizes = (List<String>) body.get("sizes");
        int count = goodsService.generateSkus(styleNo, colors, sizes);
        return success("本次生成 " + count + " 个商品");
    }

    /**
     * 初始库存 Excel 导入
     */
    @PreAuthorize("@ss.hasPermi('garment:sku:edit')")
    @Log(title = "初始库存导入", businessType = BusinessType.IMPORT)
    @PostMapping("/importStock")
    public AjaxResult importStock(@RequestParam("file") MultipartFile file) throws Exception
    {
        ExcelUtil<BizInitialStock> util = new ExcelUtil<>(BizInitialStock.class);
        List<BizInitialStock> list = util.importExcel(file.getInputStream());
        if (list == null || list.isEmpty())
        {
            throw new ServiceException("Excel 中没有数据");
        }
        int count = goodsService.importInitialStock(list);
        return success("成功导入 " + count + " 条初始库存");
    }

    @GetMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<BizInitialStock> util = new ExcelUtil<>(BizInitialStock.class);
        util.importTemplateExcel(response, "初始库存导入模板");
    }
}
