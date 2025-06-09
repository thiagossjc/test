package org.inditex.prices.infrastructure.adapter.out.repository;

import org.inditex.prices.infrastructure.entity.PriceEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Reactive repository for managing {@link PriceEntity} instances.
 * <p>
 * Provides reactive CRUD operations and custom queries to retrieve prices
 * based on product, brand, and date constraints.
 * </p>
 */
@Repository
public interface PriceRepository extends ReactiveCrudRepository<PriceEntity, Long> {

    /**
     * Finds price entities by product ID, brand ID, and date range,
     * ordered by priority descending, limited to one result.
     *
     * @param productId the product identifier
     * @param brandId   the brand identifier
     * @param date1     the start date constraint (usually the application date)
     * @param date2     the end date constraint (usually the application date)
     * @return a Flux emitting the matching price entities (max one, ordered by priority)
     */
    @Query("SELECT * FROM prices WHERE product_id = :productId AND brand_id = :brandId " +
            "AND start_date <= :date1 AND end_date >= :date2 ORDER BY priority DESC LIMIT 1")
    Mono<PriceEntity> findByProductIdAndBrandIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
            Long productId, Long brandId, LocalDateTime date1, LocalDateTime date2
    );

    /**
     * Finds the applicable price entity for the given product, brand, and date.
     * <p>
     * This method uses the custom query to fetch the top priority price valid for the specified date.
     * </p>
     *
     * @param productId the product identifier
     * @param brandId   the brand identifier
     * @param date      the date to evaluate
     * @return a Mono emitting the applicable price entity, or empty if none found
     */
    default Mono<PriceEntity> findApplicablePrice(Long productId, Long brandId, LocalDateTime date) {
        return findByProductIdAndBrandIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
                productId, brandId, date, date
        );
    }

    /**
     * Returns all price entities.
     *
     * @return a Flux emitting all {@link PriceEntity} objects
     */
    default Flux<PriceEntity> findAllPrices() {
        return findAll();
    }
}
