package com.inso.core.service.cart;

import com.alibaba.dubbo.config.annotation.Service;
import com.inso.core.dao.item.ItemDao;
import com.inso.core.dao.seller.SellerDao;
import com.inso.core.pojo.cart.Cart;
import com.inso.core.pojo.item.Item;
import com.inso.core.pojo.order.OrderItem;
import com.inso.core.pojo.seller.Seller;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Resource
    private SellerDao sellerDao;

    @Resource
    private ItemDao itemDao;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 将商品加入购物车
     *
     * @param skuId
     * @return
     */
    @Override
    public Item findOne(Long skuId) {
        return itemDao.selectByPrimaryKey(skuId);
    }

    /**
     * 填充页面展示所需的数据
     *
     * @param cartList
     * @return
     */
    @Override
    public List<Cart> autoDataToCartList(List<Cart> cartList) {
        for (Cart cart : cartList) {
            Seller seller = sellerDao.selectByPrimaryKey(cart.getSellerId());
            if (seller != null && !"".equals(seller)) {

                cart.setSellerName(seller.getNickName());   // 商家店铺名称
            }
            List<OrderItem> orderItemList = cart.getOrderItemList();
            for (OrderItem orderItem : orderItemList) {
                Item item = itemDao.selectByPrimaryKey(orderItem.getItemId());
                orderItem.setPicPath(item.getImage());  // 商品图片
                orderItem.setTitle(item.getTitle());    // 商品标题
                orderItem.setPrice(item.getPrice());    // 商品单价
                // 商品小计：单价*数量
                orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * orderItem.getNum()));
            }
        }
        return cartList;

    }

    /**
     * 登录后将本地购物车合并到Redis中
     *
     * @param newCartList
     * @param name
     */
    @Override
    public void mergeCarList(List<Cart> newCartList, String name) {
        //1.从Redis中取出旧的购物车
        List<Cart> oldCartList = (List<Cart>) redisTemplate.boundHashOps("buyerCart").get(name);
        //2.将新车合并到老车
        oldCartList = mergeNewCarListAndOldCartList(newCartList, oldCartList);
        //3.将合并后的车子保存到Redis中
        redisTemplate.boundHashOps("buyerCart").put(name, oldCartList);
    }

    /**
     * 从Redis中取出购物车
     *
     * @param name
     * @return
     */
    @Override
    public List<Cart> findCartListFromRedis(String name) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("buyerCart").get(name);
        return cartList;

    }

    /**
     * 合并新旧(cookie中和Redis中)购物车
     *
     * @param newCartList
     * @param oldCartList
     * @return
     */
    private List<Cart> mergeNewCarListAndOldCartList(List<Cart> newCartList, List<Cart> oldCartList) {
        //判断新车是否为空
        if (newCartList != null && newCartList.size() > 0) {
            //判断老车是否为空
            if (oldCartList != null && oldCartList.size() > 0) {
                //如果两个车都不为空进行合并  将新车合并到老车 返回老车
                for (Cart cart : newCartList) {
                    //判断是否是同一个商家 因为设计购物车时是按此分类的
                    int sellerIndexOf = oldCartList.indexOf(cart);
                    if (sellerIndexOf != -1) {
                        //同一个商家
                        //判断是否是同一款商品
                        List<OrderItem> newOrderItemList = cart.getOrderItemList();    //新车购物项
                        List<OrderItem> oldOrderItemList = oldCartList.get(sellerIndexOf).getOrderItemList();   //老车购物项
                        //遍历新车购物项
                        for (OrderItem newOrderItem : newOrderItemList) {
                            //判断老车中是否有该购物项(新的)
                            int itemIndexOf = oldOrderItemList.indexOf(newOrderItem);
                            if (itemIndexOf != -1) {
                                //同款商品 合并数量
                                OrderItem oldOrderItem = oldOrderItemList.get(itemIndexOf);
                                Integer oldNum = oldOrderItem.getNum();
                                Integer newNum = newOrderItem.getNum();
                                oldOrderItem.setNum(oldNum + newNum);       //合并数量


                            } else {
                                //同商家不同商品 直接将该购物项 放入老车购物项
                                oldOrderItemList.add(newOrderItem);
                            }
                        }
                    } else {
                        //不同商家,将cart放入老车中
                        oldCartList.add(cart);
                    }
                }

            } else {
                //老车为空,直接返回新车
                return newCartList;
            }
        } else {
            //新车为空 直接将老车返回
            return oldCartList;
        }
        return oldCartList;

    }
}
