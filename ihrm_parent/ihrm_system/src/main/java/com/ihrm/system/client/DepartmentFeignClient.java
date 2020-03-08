package com.ihrm.system.client;

import com.ihrm.common.entity.Result;
import com.ihrm.domain.company.Department;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @PostMapping("/company/department/search")
    Department findDepartmentByCode(@RequestParam(value = "code") String code, @RequestParam(value = "companyId") String companyId);
}
