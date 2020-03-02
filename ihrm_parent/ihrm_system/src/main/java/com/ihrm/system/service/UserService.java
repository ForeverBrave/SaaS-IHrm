package com.ihrm.system.service;

import com.ihrm.common.utils.IdWorker;
import com.ihrm.domain.system.User;
import com.ihrm.system.dao.UserDao;
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
public class UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 新增用户
     * @param user
     */
    public void saveUser(User user){
        String id = idWorker.nextId() + "";
        user.setId(id);
        user.setPassword("123456");
        user.setEnableState(1);
        userDao.save(user);
    }

    /**
     * 更新用户
     * @param user
     */
    public void updateUser(User user){
        //根据id查询用户
        User result = userDao.findById(user.getId()).get();
        //设置用户属性
        result.setUsername(user.getUsername());
        result.setPassword(user.getPassword());
        result.setDepartmentId(user.getDepartmentId());
        result.setDepartmentName(user.getDepartmentName());
        //更新用户
        userDao.save(result);
    }

    /**
     * 根据id删除用户
     * @param id
     */
    public void deleteUserById(String id){
        userDao.deleteById(id);
    }

    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    public User findUserById(String id){
        return userDao.findById(id).get();
    }

    /**
     * 分页查询所有用户
     * @param map
     *          map中的三个参数 hasDept departmentId companyId
     * @param page
     * @param size
     *
     * @return
     */
    public Page<User> findAllUsersByPage(Map<String,Object> map,int page,int size){
        /**
         * 用户构造查询条件（这里使用了lambda表达式）
         * 参数说明
         *      root：包含了所有的对象数据
         *      cq：一般不用
         *      cb：构造查询条件
         */
        Specification<User> spec = (Specification<User>) (root, cq, cb) -> {
            /**
             * 动态拼接查询条件
             */
            List<Predicate> list = new ArrayList<>();
            //根据请求的企业id是否为空构造查询条件
            if(!StringUtils.isEmpty(map.get("companyId"))) {
                list.add(cb.equal(root.get("companyId").as(String.class),(String)map.get("companyId")));
            }
            //根据请求的部门id构造查询条件
            if(!StringUtils.isEmpty(map.get("departmentId"))) {
                list.add(cb.equal(root.get("departmentId").as(String.class),(String)map.get("departmentId")));
            }
            if(!StringUtils.isEmpty(map.get("hasDept"))) {
                //根据请求的hasDept判断  是否分配部门 0未分配（departmentId = null），1 已分配 （departmentId ！= null）
                if("0".equals((String) map.get("hasDept"))) {
                    list.add(cb.isNull(root.get("departmentId")));
                }else {
                    list.add(cb.isNotNull(root.get("departmentId")));
                }
            }
            return cb.and(list.toArray(new Predicate[list.size()]));
        };
        //分页
        Page<User> userPage = userDao.findAll(spec,PageRequest.of(page, size));

        return userPage;
    }
}
