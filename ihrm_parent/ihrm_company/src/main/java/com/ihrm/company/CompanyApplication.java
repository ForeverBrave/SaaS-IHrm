package com.ihrm.company;

import com.ihrm.common.utils.IdWorker;
import com.ihrm.common.utils.JwtUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/2/29 17:06
 */
@SpringBootApplication(scanBasePackages = "com.ihrm")
@EntityScan(value = "com.ihrm.domain.company")  //配置jpa注解扫描
public class CompanyApplication {

    public static void main(String[] args) {
        SpringApplication.run(CompanyApplication.class);
    }

    /**
     * SpringBootApplication初始化的时候注入JwtUtils
     * @return
     */
    @Bean
    public JwtUtils jwtUtils(){
        return new JwtUtils();
    }

    /**
     * SpringBootApplication初始化的时候启动IdWorker
     * @return
     */
    @Bean
    public IdWorker idWorker(){
        return new IdWorker();
    }



}
