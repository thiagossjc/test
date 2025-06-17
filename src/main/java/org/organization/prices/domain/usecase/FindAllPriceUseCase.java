package org.organization.prices.domain.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.organization.prices.application.port.PriceRepositoryPort;
import org.organization.prices.domain.model.Price;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Use case for finding the applicable price.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class FindAllPriceUseCase {

    /**
     * Repository port for accessing price data.
     */
    private final PriceRepositoryPort priceRepositoryPort;

    /**
     * Finds the all prices.
     *
     * @return the applicable price
     */
    public Flux<Price> findAllPrice() {

            return priceRepositoryPort.getAll()
                    .doOnError(e -> log.error("Error querying price for all prices - {}", e.getMessage()))
                    .log("FindApplicablePriceUseCase.findPrice");
    }
}