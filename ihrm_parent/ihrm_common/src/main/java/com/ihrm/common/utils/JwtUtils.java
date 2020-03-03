package com.ihrm.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Date;
import java.util.Map;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/3/3 16:05
 */
@Data
@ConfigurationProperties("jwt.config")
public class JwtUtils {
    //签名私钥
    private String key;
    //签名的失效时间
    private Long ttl;

    /**
     * 设置认证token
     * @param id        登录用户id
     * @param name      登录用户名
     * @param map       自定义信息
     * @return
     */
    public String createJwt(String id, String name, Map<String,Object> map){
        //1、创建失效时间
        long now = System.currentTimeMillis();  //当前毫秒
        long exp = now + ttl;   //当前毫秒+失效时长=失效时间
        //2、创建jwtBuilder
        JwtBuilder jwtBuilder = Jwts.builder().setId(id).setSubject(name)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, key);
        //3、创建map设置claims
        for (Map.Entry<String,Object> entry : map.entrySet()) {
            jwtBuilder.claim(entry.getKey(),entry.getValue());
        }
        //设置失效时间
        jwtBuilder.setExpiration(new Date(exp));
        //4、创建token
        String token = jwtBuilder.compact();
        return token;
    }

    /**
     * 解析token字符串获取claims
     * @param token
     * @return
     */
    public Claims parseJwt(String token){
        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        return claims;
    }
}
