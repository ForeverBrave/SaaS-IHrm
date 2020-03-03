package com.ihrm.domain.company.dto;

import com.ihrm.domain.company.Company;
import com.ihrm.domain.company.Department;
import lombok.Data;

import java.util.List;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/3/2 10:44
 */
@Data
public class DeptListDto {

    private String companyId;
    private String companyName;
    //公司联系人
    private String companyManage;
    private List<Department> depts;

    public DeptListDto(Company company,List depts) {
        this.companyId = company.getId();
        this.companyName = company.getName();
        this.companyManage = company.getLegalRepresentative();
        this.depts = depts;
    }
}
