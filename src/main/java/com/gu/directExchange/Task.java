package com.gu.directExchange;

import com.gu.utils.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.nio.charset.StandardCharsets;

public class Task {
    private static final String EXCHANGE_NAME = "directExchanges";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMqUtils.getChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        channel.basicPublish(EXCHANGE_NAME,"error",null,"error".getBytes(StandardCharsets.UTF_8));
    }
}
