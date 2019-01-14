package com.inso.core.service.cart;

import com.inso.core.pojo.cart.Cart;
import com.inso.core.pojo.item.Item;

import java.util.List;

public interface CartService {

    /**
     * ����id��ѯsku����
     * @param skuId
     * @return
     */
    public Item findOne(Long skuId);

    /**
     * ���ҳ��չʾ���������
     * @param cartList
     * @return
     */
    List<Cart> autoDataToCartList(List<Cart> cartList);

    /**
     * ��¼�󽫱��ع��ﳵ�ϲ���Redis��
     * @param cartList
     * @param name
     */
    void mergeCarList(List<Cart> cartList, String name);

    /**
     * ��Redis��ȡ�����ﳵ
     * @param name
     * @return
     */
    List<Cart> findCartListFromRedis(String name);
}
