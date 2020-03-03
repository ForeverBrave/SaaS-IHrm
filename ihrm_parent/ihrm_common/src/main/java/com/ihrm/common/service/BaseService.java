package com.ihrm.common.service;

import org.springframework.data.jpa.domain.Specification;


/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/3/2 11:09
 */
public class BaseService<T> {

    protected Specification<T> getSpec(String companyId) {
        Specification spect = (Specification) (root, criteriaQuery, cb) -> {
            //根据企业id查询
            return cb.equal(root.get("companyId").as(String.class),companyId);
        };
        return spect;
    }

}

