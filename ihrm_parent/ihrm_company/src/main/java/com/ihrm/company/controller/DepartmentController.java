package com.ihrm.company.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.company.service.CompanyService;
import com.ihrm.company.service.DepartmentService;
import com.ihrm.domain.company.Company;
import com.ihrm.domain.company.Department;
import com.ihrm.domain.company.dto.DeptListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/3/2 10:24
 */
@RestController
@RequestMapping("/company")
@CrossOrigin
public class DepartmentController extends BaseController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private CompanyService companyService;

    /**
     * 新增部门
     * @param department
     * @return
     */
    @PostMapping("/department")
    public Result saveDepartment(@RequestBody Department department){
        //1.设置保存的企业id(暂时用固定值 1 ，后面会解决)
        try {
            department.setCompanyId(companyId);
            departmentService.saveDepartment(department);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAIL();
        }
        return Result.SUCCESS();
    }

    /**
     * 查询所有部门
     * 指定企业id
     * @return
     */
    @GetMapping("/department")
    public Result findAllDepartments(){
        //指定企业id(暂定为 1，后续完善)
        DeptListDto deptListDto = null;
        try {
            Company companyById = companyService.findCompanyById(companyId);
            //完成查询
            List<Department> allDepartments = departmentService.findAllDepartments(companyId);
            //构造返回结果
            deptListDto = new DeptListDto(companyById, allDepartments);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAIL();
        }
        return new Result(ResultCode.SUCCESS,deptListDto);
    }

    /**
     * 根据id查询部门
     * @param id
     * @return
     */
    @GetMapping("/department/{id}")
    public Result findDepartmentById(@PathVariable String id){
        Department departmentById = null;
        try {
            departmentById = departmentService.findDepartmentById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAIL();
        }
        return new Result(ResultCode.SUCCESS,departmentById);
    }

    /**
     * 修改部门
     * @param id
     * @param department
     * @return
     */
    @PutMapping("/department/{id}")
    public Result updateDepartment(@PathVariable String id,@RequestBody Department department){
        try {
            //1.设置修改部门的id
            department.setId(id);
            //2.调用service进行更新
            departmentService.updateDepartment(department);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAIL();
        }
        return Result.SUCCESS();
    }

    /**
     * 根据id删除部门
     * @param id
     * @return
     */
    @DeleteMapping("/department/{id}")
    public Result deleteDepartmentById(@PathVariable String id){
        try {
            departmentService.deleteDepartmentById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAIL();
        }
        return Result.SUCCESS();
    }
}
