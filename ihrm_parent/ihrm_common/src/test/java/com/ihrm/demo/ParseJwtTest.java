package com.ihrm.demo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/3/3 15:55
 */
public class ParseJwtTest {
    /**
     * 解析jwtToken字符串
     *      setSigningKey(自定义的私钥)
     * @param args
     */
    public static void main(String[] args) {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMDY2MzcwNDk4NjMzNDg2MzM2Iiwic3ViIjoiemJ6IiwiaWF0IjoxNTgzMzAzMDg4LCJjb21wYW55SWQiOiIxIiwiY29tcGFueU5hbWUiOiLkvKDmmbrmkq3lrqIiLCJleHAiOjE1ODMzMDY2ODh9.AGahtmY7AC8Ou-1sT6qscELAVk3BIukGviMmUI_efUM";
        Claims claims = Jwts.parser().setSigningKey("saas-ihrm").parseClaimsJws(token).getBody();

        //私有数据存放在claims
        System.out.println(claims.getId());
        System.out.println(claims.getSubject());
        System.out.println(claims.getIssuedAt());

        //解析自定义claim中的内容
        String companyId = (String) claims.get("companyId");
        String companyName = (String) claims.get("companyName");
        System.out.println(companyId+"——"+companyName);

    }
}
