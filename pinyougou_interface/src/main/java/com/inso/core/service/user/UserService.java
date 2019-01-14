package com.inso.core.service.user;

import com.inso.core.pojo.user.User;

public interface UserService {

    /**
     * 发送短信验证码
     * @param phone
     */
    public void sendCode(String phone);

    /**
     * 用户注册
     * @param smsCode
     * @param user
     */
    public void add(String smsCode,User user);
}
