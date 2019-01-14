package com.inso.core.service;

import com.inso.core.pojo.seller.Seller;
import com.inso.core.service.seller.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Set;

public class UserDetailsServiceImpl implements UserDetailsService {


    private SellerService sellerService;

    //手动注入
    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //根据登录名称查询商家对象(主键)
        Seller seller = sellerService.findOne(username);

        //只有审核通过的商家才可以登录
        if (seller != null && "1".equals(seller.getStatus())) {

            Set<GrantedAuthority> authorities = new HashSet<>();

            authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));


            User user = new User(username, seller.getPassword(), authorities);
            return user;

        }
        return null;
    }
}
