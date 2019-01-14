package com.inso.core.service.pay;

import java.util.Map;

public interface PayService {

    /**
     * ����֧���Ķ�ά��
     * @return
     */
    Map<String,String> createNative(String username) throws Exception;

    /**
     * ����ʹ�õ�������,������Ҫ��֪������״ֻ̬�ܵ���΢�ŵĲ�ѯ����API
     * @param out_trade_no
     * @return
     */
    Map<String,String> queryPayStatus(String out_trade_no,String username) throws Exception;
}
