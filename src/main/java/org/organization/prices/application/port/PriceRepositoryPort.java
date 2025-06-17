package org.organization.prices.application.port;

import org.organization.prices.domain.model.Price;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Interface for price repository operations.
 */
public interface PriceRepositoryPort {

    /**
     * Finds the applicable price for the given product, brand, and date.
     *
     * @param productId the product identifier
     * @param brandId the brand identifier
     * @param date the date for which to find the price
     * @return a Mono emitting the applicable price, or empty if none found
     */
    Mono<Price> findApplicablePrice(Long productId, Long brandId, LocalDateTime date);

    /**
     * Retrieves all prices.
     *
     * @return a Flux emitting all prices
     */
    Flux<Price> getAll();
}