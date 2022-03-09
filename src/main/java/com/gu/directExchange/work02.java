package com.gu.directExchange;

import com.gu.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class work02 {
    private static final String EXCHANGE_NAME = "directExchanges";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        String QUEUE_NAME = "Q2";
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"error");

        DeliverCallback deliverCallback = (consumerTag, delivery)->{
            String message = new String(delivery.getBody(), "UTF-8");
            message=QUEUE_NAME+"接收绑定键:"+delivery.getEnvelope().getRoutingKey()+",消息:"+message;
            System.out.println(message);
        };
        channel.basicConsume(QUEUE_NAME,true,deliverCallback, CancelCallback->{});
    }
}
