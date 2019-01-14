package com.inso.core.service.address;

import com.alibaba.dubbo.config.annotation.Service;
import com.inso.core.dao.address.AddressDao;
import com.inso.core.pojo.address.Address;
import com.inso.core.pojo.address.AddressQuery;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Resource
    private AddressDao addressDao;

    /**
     * 根据登录人信息查询收货人地址
     *
     * @param name
     * @return
     */
    @Override
    public List<Address> findListByLoginUser(String name) {
        AddressQuery query = new AddressQuery();
        query.createCriteria().andUserIdEqualTo(name);
        return addressDao.selectByExample(query);
    }
}
