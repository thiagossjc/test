package org.inditex.prices.infrastructure.adapter.out.repository;

import lombok.RequiredArgsConstructor;
import org.inditex.prices.domain.execption.PriceNotFoundException;
import org.inditex.prices.domain.model.Price;
import org.inditex.prices.application.port.PriceRepositoryPort;
import org.inditex.prices.infrastructure.mapper.PriceEntityMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Adapter for price repository operations.
 */
@Component
@RequiredArgsConstructor
public class PriceRepositoryAdapter implements PriceRepositoryPort {

    /**
     * Repository for accessing price entities from the database.
     */
    private final PriceRepository priceRepository;

    /**
     * Mapper to convert between {@link org.inditex.prices.infrastructure.entity.PriceEntity} and {@link Price} domain model.
     */
    private final PriceEntityMapper priceMapper;

    /**
     * Finds the applicable price for a given product, brand, and application date.
     * <p>
     * Retrieves a price entity from the repository, maps it to the domain model, and returns it
     * as a {@link Mono}. If no price is found, a {@link PriceNotFoundException} is thrown.
     * </p>
     *
     * @param productId the ID of the product
     * @param brandId the ID of the brand
     * @param date the application date to evaluate the price
     * @return a {@link Mono} emitting the applicable {@link Price}
     * @throws PriceNotFoundException if no price is found for the given criteria
     */

    /**
     * Finds the applicable price for a product, brand, and date.
     *
     * @param productId the product ID
     * @param brandId the brand ID
     * @param date the application date
     * @return the applicable price
     */
    public Mono<Price> findApplicablePrice(Long productId, Long brandId, LocalDateTime date) {
        return priceRepository.findApplicablePrice(productId, brandId, date)
                .map(priceMapper::toDomain)
                .switchIfEmpty(Mono.error(new PriceNotFoundException("Price not found")));
    }

    /**
     * Retrieves all prices from the repository.
     * <p>
     * Fetches all price entities, maps them to the domain model, and returns them as a {@link Flux}.
     * If no prices are found, a {@link PriceNotFoundException} is thrown.
     * </p>
     *
     * @return a {@link Flux} emitting all {@link Price} objects
     * @throws PriceNotFoundException if no prices are found
     */
    @Override
    public Flux<Price> getAll() {
        return priceRepository.findAllPrices().map(priceMapper::toDomain)
                .switchIfEmpty(Flux.error(new PriceNotFoundException("Prices not found")));
    }
}