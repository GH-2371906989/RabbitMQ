package com.gu.confirmSelect.Releaseconfirmation;

import com.gu.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.util.Scanner;

public class Task {
    //对了名称
    public static final String QUEUE_NAME = "durable_Releasecon_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();

        //开启发布确认的方法
        channel.confirmSelect();
        /**
         * 生成一个队列
         * 1.队列名称
         * 2.队列里面的消息是否持久化 默认消息存储在内存中
         * 3.该队列是否只供一个消费者进行消费 是否进行共享 true 可以多个消费者消费
         * 4.是否自动删除 最后一个消费者端开连接以后 该队列是否自动删除 true 自动删除
         * 5.其他参数
         */
        //队列durable 持久化
        boolean durable = true;
        channel.queueDeclare(QUEUE_NAME,durable,false,false,null);

        //控制台输入
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String message = scanner.next();
            /**
             * 发送一个消息
             * 1.发送到那个交换机
             * 2.路由的 key 是哪个
             * 3.其他的参数信息
             * 4.发送消息的消息体
             */
            //消息持久化
            channel.basicPublish("",QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN
                    ,message.getBytes());
            System.out.println("发送消息完成:"+message);
        }

    }
}
