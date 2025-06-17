package org.organization.prices.infrastructure.adapter.out.kafka;

import lombok.extern.slf4j.Slf4j;
import org.organization.prices.application.port.CircuitBreakerPort;
import org.organization.prices.application.port.EventPublisherPort;
import org.organization.prices.domain.execption.PriceNotFoundException;
import org.organization.prices.domain.model.PriceEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.util.UUID;

/**
 * Adapter implementation of {@link EventPublisherPort} that publishes PriceEvent messages to Kafka.
 * <p>
 * This component sends events asynchronously using Reactor Kafka and applies a circuit breaker pattern
 * to handle failures gracefully. It can be enabled or disabled via configuration.
 * </p>
 */
@Component
@Slf4j
public class KafkaEventPublisherAdapter implements EventPublisherPort {

    /**
     * Kafka sender used to send PriceEvent messages.
     */
    private final KafkaSender<String, PriceEvent> kafkaSender;

    /**
     * Circuit breaker for handling Kafka failures.
     */
    private final CircuitBreakerPort circuitBreakerPort;

    /**
     * Flag to enable or disable Kafka publishing via configuration property.
     */
    private boolean kafkaEnabled;

    /**
     * Kafka topic to which price events are published.
     */
    private static final String TOPIC = "priceTopic";

    /**
     * Circuit breaker instance name used for Kafka publishing.
     */
    private static final String CIRCUIT_BREAKER_NAME = "kafkaPublisher";

    /**
     * Constructs a new KafkaEventPublisherAdapter.
     *
     * @param kafkaSender       the Kafka sender instance
     * @param circuitBreakerPort the circuit breaker port for fault tolerance
     * @param kafkaEnabled       flag indicating whether Kafka publishing is enabled (from config)
     */
    public KafkaEventPublisherAdapter(KafkaSender<String, PriceEvent> kafkaSender,
                                      CircuitBreakerPort circuitBreakerPort,
                                      @Value("${spring.kafka.enabled:true}") boolean kafkaEnabled) {
        this.kafkaSender = kafkaSender;
        this.circuitBreakerPort = circuitBreakerPort;
        this.kafkaEnabled = kafkaEnabled;
    }

    /**
     * Publishes a {@link PriceEvent} to the configured Kafka topic asynchronously.
     * <p>
     * If Kafka publishing is disabled, this method returns an empty Mono immediately.
     * The publishing process is wrapped with a circuit breaker to manage failures.
     * </p>
     *
     * @param event the price event to publish
     * @return a {@link Mono} signaling completion of the publish operation
     */
    @Override
    public Mono<Void> publishEvent(PriceEvent event) {
        if (!kafkaEnabled) {
            return Mono.empty();
        }

        // Creates a SenderRecord with key, value and a UUID correlation id (optional)
        SenderRecord<String, PriceEvent, UUID> record = SenderRecord.create(
                TOPIC,
                null,
                null,
                event.getProductId().toString(),
                event,
                UUID.randomUUID()
        );

        return circuitBreakerPort.executeCircuitBreaker(
                CIRCUIT_BREAKER_NAME,
                kafkaSender.send(Mono.just(record))
                        .then()
                        .onErrorResume(this::publishEventFallback),
                PriceNotFoundException.class
        );
    }

    /**
     * Fallback method executed when Kafka publishing fails.
     * <p>
     * Logs a warning and returns an empty Mono, effectively swallowing the error.
     * </p>
     *
     * @param t the throwable error that occurred during publishing
     * @return an empty {@link Mono} indicating fallback completion
     */
    private Mono<Void> publishEventFallback(Throwable t) {
        log.warn("Kafka unavailable, event not published. Error: {}", t.getMessage());
        return Mono.empty();
    }
}