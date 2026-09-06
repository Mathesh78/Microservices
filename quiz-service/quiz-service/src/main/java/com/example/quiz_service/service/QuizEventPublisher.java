package com.example.quiz_service.service;

import com.example.quiz_service.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class QuizEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public QuizEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishQuizSubmitted(Integer quizId, Integer score) {
        String event = "{\"quizId\":" + quizId + ",\"score\":" + score + "}";

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                event
        );
    }
}
