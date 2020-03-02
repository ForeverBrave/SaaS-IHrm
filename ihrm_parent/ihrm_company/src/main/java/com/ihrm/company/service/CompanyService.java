package com.ihrm.company.service;

import com.ihrm.common.utils.IdWorker;
import com.ihrm.company.dao.CompanyDao;
import com.ihrm.domain.company.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/2/29 18:54
 */
@Service
public class CompanyService {

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 添加企业
     *  1、配置idworker到工程
     *  2、在service中注入idworker
     *  3、通过idworder生成id
     *  4、保存企业
     * @param company
     * @return
     */
    public void addCompany(Company company){
        //基本属性设置
        String id = idWorker.nextId() + "";
        company.setId(id);
        //默认的状态
        //0：未审核  1：已审核（默认未审核）
        company.setAuditState("0");
        //0：未激活  1：已激活（默认已激活）
        company.setState(1);
        companyDao.save(company);
    }

    /**
     * 更新企业
     * @param company
     */
    public void updateCompany(Company company){
        //1、根据id查询企业
        Company temp = companyDao.findById(company.getId()).get();
        //2、设置修改属性
        temp.setName(company.getName());
        temp.setCompanyPhone(company.getCompanyPhone());
        //3、调用dao，完成更新（jpa内部save方法中，会根据id查询，有id则更新，没id则添加）
        companyDao.save(company);
    }

    /**
     * 删除企业
     * @param id
     */
    public void deleteCompanyById(String id){
        companyDao.deleteById(id);
    }

    /**
     * 根据id查询企业
     * @param id
     * @return
     */
    public Company findCompanyById(String id){
        return companyDao.findById(id).get();
    }

    /**
     * 查询企业列表
     * @return
     */
    public List<Company> findAllCompany(){
        return companyDao.findAll();
    }

    /**
     * 查询企业列表带分页
     * @param currentPage
     * @param pageSize
     * @return
     */
    public Page<Company> findAllCompanyByPage(Integer currentPage, Integer pageSize){
        PageRequest pageRequest = PageRequest.of(currentPage, pageSize);
        return companyDao.findAll(pageRequest);
    }


}
