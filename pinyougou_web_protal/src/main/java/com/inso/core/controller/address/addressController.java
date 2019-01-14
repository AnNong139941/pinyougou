package com.inso.core.controller.address;

import com.alibaba.dubbo.config.annotation.Reference;
import com.inso.core.pojo.address.Address;
import com.inso.core.service.address.AddressService;
import org.springframework.data.map.repository.config.EnableMapRepositories;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("address")
public class addressController {

    @Reference
    private AddressService addressService;

    /**
     * 根据登录人信息查询收货人地址
     *
     * @return
     */
    @RequestMapping("findListByLoginUser.do")
    public List<Address> findListByLoginUser() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        return addressService.findListByLoginUser(name);

    }
}
