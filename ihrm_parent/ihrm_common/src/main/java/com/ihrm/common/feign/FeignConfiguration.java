package com.ihrm.common.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/3/8 10:57
 */
@Configuration
public class FeignConfiguration {

    //配置Feign拦截器，解决请求头问题
    @Bean
    public RequestInterceptor requestInterceptor(){
        //获取所有浏览器发送的请求属性，将请求头赋值到Feign中
        return requestTemplate -> {
            //请求属性
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                //获取request
                HttpServletRequest request = attributes.getRequest();
                //获取所有请求头的名称
                Enumeration<String> headerNames = request.getHeaderNames();
                if (headerNames != null) {
                    while (headerNames.hasMoreElements()){
                        //每个请求头名称
                        String name = headerNames.nextElement();
                        //每个请求头对应的数据
                        String value = request.getHeader(name);
                        requestTemplate.header(name,value);
                    }
                }
            }

        };
    }
}
