package com.ihrm.system.shiro.realm;

import com.ihrm.common.shiro.realm.IHrmRealm;
import com.ihrm.domain.system.Permission;
import com.ihrm.domain.system.User;
import com.ihrm.domain.system.response.ProfileResult;
import com.ihrm.system.service.PermissionService;
import com.ihrm.system.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/3/7 13:40
 */
public class UserRealm extends IHrmRealm {

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;


    /**
     * 认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        //1、获取用户手机号和密码
        UsernamePasswordToken upToken = (UsernamePasswordToken) authenticationToken;
        String mobile = upToken.getUsername();
        String password = new String(upToken.getPassword());
        //2、根据手机号查询用户
        User user = userService.findByMobile(mobile);
        //3、判断用户是否存在，用户密码是否和输入密码一致
        if(user != null && user.getPassword().equals(password)){
            //4、构造安全数据并返回(安全数据只需要  用户基本数据，权限信息即可，故使用ProfileResult)
            ProfileResult result = null;
            if("user".equals(user.getLevel())){
                //4.2、企业用户具有的当前角色权限
                result = new ProfileResult(user);
            }else {
                Map map = new HashMap();
                if("coAdmin".equals(user.getLevel())){
                    //4.3、企业管理员具有的所有的企业权限
                    map.put("enVisible","1");
                }
                //4.4、saas平台管理员具有所有的权限
                List<Permission> list = permissionService.findAllPermissions(map);
                result = new ProfileResult(user,list);
            }
            //构造方法：安全数据，密码，realm域名
            SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(result,user.getPassword(),this.getName());
            return info;
        }
        //返回null，会抛出异常，表示用户名密码不匹配
        throw new UnknownAccountException();
    }

}
