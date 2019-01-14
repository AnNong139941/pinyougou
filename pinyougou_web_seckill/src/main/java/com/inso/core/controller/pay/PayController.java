package com.inso.core.controller.pay;

import com.alibaba.dubbo.config.annotation.Reference;
import com.inso.core.entity.Result;
import com.inso.core.pojo.seckill.SeckillOrder;
import com.inso.core.service.pay.PayService;
import com.inso.core.service.seckillOrder.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("pay")
public class PayController {

    @Reference
    private SeckillOrderService seckillOrderService;

    @Reference
    private PayService payService;


    @RequestMapping("createNative.do")
    private Map<String, String> createNative() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //查询订单
        SeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(username);

        if (seckillOrder != null) {
            try {
                return payService.createNative(username);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new HashMap<>();
    }

    @RequestMapping("queryPayStatus.do")
    public Result queryPayStatus(String out_trade_no) throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //生成 二维码后,需要查询订单是否支付成功
        //编写定时器
        int time = 0;
        while (true) {
            //查询订单获取相应结果
            Map<String, String> map = payService.queryPayStatus(out_trade_no, username);
            //判断交易是否成功
            if ("SUCCESS".equals(map.get("trade_state"))) {
                //交易成功 保存订单到数据库中
                seckillOrderService.saveOrderFromRedisToDb(username, Long.valueOf(out_trade_no), map.get("transaction_id"));
                return new Result(true, "支付成功");
            } else {
                //支付未成功,或者正在支付等情况
                Thread.sleep(5000);
                time++;
            }
            //默认code_url是两个小时,我们设置为半个小时过期
            if (time > 360) {
                return new Result(false, "二维码超时");
            }
        }
    }


}
