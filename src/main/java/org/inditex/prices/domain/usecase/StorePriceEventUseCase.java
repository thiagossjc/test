package org.inditex.prices.domain.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inditex.prices.application.port.EventPublisherPort;
import org.inditex.prices.application.port.EventStorePort;
import org.inditex.prices.domain.model.Price;
import org.inditex.prices.domain.model.PriceEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Use case for storing and publishing price query events.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StorePriceEventUseCase {

    /**
     * Port responsible for persisting events.
     */
    private final EventStorePort eventStore;

    /**
     * Port responsible for publishing events (e.g., to Kafka, message bus, etc.).
     */
    private final EventPublisherPort eventPublisher;


    /**
     * Stores and publishes an event based on the given price and query date.
     *
     * @param price      the price result obtained from a query
     * @param queryDate  the date when the query was made
     * @return a Mono that completes when the event is stored and published,
     *         or empty if an error occurs
     */
    public Mono<Void> storeEvent(Price price, LocalDateTime queryDate) {
        if (price == null) {
            log.warn("Attempted to store a price event with null price");
            return Mono.empty();
        }

        PriceEvent event = buildPriceEvent(price, queryDate);

        return Mono.defer(() -> {
                    eventStore.storeEvent(event);
                    return eventPublisher.publishEvent(event);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(e -> {
                    log.error("Error storing or publishing event: productId={}, brandId={}, queryDate={}",
                            price.getProductId(), price.getBrandId(), queryDate, e);
                    return Mono.empty();
                });
    }

    private PriceEvent buildPriceEvent(Price price, LocalDateTime queryDate) {
        String eventType = price.getPrice().compareTo(BigDecimal.ZERO) == 0
                ? "ERROR_QUERY"
                : "PRICE_QUERY";

        return PriceEvent.builder()
                .eventType(eventType)
                .productId(price.getProductId())
                .brandId(price.getBrandId())
                .queryDate(queryDate)
                .price(price.getPrice())
                .priceList(price.getPriceList())
                .createdAt(LocalDateTime.now())
                .build();
    }
}