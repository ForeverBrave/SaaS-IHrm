package com.ihrm.company.controller;

import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.company.service.CompanyService;
import com.ihrm.domain.company.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/2/29 19:10
 */
@RestController
@RequestMapping("/company")
@CrossOrigin
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    /**
     * 添加企业
     * @param company
     * @return
     */
    @PostMapping
    public Result addCompany(@RequestBody Company company){
        try {
            companyService.addCompany(company);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(ResultCode.FAIL);
        }
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 根据id修改企业
     * @param id
     * @param company
     * @return
     */
    @PutMapping("/{id}")
    public Result updateCompany(@PathVariable String id,@RequestBody Company company){
        try {
            company.setId(id);
            companyService.updateCompany(company);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(ResultCode.FAIL);
        }
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 根据id删除企业
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result deleteCompanyById(@PathVariable String id){
        try {
            companyService.deleteCompanyById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(ResultCode.FAIL);
        }
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 根据id查询企业
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findCompanyById(@PathVariable String id){
        Company company = null;
        try {
            company = companyService.findCompanyById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(ResultCode.FAIL);
        }
        return new Result(ResultCode.SUCCESS,company);
    }

    /**
     * 查询所有企业列表
     * @return
     */
    @GetMapping
    public Result findAllCompany(){
        List<Company> allCompany = null;
        try {
            allCompany = companyService.findAllCompany();
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(ResultCode.FAIL);
        }
        return new Result(ResultCode.SUCCESS,allCompany);
    }

    /**
     * 查询企业列表带分页
     * @param currentPage
     * @param pageSize
     * @return
     */
    @GetMapping("/{currentPage}/{pageSize}")
    public Result findAllCompanyByPage(@PathVariable Integer currentPage,@PathVariable Integer pageSize){
        Page<Company> allCompanyByPage = companyService.findAllCompanyByPage(currentPage, pageSize);
        return new Result(ResultCode.SUCCESS,allCompanyByPage);
    }
}
