package org.inditex.prices.infrastructure.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.inditex.prices.domain.model.PriceEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka reactive configuration class.
 * <p>
 * Configures a {@link KafkaSender} bean for sending {@link PriceEvent} messages
 * with reactive Kafka support using Reactor Kafka.
 * </p>
 */
@Configuration
public class KafkaReactiveConfig {

    /**
     * Kafka bootstrap servers address.
     * <p>
     * Injected from application properties via {@code spring.kafka.bootstrap-servers}.
     * </p>
     */
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Creates and configures a reactive {@link KafkaSender} bean for sending
     * messages with key of type {@link String} and value of type {@link PriceEvent}.
     * <p>
     * Uses String serializer for keys and Spring Kafka's JSON serializer for values.
     * </p>
     *
     * @return configured reactive {@link KafkaSender} instance
     */
    @Bean
    public KafkaSender<String, PriceEvent> kafkaSender() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // Use Spring Kafka's JSON serializer, compatible with reactive Kafka here
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        SenderOptions<String, PriceEvent> senderOptions = SenderOptions.create(props);

        return KafkaSender.create(senderOptions);
    }
}
