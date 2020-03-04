package com.ihrm.system.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.PageResult;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.common.exception.CommonException;
import com.ihrm.common.utils.JwtUtils;
import com.ihrm.domain.system.Permission;
import com.ihrm.domain.system.User;
import com.ihrm.domain.system.response.ProfileResult;
import com.ihrm.domain.system.response.UserResult;
import com.ihrm.system.service.PermissionService;
import com.ihrm.system.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/3/2 15:41
 */
@RestController
@RequestMapping("/sys")
@CrossOrigin
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 分配角色
     * @param map
     * @return
     */
    @PutMapping("/user/assignRoles")
    public Result assignRoles(@RequestBody Map<String,Object> map){
        try {
            //1、获取被分配的用户id
            String userId = (String) map.get("id");
            //2、获取到角色的id列表
            List<String> roleIds = (List<String>) map.get("roleIds");
            //3、调用service完成角色分配
            userService.assignRoles(userId,roleIds);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAIL();
        }
        return Result.SUCCESS();
    }

    /**
     * 新增用户
     * @param user
     * @return
     */
    @PostMapping("/user")
    public Result saveUser(@RequestBody User user){
        //1.设置保存的企业id(暂时用固定值 1 ，后面会解决)
        try {
            user.setCompanyId(companyId);
            user.setCompanyName(companyName);
            userService.saveUser(user);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAIL();
        }
        return Result.SUCCESS();
    }

    /**
     * 分页查询所有用户
     * 指定企业id
     * @return
     */
    @GetMapping("/user")
    public Result findAllUsersByPage(int page, int size, @RequestParam Map map){
        PageResult pageResult = null;
        try {
            map.put("companyId",companyId);
            //调用service
            Page<User> allUsersByPage = userService.findAllUsersByPage(map, page, size);
            //构造返回结果  getTotalElements()：总条数  getContent()：List集合
            pageResult = new PageResult(allUsersByPage.getTotalElements(),allUsersByPage.getContent());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(ResultCode.SUCCESS,pageResult);
    }

    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    @GetMapping("/user/{id}")
    public Result findUserById(@PathVariable String id){
        UserResult userResult = null;
        try {
            //添加roleIds（用户所具有的角色）
            User user = userService.findUserById(id);
            userResult = new UserResult(user);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAIL();
        }
        return new Result(ResultCode.SUCCESS,userResult);
    }

    /**
     * 修改用户
     * @param id
     * @param user
     * @return
     */
    @PutMapping("/user/{id}")
    public Result updateUser(@PathVariable String id,@RequestBody User user){
        try {
            //1.设置修改用户的id
            user.setId(id);
            //2.调用service进行更新
            userService.updateUser(user);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAIL();
        }
        return Result.SUCCESS();
    }

    /**
     * 根据id删除用户
     * @param id
     * @return
     */
    @DeleteMapping("/user/{id}")
    public Result deleteUserById(@PathVariable String id){
        try {
            userService.deleteUserById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAIL();
        }
        return Result.SUCCESS();
    }

    /**
     * 用户登录
     * @param loginMap
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody Map<String,String> loginMap){
        String mobile = loginMap.get("mobile");
        String password = loginMap.get("password");

        User user = userService.findByMobile(mobile);
        //登录失败
        if(user == null || !user.getPassword().equals(password)){
            return new Result(ResultCode.MOBILE_OR_PASSWORDERROR);
        }else {
            //登录成功
            Map<String,Object> map = new HashMap<>();
            map.put("companyId",user.getCompanyId());
            map.put("companyName",user.getCompanyName());
            String token = jwtUtils.createJwt(user.getId(), user.getUsername(), map);
            return new Result(ResultCode.SUCCESS,token);
        }
    }

    /**
     * 用户登录成功之后，获取用户信息
     * @return
     */
    @PostMapping("/profile")
    public Result profile(HttpServletRequest request) throws Exception {
        //1、获取请求头信息，名称为 Authorization
        String authorization = request.getHeader("Authorization");
        if(StringUtils.isEmpty(authorization)){
            throw new CommonException(ResultCode.UNAUTHENTICATED);
        }
        //2、替换Bearer+空格（Bearer+空格为 约定熟成）
        String token = authorization.replace("Bearer ", "");
        //3、解析token
        Claims claims = jwtUtils.parseJwt(token);
        String userId = claims.getId();
        //4、根据用户id查询用户
        User user = userService.findUserById(userId);
        //4.1、根据不同的用户级别获取用户权限
        ProfileResult result = null;
        if("user".equals(user.getLevel())){
            //4.2、企业用户具有的当前角色权限
            result = new ProfileResult(user);
            System.out.println(result);
        }else {
            Map map = new HashMap();
            if("coAdmin".equals(user.getLevel())){
                //4.3、企业管理员具有的所有的企业权限
                map.put("enVisible","1");
            }
            //4.4、saas平台管理员具有所有的权限
            List<Permission> list = permissionService.findAllPermissions(map);
            result = new ProfileResult(user,list);
        }
        //5、构建返回值对象
        return new Result(ResultCode.SUCCESS,result);
    }
}
