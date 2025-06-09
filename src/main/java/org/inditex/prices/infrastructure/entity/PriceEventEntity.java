package org.inditex.prices.infrastructure.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Reactive R2DBC entity representing a price event.
 */
@Data
@Table("PRICE_EVENTS")
public class PriceEventEntity {

    /**
     * Unique identifier for the price event.
     * Note: R2DBC requires an ID support auto-generation like webflux.
     */
    @Id
    private Long id;

    /**
     * Product identifier.
     */
    private Long productId;

    /**
     * Brand identifier.
     */
    private Long brandId;

    /**
     * Price list identifier.
     */
    private Integer priceList;

    /**
     * Date and time when the price query was made.
     */
    private LocalDateTime queryDate;

    /**
     * Price value.
     */
    private BigDecimal price;

    /**
     * Type of event.
     */
    private String eventType;

    /**
     * Timestamp when the event was created.
     */
    private LocalDateTime createdAt;
}
