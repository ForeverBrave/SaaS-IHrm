package com.ihrm.company.dao;

import com.ihrm.domain.company.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/2/29 18:43
 */
public interface CompanyDao extends JpaRepository<Company,String>, JpaSpecificationExecutor<Company> {
}
