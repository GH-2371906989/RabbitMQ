package com.gu.Exchange05;

import com.gu.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class work02 {
    public static final String EXCHANGE_NAME ="logs";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();

        /**
         * 生成一个临时的队列 队列的名称是随机的
         * 当消费者断开和该队列的连接时 队列自动删除
         */
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName,EXCHANGE_NAME,"");

        System.out.println("C2等待接收消息,把接收到的消息打印在屏幕........... ");

        DeliverCallback deliverCallback = (consumerTag, delivery) ->
        {String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("控制台打印接收到的消息"+message);
        };
        /**-
         * 消费者消费消息
         * 1.消费哪个队列
         * 2.消费成功之后是否要自动应答 true 代表自动应答 false 手动应答
         * 3.消费者未成功消费的回调
         * 4.消费者取录消息的回调
         */
        channel.basicConsume(queueName,true,deliverCallback, CancelCallback->{});

    }
}
