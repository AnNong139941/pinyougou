package com.inso.core.service.pay;

import java.util.Map;

public interface PayService {

    /**
     * 生成支付的二维码
     * @return
     */
    Map<String,String> createNative(String username) throws Exception;

    /**
     * 我们使用的是内网,服务器要想知道订单状态只能调用微信的查询订单API
     * @param out_trade_no
     * @return
     */
    Map<String,String> queryPayStatus(String out_trade_no,String username) throws Exception;
}
