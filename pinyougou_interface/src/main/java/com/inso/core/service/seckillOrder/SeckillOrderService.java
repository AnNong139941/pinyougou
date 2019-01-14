package com.inso.core.service.seckillOrder;

import com.inso.core.pojo.seckill.SeckillOrder;

public interface SeckillOrderService {

    /**
     * 秒杀商品提交订单
     *
     * @param username
     * @param seckillId
     */
    void submitOrder(String username, Long seckillId);

    /**
     * 根据用户名查询秒杀订单(秒杀的订单有时间限制)
     *
     * @param username
     * @return
     */
    SeckillOrder searchOrderFromRedisByUserId(String username);

    /**
     * 支付成功,将缓存中的订单保存到数据库
     *
     * @param userId
     * @param orderId
     * @param transactionId
     */
    void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId);
}
