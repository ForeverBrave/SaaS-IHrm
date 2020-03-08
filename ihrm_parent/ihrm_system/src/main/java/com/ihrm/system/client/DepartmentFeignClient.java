package com.ihrm.system.client;

import com.ihrm.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/3/8 10:35
 * 声明接口，通过Feign调用其它微服务
 */
@FeignClient("ihrm-company") //声明调用微服务的名称
public interface DepartmentFeignClient {
    /**
     * 调用微服务接口
     */
    @GetMapping("/company/department/{id}")
    Result findDepartmentById(@PathVariable String id);
}
