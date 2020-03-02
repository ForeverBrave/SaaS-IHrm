package com.ihrm.system.service;

import com.ihrm.common.utils.IdWorker;
import com.ihrm.domain.system.Role;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/3/2 15:02
 */
@Service
public class RoleService {
    @Autowired
    private RoleDao roleDao;
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
     * @param size
     *
     * @return
     */
    public Page<Role> findAllRolesByPage(String companyId,int page,int size){
        /**
         * 角色构造查询条件（这里使用了lambda表达式）
         * 参数说明
         *      root：包含了所有的对象数据
         *      cq：一般不用
         *      cb：构造查询条件
         */
        Specification<Role> spec = (Specification<Role>) (root, cq, cb) ->
                cb.equal(root.get("companyId").as(String.class),companyId);

        //分页
        return roleDao.findAll(spec,PageRequest.of(page-1, size));
    }
}
