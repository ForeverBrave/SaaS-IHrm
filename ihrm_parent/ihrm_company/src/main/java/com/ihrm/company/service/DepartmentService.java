package com.ihrm.company.service;

import com.ihrm.common.service.BaseService;
import com.ihrm.common.utils.IdWorker;
import com.ihrm.company.dao.DepartmentDao;
import com.ihrm.domain.company.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/3/2 10:09
 */
@Service
public class DepartmentService extends BaseService {

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 新增部门
     * @param department
     */
    public void saveDepartment(Department department){
        String id = idWorker.nextId() + "";
        department.setId(id);
        departmentDao.save(department);
    }

    /**
     * 更新部门
     * @param department
     */
    public void updateDepartment(Department department){
        //根据id查询部门
        Department dept = departmentDao.findById(department.getId()).get();
        //设置部门属性
        dept.setCode(department.getCode());
        dept.setIntroduce(department.getIntroduce());
        dept.setName(department.getName());
        //更新部门
        departmentDao.save(dept);
    }

    /**
     * 根据id删除部门
     * @param id
     */
    public void deleteDepartmentById(String id){
        departmentDao.deleteById(id);
    }

    /**
     * 根据id查询部门
     * @param id
     * @return
     */
    public Department findDepartmentById(String id){
        return departmentDao.findById(id).get();
    }

    /**
     * 查询全部部门列表
     * @return
     */
    public List<Department> findAllDepartments(String companyId){
        /**
         * 用户构造查询条件（这里使用了lambda表达式）
         * 参数说明
         *      root：包含了所有的对象数据
         *      cq：一般不用
         *      cb：构造查询条件
         */
        //Specification<Department> spec = (Specification<Department>) (root, cq, cb) -> cb.equal(root.get("companyId").as(String.class),companyId);
        //将方法抽取到了BaseService，并继承BaseService
        return departmentDao.findAll(getSpec(companyId));
    }

}
