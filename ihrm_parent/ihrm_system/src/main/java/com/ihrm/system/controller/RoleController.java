package com.ihrm.system.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.PageResult;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.domain.system.Role;
import com.ihrm.system.service.RoleService;
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
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    /**
     * 新增用户
     * @param role
     * @return
     */
    @PostMapping("/role")
    public Result saveRole(@RequestBody Role role){
        //1.设置保存的企业id(暂时用固定值 1 ，后面会解决)
        try {
            role.setCompanyId(companyId);
            roleService.saveRole(role);
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
    @GetMapping("/role")
    public Result findAllRolesByPage(Integer page, Integer pagesize,Role role){
        PageResult<Role> pageResult = null;
        try {
            Page<Role> rolePage = roleService.findAllRolesByPage(companyId, page, pagesize);
            pageResult = new PageResult(rolePage.getTotalElements(),rolePage.getContent());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAIL();
        }
        return new Result(ResultCode.SUCCESS,pageResult);
    }

    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    @GetMapping("/role/{id}")
    public Result findRoleById(@PathVariable String id){
        Role roleById = null;
        try {
            roleById = roleService.findRoleById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAIL();
        }
        return new Result(ResultCode.SUCCESS,roleById);
    }

    /**
     * 查询所有用户
     * @return
     */
    @GetMapping("/role/list")
    public Result findAll(){
        List<Role> roleList = null;
        try {
            roleList = roleService.findAll(companyId);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAIL();
        }
        return new Result(ResultCode.SUCCESS,roleList);
    }

    /**
     * 修改用户
     * @param id
     * @param role
     * @return
     */
    @PutMapping("/role/{id}")
    public Result updateRole(@PathVariable String id,@RequestBody Role role){
        try {
            //1.调用service进行更新
            roleService.updateRole(role);
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
    @DeleteMapping("/role/{id}")
    public Result deleteRoleById(@PathVariable String id){
        try {
            roleService.deleteRoleById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAIL();
        }
        return Result.SUCCESS();
    }
}
