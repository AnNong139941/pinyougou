package com.inso.core.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;

/**
 * springSecurity与cas整合后的认证类
 */
public class UserDetailServiceImpl implements UserDetailsService {
    /**
     * 进入到该方法说明cas已经对用户完成认证,因此我们只需要授权就可以了
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //通过springsecurity完成授权,认证交给cas完成

        HashSet<GrantedAuthority> authorities = new HashSet<>();
        //添加访问权限
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
        authorities.add(grantedAuthority);
        User user = new User(username,"",authorities);
        return user;
    }
}
