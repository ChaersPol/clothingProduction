package com.ruoyi.web.controller.garment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.garment.*;
import com.ruoyi.system.mapper.garment.BizBundleMapper;
import com.ruoyi.system.mapper.garment.BizMaterialMapper;
import com.ruoyi.system.mapper.garment.BizSkuMapper;
import com.ruoyi.system.mapper.garment.BizStyleMapper;
import com.ruoyi.system.service.garment.GoodsService;
import com.ruoyi.system.service.garment.BizEmployeeService;

/**
 * 基础资料（颜色/尺码）及通用下拉数据
 */
@RestController
@RequestMapping("/garment")
public class BizBasicsController extends BaseController
{
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private BizStyleMapper styleMapper;
    @Autowired
    private BizSkuMapper skuMapper;
    @Autowired
    private BizBundleMapper bundleMapper;
    @Autowired
    private BizEmployeeService employeeService;
    @Autowired
    private BizMaterialMapper materialMapper;
    @Autowired
    private com.ruoyi.system.mapper.garment.BizCustomerMapper customerMapper;
    @Autowired
    private com.ruoyi.system.mapper.garment.GarmentUserMapper garmentUserMapper;

    @PreAuthorize("@ss.hasPermi('garment:basics:list')")
    @GetMapping("/color/list")
    public TableDataInfo colorList(BizColor color)
    {
        startPage();
        return getDataTable(goodsService.selectColorList(color));
    }

    @PreAuthorize("@ss.hasPermi('garment:basics:add')")
    @PostMapping("/color")
    public AjaxResult addColor(@RequestBody BizColor color)
    {
        return toAjax(goodsService.saveColor(color));
    }

    @PreAuthorize("@ss.hasPermi('garment:basics:edit')")
    @PutMapping("/color")
    public AjaxResult editColor(@RequestBody BizColor color)
    {
        return toAjax(goodsService.saveColor(color));
    }

    @PreAuthorize("@ss.hasPermi('garment:basics:remove')")
    @DeleteMapping("/color/{colorIds}")
    public AjaxResult delColor(@PathVariable Long[] colorIds)
    {
        for (Long colorId : colorIds)
        {
            goodsService.deleteColor(colorId);
        }
        return toAjax(colorIds.length);
    }

    @PreAuthorize("@ss.hasPermi('garment:basics:list')")
    @GetMapping("/size/list")
    public TableDataInfo sizeList(BizSize size)
    {
        startPage();
        return getDataTable(goodsService.selectSizeList(size));
    }

    @PreAuthorize("@ss.hasPermi('garment:basics:add')")
    @PostMapping("/size")
    public AjaxResult addSize(@RequestBody BizSize size)
    {
        return toAjax(goodsService.saveSize(size));
    }

    @PreAuthorize("@ss.hasPermi('garment:basics:edit')")
    @PutMapping("/size")
    public AjaxResult editSize(@RequestBody BizSize size)
    {
        return toAjax(goodsService.saveSize(size));
    }

    @PreAuthorize("@ss.hasPermi('garment:basics:remove')")
    @DeleteMapping("/size/{sizeIds}")
    public AjaxResult delSize(@PathVariable Long[] sizeIds)
    {
        for (Long sizeId : sizeIds)
        {
            goodsService.deleteSize(sizeId);
        }
        return toAjax(sizeIds.length);
    }

    /* ---------------- 通用下拉，所有登录用户可用 ---------------- */
    @GetMapping("/common/styles")
    public AjaxResult styles()
    {
        return success(styleMapper.selectStyleAll());
    }

    @GetMapping("/common/colors")
    public AjaxResult colors()
    {
        return success(goodsService.selectColorList(new BizColor()));
    }

    @GetMapping("/common/sizes")
    public AjaxResult sizes()
    {
        return success(goodsService.selectSizeList(new BizSize()));
    }

    @GetMapping("/common/skus")
    public AjaxResult skus(@RequestParam(required = false) String styleNo, @RequestParam(required = false) String barcode)
    {
        if (barcode != null && !barcode.isEmpty())
        {
            BizSku sku = skuMapper.selectSkuByBarcode(barcode);
            List<BizSku> list = new ArrayList<>();
            if (sku != null)
            {
                list.add(sku);
            }
            return success(list);
        }
        if (styleNo == null || styleNo.isEmpty())
        {
            return success(new ArrayList<>());
        }
        return success(skuMapper.selectSkuByStyleNo(styleNo));
    }

    @GetMapping("/common/sku")
    public AjaxResult skuByBarcode(@RequestParam String barcode)
    {
        return success(skuMapper.selectSkuByBarcode(barcode));
    }

    @GetMapping("/common/bundle")
    public AjaxResult bundleByCode(@RequestParam String code)
    {
        return success(bundleMapper.selectByCode(code));
    }

    @GetMapping("/common/employees")
    public AjaxResult employees(@RequestParam(required = false) String position)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        List<BizEmployee> employees = StringUtils.isEmpty(position)
                ? employeeService.selectEmployeeAll()
                : employeeService.selectEmployeeByPosition(position);
        for (BizEmployee e : employees)
        {
            Map<String, Object> item = new HashMap<>();
            item.put("employeeId", e.getEmployeeId());
            item.put("name", e.getName());
            item.put("position", e.getPosition());
            list.add(item);
        }
        return success(list);
    }

    /** 物资档案下拉：BOM、采购选物料用，单位/规格以档案为准 */
    @GetMapping("/common/materials")
    public AjaxResult materials()
    {
        return success(materialMapper.selectMaterialList(new BizMaterial()));
    }

    /** 客户档案下拉：裁货、出货、出库、退货统一从客户档案选择 */
    @GetMapping("/common/customers")
    public AjaxResult customers()
    {
        return success(customerMapper.selectCustomerAll());
    }

    /** 人员下拉：以系统用户为唯一人员来源，岗位取岗位管理（如 设计、裁床、车间、后道） */
    @GetMapping("/common/users")
    public AjaxResult users(@RequestParam(required = false) String post)
    {
        return success(garmentUserMapper.selectActiveUsers(post));
    }
}
