package com.inso.core.service.seckillOrder;

import com.inso.core.pojo.seckill.SeckillOrder;

public interface SeckillOrderService {

    /**
     * ��ɱ��Ʒ�ύ����
     *
     * @param username
     * @param seckillId
     */
    void submitOrder(String username, Long seckillId);

    /**
     * �����û�����ѯ��ɱ����(��ɱ�Ķ�����ʱ������)
     *
     * @param username
     * @return
     */
    SeckillOrder searchOrderFromRedisByUserId(String username);

    /**
     * ֧���ɹ�,�������еĶ������浽���ݿ�
     *
     * @param userId
     * @param orderId
     * @param transactionId
     */
    void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId);
}
