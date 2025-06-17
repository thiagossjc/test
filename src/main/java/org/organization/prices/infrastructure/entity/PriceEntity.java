package org.organization.prices.infrastructure.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA entity representing a price record.
 */
@Table(name = "PRICES")
@Data
public class PriceEntity {

    /**
     * Brand identifier.
     */
    @Id
    private Long id;

    /**
     * Brand identifier.
     */
    private Long brandId;

    /**
     * Price start date and time.
     */
    private LocalDateTime startDate;

    /**
     * Price end date and time.
     */
    private LocalDateTime endDate;

    /**
     * Price list identifier.
     */
    private Integer priceList;

    /**
     * Product identifier.
     */
    private Long productId;

    /**
     * Priority of the price.
     */
    private Integer priority;

    /**
     * Price value.
     */
    private BigDecimal price;

    /**
     * Currency code of the price.
     */
    private String currency;
}