package org.organization.prices.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing a request for price information.
 * Contains the product ID, brand ID, and date for which the price is requested.
 */
@Data
@AllArgsConstructor
public class PriceRequestDto {
    /**
     * The identifier of the product for which the price is requested.
     */
    private Long productId;

    /**
     * The identifier of the brand associated with the product.
     */
    private Long brandId;

    /**
     * The date and time for which the price is requested.
     */
    private LocalDateTime date;
}
