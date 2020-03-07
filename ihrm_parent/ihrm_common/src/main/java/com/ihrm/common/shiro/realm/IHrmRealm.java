package com.ihrm.common.shiro.realm;

import com.ihrm.domain.system.response.ProfileResult;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.Map;
import java.util.Set;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/3/7 13:37
 *
 * 公共realm：获取安全数据，构造权限信息
 */
public class IHrmRealm extends AuthorizingRealm {

    @Override
    public void setName(String name){
        super.setName("ihrmRealm");
    }

    /**
     * 授权
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //1、获取安全数据
        ProfileResult result = (ProfileResult) principalCollection.getPrimaryPrincipal();
        //2、获取权限信息
        Set<String> apisPerms = (Set<String>) result.getRoles().get("apis");
        //3、构造权限数据，返回值
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(apisPerms);
        return info;
    }

    /**
     * 认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        return null;
    }
}
