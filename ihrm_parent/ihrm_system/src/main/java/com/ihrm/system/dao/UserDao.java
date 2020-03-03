package com.ihrm.system.dao;

import com.ihrm.domain.system.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/3/2 15:01
 */
public interface UserDao extends JpaRepository<User,String>, JpaSpecificationExecutor<User> {
    User findByMobile(String mobile);
}
