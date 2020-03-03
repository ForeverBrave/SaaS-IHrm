package com.ihrm.demo;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/3/3 15:52
 */
public class CreateJwtTest {
    /**
     * 通过jjwt创建token
     *      setId,setSubject  设置用户信息
     *      setIssuedAt  设置日期
     *      claim   自定义信息
     *      signWith(加密算法，私钥)
     * @param args
     */
    public static void main(String[] args) {
        JwtBuilder jwtBuilder = Jwts.builder().setId("888").setSubject("Brave")
                .setIssuedAt(new Date())
                .claim("companyId","666666")
                .claim("companyName","青春年华有限公司")
                .signWith(SignatureAlgorithm.HS256, "ihrm");
        String token = jwtBuilder.compact();
        System.out.println(token);
    }
}
