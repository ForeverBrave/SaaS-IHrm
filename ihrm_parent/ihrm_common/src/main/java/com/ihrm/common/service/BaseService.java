package com.ihrm.common.service;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/3/2 11:09
 */
public class BaseService<T> {

    protected Specification<T> getSpec(String companyId){
        Specification<T> spec = (Specification<T>) (root, cq, cb) -> cb.equal(root.get("companyId").as(String.class),companyId);
        return spec;
    }

}

