package com.inso.core.service.pay;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.inso.core.dao.log.PayLogDao;
import com.inso.core.dao.order.OrderDao;
import com.inso.core.pojo.log.PayLog;
import com.inso.core.pojo.order.Order;
import com.inso.core.utils.httpClient.HttpClient;
import com.inso.core.utils.uniqueId.IdWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PayServiceImpl implements PayService {

    @Resource
    private IdWorker idWorker;

    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${partnerkey}")
    private String partnerkey;

    @Value("${notifyurl}")
    private String notifyurl;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private PayLogDao payLogDao;

    @Resource
    private OrderDao orderDao;

    /**
     * 生成支付的二维码
     *
     * @return
     */
    @Override
    public Map<String, String> createNative(String username) throws Exception {

        //从缓存中获取日志
        PayLog payLog = (PayLog) redisTemplate.boundHashOps("payLog").get(username);
        System.out.println(payLog);
        HashMap<String, String> data = new HashMap<>();
        //long out_trade_no = idWorker.nextId();              //订单号(流水号)

        //调用微信统一下单API
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";

        //公众账号ID	appid	是	String(32)	wxd678efh567hg6787	微信支付分配的公众账号ID（企业号corpid即为此appId）
        data.put("appid", appid);
        //商户号	mch_id	是	String(32)	1230000109	微信支付分配的商户号
        data.put("mch_id", partner);
        //随机字符串	nonce_str	是	String(32)	随机字符串，长度要求在32位以内。推荐随机数生成算法
        data.put("nonce_str", WXPayUtil.generateNonceStr());
        //签名	sign	是	String(32)	通过签名算法计算得出的签名值，详见签名生成算法
        //data.put("sign")  map转换为xml时会自动生成
        //商品描述	body	是	String(128)	腾讯充值中心-QQ会员充值
        data.put("body", "品优购商品订单支付");
        //商户订单号	out_trade_no	是	String(32)	20150806125346	商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|* 且在同一个商户号下唯一。详见商户订单号
        //data.put("out_trade_no", String.valueOf(out_trade_no));
        //从日志中获取
        data.put("out_trade_no", payLog.getOutTradeNo());
        //标价金额	total_fee	是	Int	88	订单总金额，单位为分，详见支付金额
        //data.put("total_fee",String.valueOf(payLog.getTotalFee()));
        data.put("total_fee", "1");
        //终端IP	spbill_create_ip	是	String(16)	123.12.12.123	支持IPV4和IPV6两种格式的IP地址。调用微信支付API的机器IP
        data.put("spbill_create_ip", "192.168.200.128");
        //通知地址	notify_url	是	String(256)	http://www.weixin.qq.com/wxpay/pay.php	异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
        data.put("notify_url", "http://www.baidu.com");
        //交易类型	trade_type	是	String(16)	 NATIVE -Native支付
        data.put("trade_type", "NATIVE");
        // trade_type=NATIVE时（即扫码支付），此参数必传。此参数为二维码中包含的商品ID，商户自行定义。
        data.put("product_id", "12235413214070356458058");

        //微信支付规定只能是xml传输
        String xmlParam = WXPayUtil.generateSignedXml(data, partnerkey);

        //使用HTTPClient模拟浏览器发送请求
        HttpClient httpClient = new HttpClient(url);
        httpClient.setHttps(true);                  //采用https
        httpClient.setXmlParam(xmlParam);            //微信接口所需要的数据
        httpClient.post();                          //采用POST方法提交
        String content = httpClient.getContent();   //响应结果

        //System.out.println("微信支付统一下单API: " + content);

        Map<String, String> map = WXPayUtil.xmlToMap(content);
        //map.put("total_fee", "1000000");
        map.put("total_fee", String.valueOf(payLog.getTotalFee()));
        //map.put("out_trade_no", String.valueOf(out_trade_no));
        map.put("out_trade_no", payLog.getOutTradeNo());
        // System.out.println(map.get("code_url"));
        return map;
    }

    /**
     * 查询订单
     *
     * @param out_trade_no
     * @return
     */
    @Override
    public Map<String, String> queryPayStatus(String out_trade_no, String username) throws Exception {
        //调用微信查询订单API

        String url = "https://api.mch.weixin.qq.com/pay/orderquery";
        HashMap<String, String> data = new HashMap<>();
        //公众账号ID	appid	是	String(32)	微信支付分配的公众账号ID（企业号corpid即为此appId）
        data.put("appid", appid);
        //商户号	mch_id	是	String(32)	1230000109	微信支付分配的商户号
        data.put("mch_id", partner);
        //订单号
        data.put("out_trade_no", out_trade_no);
        //随机字符串	nonce_str
        data.put("nonce_str", WXPayUtil.generateNonceStr());
        //签名	sign	是
        String xmlParam = WXPayUtil.generateSignedXml(data, partnerkey);

        //使用HTTPClient模拟浏览器发送请求
        HttpClient httpClient = new HttpClient(url);
        httpClient.setHttps(true);                  //采用https
        httpClient.setXmlParam(xmlParam);            //微信接口所需要的数据
        httpClient.post();                          //采用POST方法提交
        String strXML = httpClient.getContent();
        Map<String, String> map = WXPayUtil.xmlToMap(strXML);
        //System.out.println("调用微信查询订单api: " + strXML);
        // 支付成功更新支付日志表

        if ("SUCCESS".equals(map.get("trade_state"))) {
            PayLog payLog = new PayLog();
            payLog.setOutTradeNo(out_trade_no); // 根据主键更新
            payLog.setPayTime(new Date()); // 支付完成时间
            payLog.setTransactionId(map.get("transaction_id")); // 交易流水
            payLog.setTradeState("1"); // 支付状态:已支付
            payLogDao.updateByPrimaryKeySelective(payLog);

            //更新订单状态
            PayLog pl = payLogDao.selectByPrimaryKey(out_trade_no);
            if (pl != null & !"".equals(pl)) {

                String orderList = pl.getOrderList();
                String[] orderId = orderList.split(", ");
                for (String id : orderId) {
                    Order order = new Order();
                    order.setOrderId(Long.parseLong(id)); //订单id
                    order.setStatus("2");               //修改订单状态
                    order.setUpdateTime(new Date());    //修改时间
                    order.setPaymentTime(new Date());   //付款时间
                    orderDao.updateByPrimaryKeySelective(order);
                }
            }

            //删除缓存中的日志
            redisTemplate.boundHashOps("payLog").delete(username);

        }
        return map;
    }
}

