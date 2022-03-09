package com.gu.UnfairDistribution.Releaseconfirmation;

import com.gu.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.MessageProperties;

import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/*
* 发布确认
*   1.单个确认
*   2.批量确认
*   3.异步确认
* */
public class Task {
    //对了名称
    public static final String QUEUE_NAME = "durable_Releasecon_queue";

    //批量发消息的个数
    public static final int MESSAGE_COUNT = 1000;
    public static void main(String[] args) throws Exception {
        // 1.单个确认  发布1000条单独确认消息,耗时659ms
//        publishmessagesingle();
        // 2.批量确认 发布1000条批量确认消息,耗时48ms
//        publishMessageBatch();
        // 3.异步确认  发布1000条异步确认消息,耗时51ms
        publishMessageAsyn();

    }

    private static void publishMessageAsyn() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //开启发布确认的方法
        channel.confirmSelect();
        //线程安全有序的哈希表 适用于高并发
        ConcurrentSkipListMap<Long,String> outstanding =
                new ConcurrentSkipListMap<>();

        //队列声明
        String queueName = UUID.randomUUID().toString();
        //创建队列
        channel.queueDeclare(queueName,true,false,false,null);

        //准备消息监听器 ,成功or失败
        //成功返回的消息
        ConfirmCallback ackCallback =(sequenceNumber, multiple)->{
            if (multiple){
                //删除已经确认的消息  剩下就是未确认
                ConcurrentNavigableMap<Long, String> map =
                        outstanding.headMap(sequenceNumber);
                map.clear();
            }else {
                outstanding.remove(sequenceNumber);

            }

            System.out.println("确认消息:"+sequenceNumber);
        };
        //失败返回的消息
        /*
        * 1.消息的表示
        * 2.是否为批量确认
        * */
        ConfirmCallback nackCallback = (sequenceNumber, multiple)->{
            //打印一下未确认的
            System.out.println("未确认的消息"+sequenceNumber);
        };
        channel.addConfirmListener(ackCallback,nackCallback);

        //开始时间
        long begin = System.currentTimeMillis();
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i+"";
            channel.basicPublish("",queueName,null,message.getBytes());
            //在此处记录下所有发送的消息总数
            outstanding.put(channel.getNextPublishSeqNo(),message);

        }

        //结束时间
        long end = System.currentTimeMillis();

        System.out.println("发布"+MESSAGE_COUNT+"条异步确认消息,耗时"+(end-begin)+"ms");

    }

    private static void publishMessageBatch() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //开启发布确认的方法
        channel.confirmSelect();
        //队列声明
        String queueName = UUID.randomUUID().toString();
        //创建队列
        channel.queueDeclare(queueName,true,false,false,null);
        //开始时间
        long begin = System.currentTimeMillis();

        //批量确认消息的大小
        int BatchSize = 100;
        //批量发消息  单个
        for (int i = MESSAGE_COUNT; i > 0; i--) {
            String message = i+"";
            channel.basicPublish("",queueName,null,message.getBytes());
            //单个消息马上进行发布确认
            if (1%BatchSize==0){
                channel.waitForConfirms();
            }
        }

        //结束时间
        long end = System.currentTimeMillis();

        System.out.println("发布"+MESSAGE_COUNT+"条批量确认消息,耗时"+(end-begin)+"ms");
    }

    //单个确认
    private static void publishmessagesingle() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //开启发布确认的方法
        channel.confirmSelect();
        //队列声明
        String queueName = UUID.randomUUID().toString();
        //创建队列
        channel.queueDeclare(queueName,true,false,false,null);
        //开始时间
        long begin = System.currentTimeMillis();

        //批量发消息  单个
        for (int i = MESSAGE_COUNT; i > 0; i--) {
            String message = i+"";
            channel.basicPublish("",queueName,null,message.getBytes());
            //单个消息马上进行发布确认
            boolean wait = channel.waitForConfirms();
            if (wait){
                System.out.println(i+"消息发送成功");
            }
        }
        //结束时间
        long end = System.currentTimeMillis();

        System.out.println("发布"+MESSAGE_COUNT+"条单独确认消息,耗时"+(end-begin)+"ms");
    }

}
