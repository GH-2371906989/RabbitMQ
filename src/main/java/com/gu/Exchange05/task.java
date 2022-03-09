package com.gu.Exchange05;

import com.gu.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;

import java.util.Scanner;

public class task {
    public static final String EXCHANGE_NAME ="logs";
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();

        /**
         * 声明一个 exchange
         * 1.exchange 的名称
         * 2.exchange 的类型
         */
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");

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
            channel.basicPublish(EXCHANGE_NAME,"",null,message.getBytes("UTF-8"));
            System.out.println("发送消息完成:"+message);
        }
    }
}
