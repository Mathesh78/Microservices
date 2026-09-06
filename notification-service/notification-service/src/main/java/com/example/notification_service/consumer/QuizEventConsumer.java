package com.example.notification_service.consumer;

import com.example.notification_service.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class QuizEventConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consume(String event) {
        System.out.println("============================");
        System.out.println("Quiz submitted event received");
        System.out.println(event);
        System.out.println("Send email/SMS notification here");
        System.out.println("============================");
    }
}
