package com.ruoyi.web.controller.garment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.garment.BizEmployee;
import com.ruoyi.system.service.garment.BizEmployeeService;

/**
 * 员工档案管理
 */
@RestController
@RequestMapping("/garment/employee")
public class BizEmployeeController extends BaseController
{
    @Autowired
    private BizEmployeeService employeeService;

    @PreAuthorize("@ss.hasPermi('garment:employee:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizEmployee employee)
    {
        startPage();
        return getDataTable(employeeService.selectEmployeeList(employee));
    }

    @PreAuthorize("@ss.hasPermi('garment:employee:list')")
    @GetMapping("/all")
    public AjaxResult all()
    {
        return success(employeeService.selectEmployeeAll());
    }

    @PreAuthorize("@ss.hasPermi('garment:employee:add')")
    @PostMapping
    public AjaxResult add(@RequestBody BizEmployee employee)
    {
        return toAjax(employeeService.saveEmployee(employee));
    }

    @PreAuthorize("@ss.hasPermi('garment:employee:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody BizEmployee employee)
    {
        return toAjax(employeeService.saveEmployee(employee));
    }

    @PreAuthorize("@ss.hasPermi('garment:employee:remove')")
    @DeleteMapping("/{employeeIds}")
    public AjaxResult remove(@PathVariable Long[] employeeIds)
    {
        for (Long employeeId : employeeIds)
        {
            employeeService.deleteEmployee(employeeId);
        }
        return toAjax(employeeIds.length);
    }
}
