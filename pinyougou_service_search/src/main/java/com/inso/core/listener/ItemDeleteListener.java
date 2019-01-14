package com.inso.core.listener;

import com.inso.core.service.itemsearch.ItemSearchService;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

public class ItemDeleteListener implements MessageListener {
    @Resource
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {

        try {
            //获取消息
            ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) message;
            String id = activeMQTextMessage.getText();
            System.out.println("service-search，删除索引了，商品id为：" + id);
            //消费消息
            itemSearchService.deleteItemFormSolr(Long.parseLong(id));

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
