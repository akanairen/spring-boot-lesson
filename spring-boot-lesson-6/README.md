### Spring Boot 集成 RabbitMQ 消息队列

#### 1、Windows 环境安装 ```RabbitMQ```

[下载地址](http://www.rabbitmq.com/install-windows.html)

安装完毕后，安装 ```RabbitMQ-Plugins ``` ，打开 CMD 控制台，输入

```basic
rabbitmq-plugins enable rabbitmq_management
```

重启服务，打开浏览器访问：http://localhost:15672 

#### 2、引入 ```POM``` 依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

#### 3、配置 ```properties```

```properties
# RabbitMQ 配置
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

# 配置发布消息确认回调
spring.rabbitmq.publisher-confirms=true
# 配置发布消息确认回调
spring.rabbitmq.publisher-returns=true
# 启用强制消息，true: 如果消息不可达到，将返回一个不可达的路由消息
spring.rabbitmq.template.mandatory=true
```

#### 4、编写 ```RabbitConfiguration``` 

```java
@Configuration
public class RabbitConfiguration {

    @Bean
    public Queue sayHelloQueue() {
        return new Queue("say.hello.queue");
    }

    @Bean
    public Queue sendUserQueue() {
        return new Queue("send.user.queue");
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("topic.exchange");
    }

    @Bean
    Binding sayHelloBinding() {
        return BindingBuilder.bind(sayHelloQueue()).to(topicExchange()).with("key.1");
    }

    @Bean
    Binding sendUserBinding() {
        return BindingBuilder.bind(sendUserQueue()).to(topicExchange()).with("key.2");
    }

}
```

#### 5、编写消息生产者 ```Sender``` 

```java
import com.pb.springbootlesson6.domain.User;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 消息生产者
 */
@Component
public class Sender implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    /**
     * 发送消息接口
     * @param msg
     */
    public void send(String msg) {
        rabbitTemplate.convertAndSend("topic.exchange", "key.1", msg);
    }

    public void send(User user) {
        rabbitTemplate.convertAndSend("topic.exchange", "key.2", user);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            System.out.println("消息发送成功： " + correlationData);
        } else {
            System.out.println("消息发送失败： " + cause);
        }
    }

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText,
                                String exchange, String routingKey) {
        System.out.println(message.getMessageProperties().getCorrelationIdString() + " 发送失败");
    }
}
```

#### 6、编写消息消费者

```java
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
```

#### 7、编写 ```Controller``` 发送消息

```java
@RestController
@RequestMapping("/msg/")
public class MessageController {

    @Autowired
    private Sender sender;

    @PostMapping
    public String send(String msg) {
        sender.send(msg);
        return msg;
    }

    @PostMapping("user")
    public User sendUser(@RequestBody User user) {
        sender.send(user);
        return user;
    }
}
```

> 参考：
>
> [RabbitMQ系列(一)：Windows下RabbitMQ安装及入门](https://blog.csdn.net/hzw19920329/article/details/53156015) 
>
> [第四十一章： 基于SpringBoot & RabbitMQ完成DirectExchange分布式消息消费](https://www.jianshu.com/p/6b62a0ed2491)
>
> [springboot+rabbitmq整合示例程](https://www.cnblogs.com/boshen-hzb/p/6841982.html)