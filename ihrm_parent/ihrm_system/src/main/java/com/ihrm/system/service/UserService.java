package com.ihrm.system.service;

import com.ihrm.common.service.BaseService;
import com.ihrm.common.utils.IdWorker;
import com.ihrm.common.utils.QiniuUploadUtil;
import com.ihrm.domain.company.Department;
import com.ihrm.domain.system.Role;
import com.ihrm.domain.system.User;
import com.ihrm.system.client.DepartmentFeignClient;
import com.ihrm.system.dao.RoleDao;
import com.ihrm.system.dao.UserDao;
import org.apache.shiro.crypto.hash.Md5Hash;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import java.io.IOException;
import java.util.*;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/3/2 15:02
 */
@Service
public class UserService extends BaseService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private DepartmentFeignClient departmentFeignClient;

    /**
     * 根据mobile查询用户
     * @param mobile
     * @return
     */
    public User findByMobile(String mobile){
        return userDao.findByMobile(mobile);
    }

    /**
     * 新增用户
     * @param user
     */
    public void saveUser(User user){
        String id = idWorker.nextId() + "";
        user.setId(id);
        //密码加密(默认密码:123456)
        String password = new Md5Hash("123456",user.getMobile(), 3).toString();
        //设置初始密码
        user.setPassword(password);
        user.setLevel("user");
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
     * 查询所有用户
     * @param companyId
     * @return
     */
    public List<User> findAll(String companyId) {
        return userDao.findAll(super.getSpec(companyId));
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
        Page<User> userPage = userDao.findAll(spec,PageRequest.of(page-1, size));
        return userPage;
    }

    /**
     * 分配角色
     * @param userId
     * @param roleIds
     */
    public void assignRoles(String userId,List<String> roleIds) {
        //1.根据id查询用户
        User user = userDao.findById(userId).get();
        //2.设置用户的角色集合
        Set<Role> roles = new HashSet<>();
        for (String roleId : roleIds) {
            Role role = roleDao.findById(roleId).get();
            roles.add(role);
        }
        //设置用户和角色集合的关系
        user.setRoles(roles);
        //3.更新用户
        userDao.save(user);
    }

    /**
     * 批量插入用户
     * @param userList
     * @param companyId
     * @param companyName
     */
    @Transactional
    public void saveAll(List<User> userList, String companyId, String companyName) {
        for (User user : userList) {
            //默认密码
            user.setPassword(new Md5Hash("123456",user.getMobile(),3).toString());
            //id
            user.setId(idWorker.nextId()+"");
            //基本属性
            user.setCompanyId(companyId);
            user.setCompanyName(companyName);
            user.setInServiceStatus(1);
            user.setEnableState(1);
            user.setLevel("user");

            //填充部门属性
            Department dept = departmentFeignClient.findDepartmentByCode(user.getDepartmentId(), companyId);
            if (dept != null) {
                user.setDepartmentId(dept.getId());
                user.setDepartmentName(dept.getName());
            }

            userDao.save(user);
        }
    }

    /**
     * DataUrl 形式实现 图片上传
     * @param id   用户id
     * @param file 用户上传的头像文件
     * @return     请求地址
     */
    /*public String uploadImage(String id, MultipartFile file) throws IOException {
        //1、根据id查询用户
        User user = userDao.findById(id).get();
        //2、使用DataUrl的形式存储图片（对图片byte数组进行base64编码）
        String encode = "data:image/png;base64," + Base64.encode(file.getBytes());
        //3、更新用户头像地址
        user.setStaffPhoto(encode);
        userDao.save(user);
        //4、返回
        return encode;
    }*/

    /**
     * 七牛云 形式实现 图片上传
     * @param id   用户id
     * @param file 用户上传的头像文件
     * @return     请求地址
     */
    public String uploadImage(String id, MultipartFile file) throws IOException {
        //1、根据id查询用户
        User user = userDao.findById(id).get();
        //2、将图片上传到七牛云存储，获取请求路径
        String imgUrl = new QiniuUploadUtil().upload(user.getId(), file.getBytes());
        //3、更新用户头像地址
        user.setStaffPhoto(imgUrl);
        userDao.save(user);
        //4、返回
        return imgUrl;
    }

}
