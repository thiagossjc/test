package org.organization.prices.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link PriceEvent} domain model.
 */
class PriceEventTest {

    /**
     * Tests the getter and setter methods for all fields.
     */
    @Test
    void testGettersAndSetters() {

        LocalDateTime queryDate = LocalDateTime.of(2024, 6, 1, 12, 0);

        PriceEvent priceEvent = PriceEvent.builder()
                .productId(35455L)
                .brandId(1L)
                .priceList(0)
                .queryDate(queryDate)
                .price(new BigDecimal("35.50"))
                .eventType("PRICE_QUERY")
                .createdAt(LocalDateTime.now())
                .build();


        Long productId = 35455L;
        Long brandId = 1L;
        Integer priceList = 0;
        BigDecimal price = new BigDecimal("35.50");
        String eventType = "PRICE_FOUND";
        LocalDateTime createdAt = LocalDateTime.now();

        priceEvent.setProductId(productId);
        priceEvent.setBrandId(brandId);
        priceEvent.setPriceList(priceList);
        priceEvent.setQueryDate(queryDate);
        priceEvent.setPrice(price);
        priceEvent.setEventType(eventType);
        priceEvent.setCreatedAt(createdAt);

        assertEquals(productId, priceEvent.getProductId());
        assertEquals(brandId, priceEvent.getBrandId());
        assertEquals(priceList, priceEvent.getPriceList());
        assertEquals(queryDate, priceEvent.getQueryDate());
        assertEquals(price, priceEvent.getPrice());
        assertEquals(eventType, priceEvent.getEventType());
        assertEquals(createdAt, priceEvent.getCreatedAt());
    }
}
