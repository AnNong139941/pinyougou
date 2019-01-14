package com.inso.core.listener;

import com.inso.core.service.itemsearch.ItemSearchService;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * 自定义监听器:将商品信息保存到索引库
 */
public class ItemSearchListener implements MessageListener {

    @Resource
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        //1.监听MQ容器获得消息
        ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) message;
        try {
            //获取消息(商品id)
            String id = activeMQTextMessage.getText();
            System.out.println("service-search:id= " + id);
            //2.消费消息  处理业务
            itemSearchService.updateToSolr(Long.parseLong(id));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
