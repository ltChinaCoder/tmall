package com.ozm.tmall.realm;

import com.ozm.tmall.entity.pojo.User;
import com.ozm.tmall.entity.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

public class JPARealm extends AuthorizingRealm {
        @Autowired
    UserService userService;


        @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
                return new SimpleAuthorizationInfo();

    }

        @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
                                        UsernamePasswordToken t = (UsernamePasswordToken) token;         String foreUserName = t.getUsername();         String forePassword = new String(t.getPassword());
                User user = userService.getByName(foreUserName);
                String passwordInDB = user.getPassword();
                String salt = user.getSalt();
                String passwordEncoded = new SimpleHash("md5", forePassword, salt, 2).toString();
                if (!passwordEncoded.equals(passwordInDB)) {
            throw new AuthenticationException();
        }
                SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(foreUserName, forePassword, getName());
        return authenticationInfo;
    }
}
