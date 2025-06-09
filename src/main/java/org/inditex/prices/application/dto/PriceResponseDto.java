package org.inditex.prices.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) representing a response with price information.
 * Contains the product ID, brand ID, price list, start and end dates, and the price value.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PriceResponseDto {

    /**
     * The identifier of the product associated with the price.
     */
    @NotNull
    @Positive
    private Long productId;

    /**
     * The identifier of the brand associated with the product.
     */
    @NotNull
    @Positive
    private Long brandId;

    /**
     * The identifier of the price list applied.
     */
    @NotNull
    @Positive
    private Integer priceList;

    /**
     * The start date and time when the price is effective.
     */
    @NotNull
    private String startDate;

    /**
     * The end date and time until the price is effective.
     */
    private String endDate;

    /**
     * The price value for the product.
     */
    @NotNull
    @Positive
    private BigDecimal price;
}
