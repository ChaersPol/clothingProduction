package com.ruoyi.system.service.garment;

import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 车间数据范围控制：
 * 管理员 / 系统管理员可查看全部数据，其他账号只能查看本人数据。
 */
public final class GarmentScopeUtils
{
    private GarmentScopeUtils() {}

    /**
     * 当前登录人是否可查看全部车间数据
     */
    public static boolean isManager()
    {
        try
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser == null || loginUser.getUser() == null)
            {
                return false;
            }
            SysUser user = loginUser.getUser();
            if (user.isAdmin())
            {
                return true;
            }
            if (user.getRoles() != null)
            {
                for (SysRole role : user.getRoles())
                {
                    if ("admin".equals(role.getRoleKey()) || "sysadmin".equals(role.getRoleKey()))
                    {
                        return true;
                    }
                }
            }
            return false;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * 当前登录人对应的员工姓名（去掉部门前缀，如「车间-李梅」返回「李梅」）
     */
    public static String currentEmployee()
    {
        try
        {
            SysUser user = SecurityUtils.getLoginUser().getUser();
            String nick = user.getNickName();
            if (nick == null || nick.trim().isEmpty())
            {
                return user.getUserName();
            }
            int idx = nick.lastIndexOf('-');
            if (idx < 0)
            {
                idx = nick.lastIndexOf('－');
            }
            return idx >= 0 && idx < nick.length() - 1 ? nick.substring(idx + 1) : nick;
        }
        catch (Exception e)
        {
            return "";
        }
    }
}
