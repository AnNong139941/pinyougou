package com.inso.core.service.address;

import com.inso.core.pojo.address.Address;

import java.util.List;

public interface AddressService {

    /**
     * 根据登录人信息查询收货人地址
     * @param name
     * @return
     */
    List<Address> findListByLoginUser(String name);
}
