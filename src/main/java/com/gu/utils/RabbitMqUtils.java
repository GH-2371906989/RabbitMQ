package com.gu.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMqUtils {
    //得到一个连接的 channel
    public static Channel getChannel() throws Exception{
//创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //端口号  用户密码
        factory.setHost("192.168.136.129");
        factory.setUsername("admin");
        factory.setPassword("123");
        //创建链接
        Connection connection = factory.newConnection();
        //获取信道
        Channel channel = connection.createChannel();
        return channel;
    }
}
