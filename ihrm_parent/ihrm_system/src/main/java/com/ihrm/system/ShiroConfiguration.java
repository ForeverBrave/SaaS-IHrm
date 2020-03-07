package com.ihrm.system;

import com.ihrm.common.shiro.realm.IHrmRealm;
import com.ihrm.common.shiro.session.CustomSessionManager;
import com.ihrm.system.shiro.realm.UserRealm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/3/7 10:51
 */
@Configuration
public class ShiroConfiguration {

    //1、创建realm
    @Bean
    public IHrmRealm getRealm(){
        return new UserRealm();
    }

    //2、创建安全管理器
    @Bean
    public SecurityManager getSecurityManager(IHrmRealm realm){
        //将自定义的realm配置进安全管理器
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager(realm);
        //将自定义的会话管理器注册到安全管理器中
        securityManager.setSessionManager(sessionManager());
        //将自定义的redis缓存管理器注册到安全管理器中
        securityManager.setCacheManager(redisCacheManager());
        return securityManager;
    }

    //3、配置shiro过滤器工厂  (在web程序中，shiro进行权限控制全部是通过一组过滤器集合进行控制)
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager){
        //1、创建过滤器工厂
        ShiroFilterFactoryBean filterFactory = new ShiroFilterFactoryBean();
        //2、设置安全管理器
        filterFactory.setSecurityManager(securityManager);
        //3、通用配置(跳转登录页面，未授权跳转页面)
        filterFactory.setLoginUrl("/autherror?code=1");//跳转url地址
        filterFactory.setUnauthorizedUrl("/autherror?code=2");//未授权rul地址
        //4、设置过滤器集合
        Map<String,String> filterMap = new LinkedHashMap<>();
        //anon -- 匿名访问(登录页面可以匿名访问)
        filterMap.put("/sys/login","anon");
        filterMap.put("/autherror","anon");
        //authc -- 认证之后访问（登录之后可以访问）
        filterMap.put("/**","authc");
        //perms -- 具有某种权限才能访问（这里会使用注解配置授权）
        /*
        //当前请求的地址可以匿名访问
        filterMap.put("/user/home","anon");
        //使用过滤器的形式配置请求地址的依赖权限
        filterMap.put("/user/home","perms[user-home]");//不具备指定的权限，跳转到  setUnauthorizedUrl地址
        //使用过滤器的形式配置请求地址的依赖权限
        filterMap.put("/user/home","roles[系统管理员]");//不具备指定的角色，跳转到  setUnauthorizedUrl地址
        */
        filterFactory.setFilterChainDefinitionMap(filterMap);
        return filterFactory;
    }

    /*****************************************redis相关开始***************************************************/

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.password}")
    private String password;
    /**
     * 1、redis控制器，操作redis
     */
    public RedisManager redisManager(){
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host);
        redisManager.setPort(port);
        redisManager.setPassword(password);
        return redisManager;
    }

    /**
     * 2、sessionDao
     */
    public RedisSessionDAO redisSessionDAO(){
        RedisSessionDAO sessionDAO = new RedisSessionDAO();
        sessionDAO.setRedisManager(redisManager());
        return sessionDAO;
    }

    /**
     * 3、会话管理器
     */
    public DefaultWebSessionManager sessionManager(){
        CustomSessionManager sessionManager = new CustomSessionManager();
        sessionManager.setSessionDAO(redisSessionDAO());
        //禁用cookie(可写可不写，根据需求来定)
        sessionManager.setSessionIdCookieEnabled(false);
        //禁用url重写(可写可不写，根据需求来定)
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }

    /**
     * 4、缓存管理器
     */
    public RedisCacheManager redisCacheManager(){
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }

    /*****************************************redis相关结束***************************************************/

    //4、配置shiro注解支持
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

}
