package com.inso.core.controller.seckillOrder;

import com.alibaba.dubbo.config.annotation.Reference;
import com.inso.core.entity.Result;
import com.inso.core.service.seckillOrder.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("seckillOrder")
public class SeckillOrderController {

    @Reference
    private SeckillOrderService seckillOrderService;

    @RequestMapping("submitOrder.do")
    public Result submitOrder(Long seckillId) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //判断登录状态
        if ("anonymousUser".equals(username)) {
            return new Result(false, "用户未登录");
        }
        try {
            seckillOrderService.submitOrder(username, seckillId);
            return new Result(true, "订单提交成功");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "订单提交失败");
        }


    }
}
