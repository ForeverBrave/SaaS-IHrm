package com.ihrm.system.service;

import com.ihrm.common.entity.ResultCode;
import com.ihrm.common.exception.CommonException;
import com.ihrm.common.utils.BeanMapUtils;
import com.ihrm.common.utils.IdWorker;
import com.ihrm.common.utils.PermissionConstants;
import com.ihrm.domain.system.Permission;
import com.ihrm.domain.system.PermissionApi;
import com.ihrm.domain.system.PermissionMenu;
import com.ihrm.domain.system.PermissionPoint;
import com.ihrm.system.dao.PermissionApiDao;
import com.ihrm.system.dao.PermissionDao;
import com.ihrm.system.dao.PermissionMenuDao;
import com.ihrm.system.dao.PermissionPointDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/3/2 15:02
 */
@Service
@Transactional
public class PermissionService {

    @Autowired
    private PermissionDao permissionDao;
    @Autowired
    private PermissionMenuDao permissionMenuDao;
    @Autowired
    private PermissionPointDao permissionPointDao;
    @Autowired
    private PermissionApiDao permissionApiDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 新增权限
     * @param map
     */
    public void savePermission(Map<String,Object> map) throws Exception {
        //设置主键的值
        String id = idWorker.nextId() + "";
        //通过map构造permission对象
        Permission permission = BeanMapUtils.mapToBean(map, Permission.class);
        permission.setId(id);
        //根据类型构造不同的资源对象（菜单、按钮、api）
        int type = permission.getType();
        switch (type){
            case PermissionConstants.PY_MENU:
                PermissionMenu menu = BeanMapUtils.mapToBean(map, PermissionMenu.class);
                menu.setId(id);
                permissionMenuDao.save(menu);
                break;
            case PermissionConstants.PY_POINT:
                PermissionPoint point = BeanMapUtils.mapToBean(map, PermissionPoint.class);
                point.setId(id);
                permissionPointDao.save(point);
                break;
            case PermissionConstants.PY_API:
                PermissionApi api = BeanMapUtils.mapToBean(map, PermissionApi.class);
                api.setId(id);
                permissionApiDao.save(api);
                break;
            default:
                throw new CommonException(ResultCode.FAIL);
        }
        //保存权限
        permissionDao.save(permission);
    }

    /**
     * 更新权限
     * @param map
     */
    public void updatePermission(Map<String,Object> map) throws Exception {
        Permission perm = BeanMapUtils.mapToBean(map, Permission.class);
        //通过传递的权限id查询权限
        Permission permission = permissionDao.findById(perm.getId()).get();
        permission.setName(perm.getName());
        permission.setCode(perm.getCode());
        permission.setDescription(perm.getDescription());
        permission.setEnVisible(perm.getEnVisible());
        //根据类型构造不同的资源对象（菜单、按钮、api）
        int type = permission.getType();
        switch (type){
            case PermissionConstants.PY_MENU:
                PermissionMenu menu = BeanMapUtils.mapToBean(map, PermissionMenu.class);
                menu.setId(perm.getId());
                permissionMenuDao.save(menu);
                break;
            case PermissionConstants.PY_POINT:
                PermissionPoint point = BeanMapUtils.mapToBean(map, PermissionPoint.class);
                point.setId(perm.getId());
                permissionPointDao.save(point);
                break;
            case PermissionConstants.PY_API:
                PermissionApi api = BeanMapUtils.mapToBean(map, PermissionApi.class);
                api.setId(perm.getId());
                permissionApiDao.save(api);
                break;
            default:
                throw new CommonException(ResultCode.FAIL);
        }
        //修改权限
        permissionDao.save(permission);
    }

    /**
     * 根据id删除权限
     *      1、删除权限
     *      2、删除权限对应的资源
     * @param id
     */
    public void deletePermissionById(String id) throws Exception {
        //通过传递的权限id查询权限
        Permission permission = permissionDao.findById(id).get();
        permissionDao.delete(permission);
        //根据类型构造不同的资源对象（菜单、按钮、api）
        int type = permission.getType();
        switch (type){
            case PermissionConstants.PY_MENU:
                permissionMenuDao.deleteById(id);
                break;
            case PermissionConstants.PY_POINT:
                permissionPointDao.deleteById(id);
                break;
            case PermissionConstants.PY_API:
                permissionApiDao.deleteById(id);
                break;
            default:
                throw new CommonException(ResultCode.FAIL);
        }
    }

    /**
     * 根据id查询权限
     *         1、查询权限
     *         2、根据权限的类型查询资源
     *         3、构造map集合
     * @param id
     * @return
     */
    public Map<String,Object> findPermissionById(String id) throws Exception {

        Permission permission = permissionDao.findById(id).get();
        Integer type = permission.getType();

        Object object = null;
        switch (type){
            case PermissionConstants.PY_MENU:
                object = permissionMenuDao.findById(id).get();
                break;
            case PermissionConstants.PY_POINT:
                object = permissionPointDao.findById(id).get();
                break;
            case PermissionConstants.PY_API:
                object = permissionApiDao.findById(id).get();
                break;
            default:
                throw new CommonException(ResultCode.FAIL);
        }

        Map<String, Object> map = BeanMapUtils.beanToMap(object);
        map.put("name",permission.getName());
        map.put("type",permission.getType());
        map.put("code",permission.getCode());
        map.put("description",permission.getDescription());
        map.put("pid",permission.getPid());
        map.put("enVisible",permission.getEnVisible());

        return map;
    }

    /**
     * 所有权限
     * @param map
     * @return
     *       type      : 查询全部权限列表type：0：菜单 + 按钮（权限点） 1：菜单2：按钮（权限点）3：API接口
     *       enVisible : 0：查询所有saas平台的最高权限，1：查询企业的权限
     *       pid       ：父id
     */
    public List<Permission> findAllPermissions(Map<String,Object> map){
        /**
         * 权限构造查询条件（这里使用了lambda表达式）
         * 参数说明
         *      root：包含了所有的对象数据
         *      cq：一般不用
         *      cb：构造查询条件
         */
        Specification<Permission> spec = (Specification<Permission>) (root, cq, cb) -> {
            /**
             * 动态拼接查询条件
             */
            List<Predicate> list = new ArrayList<>();
            //根据父id查询
            if(!StringUtils.isEmpty(map.get("pid"))) {
                list.add(cb.equal(root.get("pid").as(String.class),(String)map.get("pid")));
            }
            //根据enVisible查询
            if(!StringUtils.isEmpty(map.get("enVisible"))) {
                list.add(cb.equal(root.get("enVisible").as(String.class),(String)map.get("enVisible")));
            }
            //根据类型 type
            if(!StringUtils.isEmpty(map.get("type"))) {
                String type = (String) map.get("type");
                CriteriaBuilder.In<Object> in = cb.in(root.get("type"));
                if ("0".equals(type)){
                    in.value(1).value(2);
                }else {
                    in.value(Integer.parseInt(type));
                }
            }
            return cb.and(list.toArray(new Predicate[list.size()]));
        };
        return permissionDao.findAll(spec);
    }
}
