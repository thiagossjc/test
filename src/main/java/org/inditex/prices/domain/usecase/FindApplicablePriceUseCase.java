package org.inditex.prices.domain.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inditex.prices.domain.model.Price;
import org.inditex.prices.application.port.PriceRepositoryPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Use case for finding the applicable price.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class FindApplicablePriceUseCase {

    /**
     * Repository port for accessing price data.
     */
    private final PriceRepositoryPort priceRepositoryPort;

    /**
     * Finds the price with the highest priority for a product, brand, and date.
     *
     * @param productId the product ID
     * @param brandId   the brand ID
     * @param date      the application date
     * @return the applicable price
     */
    public Mono<Price> findApplicablePrice(Long productId, Long brandId, LocalDateTime date) {

            return priceRepositoryPort.findApplicablePrice(productId, brandId, date)
                    .doOnError(e -> log.error("Error querying price for productId={}, brandId={}, date={}: {}", productId, brandId, date, e.getMessage()))
                    .log("FindApplicablePriceUseCase.findPrice");
    }
}