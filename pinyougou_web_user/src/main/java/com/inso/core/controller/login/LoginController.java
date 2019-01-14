package com.inso.core.controller.login;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@RestController
@RequestMapping("login")
public class LoginController {


    /**
     * 登录成功后显示当前登录人
     * @return
     */
    @RequestMapping("name.do")
    public Map<String,String> showName() {

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        HashMap<String, String> map = new HashMap<>();
        map.put("loginName",name);

        return map;
    }
}
