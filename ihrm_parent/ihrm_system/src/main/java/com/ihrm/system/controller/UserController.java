package com.ihrm.system.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.PageResult;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.domain.system.User;
import com.ihrm.domain.system.response.UserResult;
import com.ihrm.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

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
}
