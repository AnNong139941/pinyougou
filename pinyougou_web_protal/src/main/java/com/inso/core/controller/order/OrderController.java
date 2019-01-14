package com.inso.core.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.inso.core.entity.Result;
import com.inso.core.pojo.order.Order;
import com.inso.core.service.order.OrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("order")
public class OrderController {

    @Reference
    private OrderService orderService;

    @RequestMapping("add.do")
    public Result add(@RequestBody Order order) {

        try {
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            orderService.add(userName, order);
            return new Result(true, "订单提交成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "订单提交失败");
        }
    }
}
