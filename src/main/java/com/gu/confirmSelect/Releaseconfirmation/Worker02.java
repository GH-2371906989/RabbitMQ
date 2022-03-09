package com.gu.confirmSelect.Releaseconfirmation;

import com.gu.utils.RabbitMqUtils;
import com.gu.utils.SleepUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class Worker02 {
    //对了名称
    public static final String QUEUE_NAME = "durable_Releasecon_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();

        DeliverCallback deliverCallback =(consumerTag, message)->{
            String s = new String(message.getBody());
            SleepUtils.sleep(30);
            System.out.println("接收到消息:"+new String(message.getBody()));
            /**
             * 1.消息标记 tag
             * 2.是否批量应答未应答消息
             */
            //prefetchCount 不公平分发
            int prefetchCount = 5;
            channel.basicQos(prefetchCount);
            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);

        };
        //取消消费的一个回调接口 如在消费的时候队列被删除掉了
        CancelCallback cancelCallback=(consumerTag)->{
            System.out.println("消息消费被中断");
        };
        /**-
         * 消费者消费消息
         * 1.消费哪个队列
         * 2.消费成功之后是否要自动应答 true 代表自动应答 false 手动应答
         * 3.消费者未成功消费的回调
         * 4.消费者取录消息的回调
         */
        System.out.println("C2 消费者启动等待消费.................. ");
        //采用手动应答
        boolean autoAck=false;
        channel.basicConsume(QUEUE_NAME,autoAck,deliverCallback,cancelCallback);
    }
}
