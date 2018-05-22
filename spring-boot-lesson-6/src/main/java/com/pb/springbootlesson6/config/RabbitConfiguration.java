package com.pb.springbootlesson6.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


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
