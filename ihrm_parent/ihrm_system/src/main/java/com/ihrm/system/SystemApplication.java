package com.ihrm.system;

import com.ihrm.common.utils.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/3/2 14:49
 */
@SpringBootApplication(scanBasePackages = "com.ihrm")
@EntityScan(value = "com.ihrm.domain.system")  //配置jpa注解扫描
public class SystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class);
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
