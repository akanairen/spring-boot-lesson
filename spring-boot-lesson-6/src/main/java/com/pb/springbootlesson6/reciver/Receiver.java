package com.pb.springbootlesson6.reciver;

import com.pb.springbootlesson6.domain.User;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 消息消费者
 */
@Component
public class Receiver {

    /*@RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = "queue-1", durable = "true"),
                    exchange = @Exchange(value = "topicExchange1", durable = "true", type = "topic"),
                    key = "key.1"
            )
    })*/
    @RabbitListener(queues = "say.hello.queue")
    public void handSayHello(String msg, Channel channelToUse) {
        System.out.println(Thread.currentThread().getName() + "\t接受到消息： " + msg);
        System.out.println(channelToUse);
    }

    // @RabbitListener(bindings = {
    //         @QueueBinding(
    //                 value = @Queue(value = "send.user.queue", durable = "true"),
    //                 exchange = @Exchange(value = "topicExchange", durable = "true", type = "topic")
    //         )
    // })
    @RabbitListener(queues = "send.user.queue")
    public void handSendUser(User user, Message message, Channel channelToUse) {
        System.out.println(Thread.currentThread().getName() + "\t" + message.getMessageProperties().getConsumerQueue() + "\t接受到消息： " + user);
        System.out.println(channelToUse);
        try {
            // deliveryTag：该消息的index
            // multiple：是否批量. true:将一次性ack所有小于deliveryTag的消息,false:确认当前消息
            // channelToUse.basicAck(message.getMessageProperties().getDeliveryTag(), false);

            // deliveryTag：该消息的index
            // multiple：是否批量.true:将一次性拒绝所有小于deliveryTag的消息。
            // requeue：被拒绝的是否重新入队列
            // channelToUse.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}