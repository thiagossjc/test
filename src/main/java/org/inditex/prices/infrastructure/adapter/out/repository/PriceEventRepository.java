package org.inditex.prices.infrastructure.adapter.out.repository;

import org.inditex.prices.infrastructure.entity.PriceEventEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Reactive repository interface for managing {@link PriceEventEntity} instances.
 * <p>
 * Provides reactive CRUD operations as well as custom queries related to price events.
 * </p>
 */
@Repository
public interface PriceEventRepository extends ReactiveCrudRepository<PriceEventEntity, Long> {

    /**
     * Retrieves all price event entities associated with the specified product ID.
     *
     * @param productId the unique identifier of the product
     * @return a {@link Flux} emitting all {@link PriceEventEntity} objects matching the product ID
     */
    Flux<PriceEventEntity> findByProductId(Long productId);
}
