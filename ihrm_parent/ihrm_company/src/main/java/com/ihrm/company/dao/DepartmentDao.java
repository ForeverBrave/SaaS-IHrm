package com.ihrm.company.dao;

import com.ihrm.domain.company.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/3/2 10:08
 */
public interface DepartmentDao extends JpaRepository<Department,String>, JpaSpecificationExecutor<Department> {
}
