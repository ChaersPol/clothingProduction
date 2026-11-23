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
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.garment.BizCustomer;
import com.ruoyi.system.mapper.garment.BizCustomerMapper;

/**
 * 客户档案
 */
@RestController
@RequestMapping("/garment/customer")
public class BizCustomerController extends BaseController
{
    @Autowired
    private BizCustomerMapper customerMapper;

    @PreAuthorize("@ss.hasPermi('garment:customer:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizCustomer customer)
    {
        startPage();
        return getDataTable(customerMapper.selectCustomerList(customer));
    }

    @PreAuthorize("@ss.hasPermi('garment:customer:export')")
    @Log(title = "客户档案", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizCustomer customer)
    {
        List<BizCustomer> list = customerMapper.selectCustomerList(customer);
        ExcelUtil<BizCustomer> util = new ExcelUtil<>(BizCustomer.class);
        util.exportExcel(response, list, "客户档案");
    }

    @PreAuthorize("@ss.hasPermi('garment:customer:list')")
    @GetMapping("/{customerId}")
    public AjaxResult getInfo(@PathVariable Long customerId)
    {
        return success(customerMapper.selectCustomerById(customerId));
    }

    @PreAuthorize("@ss.hasPermi('garment:customer:add')")
    @Log(title = "客户档案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizCustomer customer)
    {
        String name = customer.getCustomerName() == null ? "" : customer.getCustomerName().trim();
        if (StringUtils.isEmpty(name))
        {
            throw new ServiceException("客户名称不能为空");
        }
        customer.setCustomerName(name);
        if (customerMapper.countByName(name, 0L) > 0)
        {
            throw new ServiceException("客户「" + name + "」已存在");
        }
        if (customer.getStatus() == null)
        {
            customer.setStatus("0");
        }
        customer.setCreateBy(SecurityUtils.getUsername());
        return toAjax(customerMapper.insertCustomer(customer));
    }

    @PreAuthorize("@ss.hasPermi('garment:customer:edit')")
    @Log(title = "客户档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizCustomer customer)
    {
        String name = customer.getCustomerName() == null ? "" : customer.getCustomerName().trim();
        if (StringUtils.isEmpty(name))
        {
            throw new ServiceException("客户名称不能为空");
        }
        customer.setCustomerName(name);
        if (customerMapper.countByName(name, customer.getCustomerId()) > 0)
        {
            throw new ServiceException("客户「" + name + "」已存在");
        }
        customer.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(customerMapper.updateCustomer(customer));
    }

    @PreAuthorize("@ss.hasPermi('garment:customer:remove')")
    @Log(title = "客户档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{customerIds}")
    public AjaxResult remove(@PathVariable Long[] customerIds)
    {
        for (Long customerId : customerIds)
        {
            BizCustomer customer = customerMapper.selectCustomerById(customerId);
            if (customer == null)
            {
                continue;
            }
            if (customerMapper.countRefs(customer.getCustomerName()) > 0)
            {
                throw new ServiceException("客户「" + customer.getCustomerName() + "」已有业务单据，不能删除");
            }
            customerMapper.deleteCustomerById(customerId);
        }
        return toAjax(customerIds.length);
    }
}
