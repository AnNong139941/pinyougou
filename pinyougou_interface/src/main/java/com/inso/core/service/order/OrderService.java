package com.inso.core.service.order;

import com.inso.core.pojo.order.Order;

public interface OrderService {

    /**
     * ���ɶ���
     * @param username
     * @param order
     */
    void add(String username, Order order);
}
