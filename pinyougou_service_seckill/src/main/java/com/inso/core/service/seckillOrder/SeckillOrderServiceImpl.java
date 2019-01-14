package com.inso.core.service.seckillOrder;

import com.alibaba.dubbo.config.annotation.Service;
import com.inso.core.dao.seckill.SeckillGoodsDao;
import com.inso.core.dao.seckill.SeckillOrderDao;
import com.inso.core.pojo.seckill.SeckillGoods;
import com.inso.core.pojo.seckill.SeckillOrder;
import com.inso.core.utils.uniqueId.IdWorker;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Resource
    private SeckillOrderDao seckillOrderDao;

    @Resource
    private SeckillGoodsDao seckillGoodsDao;

    @Resource
    private IdWorker idWorker;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 秒杀商品提交订单
     *
     * @param username
     * @param seckillId
     */
    @Override
    public void submitOrder(String username, Long seckillId) {

        //1.从缓存中获取秒杀商品:
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);

        //扣减库存:
        //2.1如果商品为空 返回商品不存在
        if (seckillGoods == null) {
            throw new RuntimeException("暂无商品!");
        }
        //2.2如果剩余库存<=0 返回 商品已售完
        if (seckillGoods.getStockCount() <= 0) {
            throw new RuntimeException("商品已售完!");
        }
        //开始扣减缓存中的库存数量并重新放入缓存
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        redisTemplate.boundHashOps("seckillGoods").put(seckillId, seckillGoods);

        //如果剩余库存==0 也就是售完 更新秒杀商品的数据库库存数量
        if (seckillGoods.getStockCount() == 0) {
            seckillGoodsDao.updateByPrimaryKey(seckillGoods);
        }
        //3.设置订单到缓存(预处理订单)
        //idworker 生成主键
        long id = idWorker.nextId();
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setId(id);                 //订单号
        seckillOrder.setSeckillId(seckillId);   //秒杀的商品id
        seckillOrder.setMoney(seckillGoods.getCostPrice());//支付金额 一个商品一个订单
        seckillOrder.setUserId(username);       //用户id
        seckillOrder.setSellerId(seckillGoods.getSellerId()); //商家id
        seckillOrder.setCreateTime(new Date()); //创建时间
        seckillOrder.setStatus("0");            //支付状态: 0 为支付
        //seckillOrderDao.insertSelective(seckillOrder);
        redisTemplate.boundHashOps("seckillOrder").put(username, seckillOrder);
    }

    /**
     * 根据用户名查询秒杀订单(秒杀订单有时间限制)
     *
     * @param username
     * @return
     */
    @Override
    public SeckillOrder searchOrderFromRedisByUserId(String username) {

        return (SeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(username);

    }

    /**
     * 支付成功,将缓存中的订单保存到数据库
     *
     * @param username
     * @param orderId
     * @param transactionId
     */
    @Override
    public void saveOrderFromRedisToDb(String username, Long orderId, String transactionId) {
        //从缓存中取出订单
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(username);

        if (seckillOrder == null) {
            throw new RuntimeException("订单不存在");
        }

        //判断订单id是否一致
        if (seckillOrder.getId() != orderId) {
            throw new RuntimeException("订单不符合");
        }
        //保存到数据库中
        seckillOrder.setStatus("1");    //支付状态:已支付 1
        seckillOrder.setTransactionId(transactionId);   //微信提供的交易流水号
        seckillOrder.setPayTime(new Date());    //支付时间
        seckillOrderDao.insertSelective(seckillOrder);  //保存到数据库

        //删除缓存
        redisTemplate.boundHashOps("seckillGoods").delete(username);


    }
}
