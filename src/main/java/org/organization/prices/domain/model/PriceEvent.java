package org.organization.prices.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents an event related to a price query, used for auditing or logging purposes.
 */
@Data
@Builder
public class PriceEvent implements Serializable {

    /**
     * The identifier of the product that was queried.
     */
    @JsonProperty("productId")
    private Long productId;

    /**
     * The identifier of the brand related to the price query.
     */
    @JsonProperty("brandId")
    private Long brandId;

    /**
     * The price list ID that applied to the query.
     */
    @JsonProperty("priceList")
    private Integer priceList;

    /**
     * The date and time when the price was queried.
     */
    @JsonProperty("queryDate")
    private LocalDateTime queryDate;

    /**
     * The price value returned by the query.
     */
    @JsonProperty("price")
    private BigDecimal price;

    /**
     * The type of event, for example: "PRICE_FOUND" or "PRICE_NOT_FOUND".
     */
    @JsonProperty("eventType")
    private String eventType;

    /**
     * The timestamp when the event was created.
     */
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
}