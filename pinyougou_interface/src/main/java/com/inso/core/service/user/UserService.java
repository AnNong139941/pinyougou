package com.inso.core.service.user;

import com.inso.core.pojo.user.User;

public interface UserService {

    /**
     * ���Ͷ�����֤��
     * @param phone
     */
    public void sendCode(String phone);

    /**
     * �û�ע��
     * @param smsCode
     * @param user
     */
    public void add(String smsCode,User user);
}
