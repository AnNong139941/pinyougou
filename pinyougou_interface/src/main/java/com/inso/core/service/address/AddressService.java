package com.inso.core.service.address;

import com.inso.core.pojo.address.Address;

import java.util.List;

public interface AddressService {

    /**
     * ���ݵ�¼����Ϣ��ѯ�ջ��˵�ַ
     * @param name
     * @return
     */
    List<Address> findListByLoginUser(String name);
}
