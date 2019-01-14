package com.inso.core.service.cart;

import com.inso.core.pojo.cart.Cart;
import com.inso.core.pojo.item.Item;

import java.util.List;

public interface CartService {

    /**
     * 根据id查询sku对象
     * @param skuId
     * @return
     */
    public Item findOne(Long skuId);

    /**
     * 填充页面展示所需的数据
     * @param cartList
     * @return
     */
    List<Cart> autoDataToCartList(List<Cart> cartList);

    /**
     * 登录后将本地购物车合并到Redis中
     * @param cartList
     * @param name
     */
    void mergeCarList(List<Cart> cartList, String name);

    /**
     * 从Redis中取出购物车
     * @param name
     * @return
     */
    List<Cart> findCartListFromRedis(String name);
}
