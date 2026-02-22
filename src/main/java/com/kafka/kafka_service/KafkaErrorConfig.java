package com.kafka.kafka_service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(template);

        // No retries; publish to DLT immediately to avoid duplicate processing.
        DefaultErrorHandler handler =
                new DefaultErrorHandler(recoverer, new FixedBackOff(0L, 0));

        handler.setSeekAfterError(false);

        return handler;
    }
}
