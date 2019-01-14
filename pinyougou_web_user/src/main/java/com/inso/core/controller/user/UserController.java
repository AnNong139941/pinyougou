package com.inso.core.controller.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.inso.core.entity.Result;
import com.inso.core.pojo.user.User;
import com.inso.core.service.user.UserService;
import com.inso.core.utils.phone.PhoneFormatCheckUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController {

    @Reference
    private UserService userService;

    @RequestMapping("sendCode.do")
    public Result sendCode(String phone) {

        try {
            //校验手机号是否合法
            boolean phoneLegal = PhoneFormatCheckUtils.isPhoneLegal(phone);
            if (!phoneLegal) {
                return new Result(false, "手机号码不合法");
            }
            userService.sendCode(phone);
            return new Result(true, "发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "发送失败");
        }
    }

    /**
     * 用户注册
     *
     * @param smsCode
     * @param user
     * @return
     */
    @RequestMapping("add.do")
    public Result add(String smsCode, @RequestBody User user) {
        try {
            userService.add(smsCode, user);
            return new Result(true, "注册成功");
        } catch (RuntimeException e) {
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "注册失败");
        }
    }
}
