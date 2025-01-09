package com.bej03.notis.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RabbitMQConfig {

    @Primary
    @Bean
    public Queue myQueue() {
        return new Queue("bejo3-queue", true);  // Declare your existing queue
    }

    @Bean
    public Queue batchIdQueue() {
        return new Queue("batch-id-queue", true);  // Declare the batch queue
    }
}
