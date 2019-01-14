package com.inso.core.service.order;

import com.alibaba.dubbo.config.annotation.Service;
import com.inso.core.dao.item.ItemDao;
import com.inso.core.dao.log.PayLogDao;
import com.inso.core.dao.order.OrderDao;
import com.inso.core.dao.order.OrderItemDao;
import com.inso.core.pojo.cart.Cart;
import com.inso.core.pojo.item.Item;
import com.inso.core.pojo.log.PayLog;
import com.inso.core.pojo.order.Order;
import com.inso.core.pojo.order.OrderItem;
import com.inso.core.utils.uniqueId.IdWorker;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private IdWorker idWorker;

    @Resource
    private OrderDao orderDao;

    @Resource
    private OrderItemDao orderItemDao;

    @Resource
    private ItemDao itemDao;

    @Resource
    private PayLogDao payLogDao;

    /**
     * 生成订单
     *
     * @param username
     * @param order
     */
    @Transactional
    public void add(String username, Order order) {
        //我们在定义购物车时,是按商家分类的,所以订单也是一个商家对应一个订单
        //订单需要登录 所以从Redis中获取购物车
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("buyerCart").get(username);
        if (cartList != null && cartList.size() > 0) {
            List<Long> idsList = new ArrayList<>();       //保存所有的订单id,用于生成日志
            double logTotalFee = 0f;
            for (Cart cart : cartList) {
                long orderId = idWorker.nextId();
                order.setOrderId(orderId);              //订单id
                idsList.add(orderId);                   //保存订单id,用于生成日志
                double payment = 0f;                    //该商家下订单的支付金额
                order.setPaymentType("1");              //支付类型:在线支付
                order.setStatus("1");                   //订单状态:未付款
                order.setCreateTime(new Date());        //订单的创建时间
                order.setUpdateTime(new Date());        //订单的更新时间
                order.setUserId(username);              //用户id
                order.setSourceType("2");               //订单来源:PC端
                order.setSellerId(cart.getSellerId());  //商家id

                //获取订单明细
                List<OrderItem> orderItemList = cart.getOrderItemList();
                if (orderItemList != null && orderItemList.size() > 0) {
                    for (OrderItem orderItem : orderItemList) {
                        long id = idWorker.nextId();
                        orderItem.setId(id);        //订单明细id
                        //在放入Redis时已经将itemId存放
                        Item item = itemDao.selectByPrimaryKey(orderItem.getItemId());
                        orderItem.setGoodsId(item.getGoodsId());    //商品id SPU
                        orderItem.setOrderId(orderId);              //订单id
                        orderItem.setTitle(item.getTitle());        //商品标题
                        orderItem.setPrice(item.getPrice());        //商品价格
                        orderItem.setPicPath(item.getImage());      //商品图片
                        orderItem.setSellerId(item.getSellerId());  //商家id
                        double totalFee = orderItem.getNum() * item.getPrice().doubleValue();
                        orderItem.setTotalFee(new BigDecimal(totalFee));     //该订单明细的商品总金额

                        //保存订单明细
                        orderItemDao.insertSelective(orderItem);
                        //该商家下的订单总金额
                        payment += totalFee;
                    }
                }
                //支付总金额=所有商家的支付金额,用于生成日志
                logTotalFee += payment;
                order.setPayment(new BigDecimal(payment));
                //保存订单
                orderDao.insertSelective(order);

            }

            //生成订单日志
            PayLog payLog = new PayLog();

            payLog.setOutTradeNo(String.valueOf(idWorker.nextId()));    //支付订单号
            payLog.setCreateTime(new Date());                           //创建日期
            payLog.setUserId(username);                                 //用户id
            payLog.setTotalFee((long) logTotalFee * 100);                 //支付总金额 单位:分
            payLog.setTradeState("1");                                  // 交易状态 0：未支付   1：已支付
            //订单id集合[1,2]-->1,2(切割)
            payLog.setOrderList(idsList.toString().replace("[", "").replace("]", ""));
            payLog.setPayType("1");     //支付类型: 在线支付
            //生成日志
            payLogDao.insertSelective(payLog);
            //调用支付接口时需要从日志中获取,所以放入缓存中
            redisTemplate.boundHashOps("payLog").put(username, payLog);
        }


        // 删除redis中购物车
        redisTemplate.boundHashOps("buyerCart").delete(username);

    }
}
