package com.ruoyi.system.service.garment;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.garment.BizEmployee;
import com.ruoyi.system.mapper.garment.BizEmployeeMapper;

/**
 * 员工档案服务
 */
@Service
public class BizEmployeeService
{
    @Autowired
    private BizEmployeeMapper employeeMapper;

    public List<BizEmployee> selectEmployeeList(BizEmployee employee)
    {
        return employeeMapper.selectEmployeeList(employee);
    }

    public List<BizEmployee> selectEmployeeAll()
    {
        return employeeMapper.selectEmployeeAll();
    }

    public List<BizEmployee> selectEmployeeByPosition(String position)
    {
        return employeeMapper.selectEmployeeByPosition(position);
    }

    public int saveEmployee(BizEmployee employee)
    {
        if (StringUtils.isEmpty(employee.getStatus()))
        {
            employee.setStatus("0");
        }
        if (employee.getSort() == null)
        {
            employee.setSort(0);
        }
        if (employee.getEmployeeId() == null)
        {
            employee.setCreateBy(SecurityUtils.getUsername());
            return employeeMapper.insertEmployee(employee);
        }
        employee.setUpdateBy(SecurityUtils.getUsername());
        return employeeMapper.updateEmployee(employee);
    }

    public int deleteEmployee(Long id)
    {
        return employeeMapper.deleteEmployeeById(id);
    }
}
