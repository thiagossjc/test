package org.organization.prices.domain.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a price entity in the domain model.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Price {
    /**
     * The identifier of the brand associated with the price.
     */
    @NotNull
    @Positive
    private Long brandId;

    /**
     * The start date and time when the price is effective.
     */
    @NotNull
    private LocalDateTime startDate;

    /**
     * The end date and time until the price is effective.
     */
    private LocalDateTime endDate;

    /**
     * The identifier of the price list applied.
     */
    @NotNull
    @Positive
    private Integer priceList;

    /**
     * The identifier of the product associated with the price.
     */
    @NotNull
    @Positive
    private Long productId;

    /**
     * The priority level of the price, used to determine precedence when multiple prices apply.
     */
    @NotNull
    private Integer priority;

    /**
     * The price value for the product.
     */
    @NotNull
    @Positive
    private BigDecimal price;

    /**
     * The currency code for the price (e.g., EUR, USD).
     */
    @NotNull
    private String currency;
}
