package com.ihrm.system.controller;

import com.ihrm.common.entity.PageResult;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.common.exception.CommonException;
import com.ihrm.domain.system.Permission;
import com.ihrm.system.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/3/3 10:23
 */
@RestController
@RequestMapping("/sys")
@CrossOrigin
public class PermissionController {


    @Autowired
    private PermissionService permissionService;

    /**
     * 新增权限
     * @param map
     * @return
     */
    @PostMapping("/permission")
    public Result savePermission(@RequestBody Map<String,Object> map){
        try {
            permissionService.savePermission(map);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAIL();
        }
        return Result.SUCCESS();
    }

    /**
     * 修改权限
     * @param id
     * @param map
     * @return
     */
    @PutMapping("/permission/{id}")
    public Result updatePermission(@PathVariable String id,@RequestBody Map<String,Object> map){
        try {
            //构造id
            map.put("id",id);
            permissionService.updatePermission(map);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAIL();
        }
        return Result.SUCCESS();
    }

    /**
     * 查询所有
     * @return
     */
    @GetMapping("/permission")
    public Result findAllPermissions(@RequestParam Map map){
        List<Permission> permissionList = null;
        try {
            permissionList = permissionService.findAllPermissions(map);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAIL();
        }
        return new Result(ResultCode.SUCCESS,permissionList);
    }

    /**
     * 根据id查询权限
     * @param id
     * @return
     */
    @GetMapping("/permission/{id}")
    public Result findPermissionById(@PathVariable String id){
        Map map = null;
        try {
            map = permissionService.findPermissionById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAIL();
        }
        return new Result(ResultCode.SUCCESS,map);
    }

    /**
     * 根据id删除权限
     * @param id
     * @return
     */
    @DeleteMapping("/permission/{id}")
    public Result deletePermissionById(@PathVariable String id){
        try {
            permissionService.deletePermissionById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAIL();
        }
        return Result.SUCCESS();
    }

}
