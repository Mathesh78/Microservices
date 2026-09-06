package com.example.notification_service.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE = "quiz.exchange";
    public static final String QUEUE = "quiz.submitted.queue";
    public static final String ROUTING_KEY = "quiz.submitted";

    @Bean
    public TopicExchange quizExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue quizSubmittedQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Binding quizSubmittedBinding(Queue quizSubmittedQueue,
                                        TopicExchange quizExchange) {
        return BindingBuilder.bind(quizSubmittedQueue)
                .to(quizExchange)
                .with(ROUTING_KEY);
    }
}
