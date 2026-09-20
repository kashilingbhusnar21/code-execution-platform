package com.codeplatform.code_executor.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String CODE_SUBMISSION_QUEUE = "code.submission.queue";
    public static final String CODE_RESULT_QUEUE = "code.result.queue";
    public static final String CODE_EXCHANGE = "code.exchange";
    public static final String CODE_SUBMISSION_ROUTING_KEY = "code.submission";
    public static final String CODE_RESULT_ROUTING_KEY = "code.result";

    @Bean
    public Queue codeSubmissionQueue() {
        return new Queue(CODE_SUBMISSION_QUEUE, true);
    }

    @Bean
    public Queue codeResultQueue() {
        return new Queue(CODE_RESULT_QUEUE, true);
    }

    @Bean
    public TopicExchange codeExchange() {
        return new TopicExchange(CODE_EXCHANGE);
    }

    @Bean
    public Binding codeSubmissionBinding() {
        return BindingBuilder.bind(codeSubmissionQueue())
                .to(codeExchange())
                .with(CODE_SUBMISSION_ROUTING_KEY);
    }

    @Bean
    public Binding codeResultBinding() {
        return BindingBuilder.bind(codeResultQueue())
                .to(codeExchange())
                .with(CODE_RESULT_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}
