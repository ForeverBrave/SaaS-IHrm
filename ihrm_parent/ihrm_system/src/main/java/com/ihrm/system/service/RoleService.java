package com.ihrm.system.service;

import com.ihrm.common.service.BaseService;
import com.ihrm.common.utils.IdWorker;
import com.ihrm.common.utils.PermissionConstants;
import com.ihrm.domain.system.Permission;
import com.ihrm.domain.system.Role;
import com.ihrm.system.dao.PermissionDao;
import com.ihrm.system.dao.RoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/3/2 15:02
 */
@Service
public class RoleService extends BaseService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PermissionDao permissionDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 新增角色
     * @param role
     */
    public void saveRole(Role role){
        String id = idWorker.nextId() + "";
        role.setId(id);
        roleDao.save(role);
    }

    /**
     * 更新角色
     * @param role
     */
    public void updateRole(Role role){
        //根据id查询角色
        Role result = roleDao.getOne(role.getId());
        //设置角色属性
        result.setDescription(role.getDescription());
        result.setName(role.getName());
        //更新角色
        roleDao.save(result);
    }

    /**
     * 根据id删除角色
     * @param id
     */
    public void deleteRoleById(String id){
        roleDao.deleteById(id);
    }

    /**
     * 根据id查询角色
     * @param id
     * @return
     */
    public Role findRoleById(String id){
        return roleDao.findById(id).get();
    }

    /**
     * 分页查询所有角色
     * @param companyId
     * @param page
     * @param pagesize
     *
     * @return
     */
    public Page<Role> findAllRolesByPage(String companyId,Integer page,Integer pagesize){
        /**
         * 角色构造查询条件（这里使用了lambda表达式）
         * 参数说明
         *      root：包含了所有的对象数据
         *      cq：一般不用
         *      cb：构造查询条件
         */
        //分页
        return roleDao.findAll(getSpec(companyId),PageRequest.of(page-1, pagesize));
    }

    /**
     * 查询所有角色
     * @param companyId
     * @return
     */
    public List<Role> findAll(String companyId) {
        return roleDao.findAll(getSpec(companyId));
    }

    /**
     * 分配权限
     * @param roleId
     * @param permIds
     */
    public void assignPerms(String roleId,List<String> permIds) {
        //1.获取分配的角色对象
        Role role = roleDao.findById(roleId).get();
        //2.构造角色的权限集合
        Set<Permission> perms = new HashSet<>();
        for (String permId : permIds) {
            Permission permission = permissionDao.findById(permId).get();
            //需要根据父id和类型查询API权限列表
            List<Permission> apiList = permissionDao.findByTypeAndPid(PermissionConstants.PY_API, permission.getId());
            perms.addAll(apiList);//自定赋予API权限
            perms.add(permission);//当前菜单或按钮的权限
        }
        //3.设置角色和权限的关系
        role.setPermissions(perms);
        //4.更新角色
        roleDao.save(role);
    }
}
