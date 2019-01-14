package com.inso.core.service.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.inso.core.dao.user.UserDao;
import com.inso.core.pojo.user.User;
import com.inso.core.utils.MD5.MD5Util;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private JmsTemplate jmsTemplate;

    @Resource
    private Destination smsDestination;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private UserDao userDao;

    @Override
    public void sendCode(final String phone) {

        //生成随机6位数验证码
        final String code = RandomStringUtils.randomNumeric(6);
        //System.out.println("============发送的验证码============="+code);
        //实现code的session共享
        redisTemplate.boundValueOps(phone).set(code);
        //设置验证码过期时间
        redisTemplate.boundValueOps(phone).expire(5, TimeUnit.MINUTES);

        jmsTemplate.send(smsDestination, new MessageCreator() {
            //map格式的消息
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("phoneNumbers", phone);
                mapMessage.setString("signName", "阮文");
                mapMessage.setString("templateCode", "SMS_140720901");
                mapMessage.setString("templateParam", "{\"code\":\"" + code + "\"}");
                return mapMessage;
            }
        });
    }

    /**
     * 用户注册
     *
     * @param smsCode
     * @param user
     */
    @Override
    public void add(String smsCode, User user) {

        //获取到验证进行校验
        //System.out.println("页面输入: "+smsCode);
        String code = (String) redisTemplate.boundValueOps(user.getPhone()).get();
        //System.out.println("-------校验 "+code);
        if (code != null && !"".equals(smsCode) && code.equals(smsCode)) {
            //验证一致 进行注册
            //对密码进行md5加密
            String password = MD5Util.MD5Encode(user.getPassword(), null);
            user.setPassword(password);
            user.setCreated(new Date());
            user.setUpdated(new Date());
            userDao.insertSelective(user);
        } else {
            throw new RuntimeException("验证不正确");
        }

    }
}

