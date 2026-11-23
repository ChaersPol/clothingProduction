package com.ruoyi.system.mapper.garment;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 人员（系统用户）查询，人员主数据统一取 sys_user + 岗位管理 sys_post
 */
@Mapper
public interface GarmentUserMapper
{
    /**
     * 按岗位前缀查询在用用户（如 post=设计 查设计师、设计部-主设计师）
     */
    @Select("""
        <script>
        select u.user_id as userId, u.user_name as userName, u.nick_name as nickName,
               substring_index(u.nick_name,'-',-1) as displayName,
               group_concat(p.post_name order by p.post_id separator ',') as postNames
        from sys_user u
        left join sys_user_post up on up.user_id = u.user_id
        left join sys_post p on p.post_id = up.post_id and p.status = '0'
        where u.status = '0' and u.del_flag = '0'
        <if test="post != null and post != ''"> and p.post_name like concat(#{post},'%')</if>
        group by u.user_id, u.user_name, u.nick_name
        order by u.user_id
        </script>
        """)
    List<Map<String, Object>> selectActiveUsers(@Param("post") String post);
}
