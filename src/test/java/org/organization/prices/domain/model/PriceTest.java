package org.organization.prices.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Price} domain model.
 */
class PriceTest {

    /**
     * Tests the getter and setter methods for all fields.
     */
    @Test
    void testGettersAndSetters() {
        Price price = new Price();

        Long brandId = 1L;
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 23, 59);
        Integer priceList = 100;
        Long productId = 10L;
        Integer priority = 5;
        BigDecimal priceValue = new BigDecimal("99.99");
        String currency = "EUR";

        price.setBrandId(brandId);
        price.setStartDate(startDate);
        price.setEndDate(endDate);
        price.setPriceList(priceList);
        price.setProductId(productId);
        price.setPriority(priority);
        price.setPrice(priceValue);
        price.setCurrency(currency);

        assertEquals(brandId, price.getBrandId());
        assertEquals(startDate, price.getStartDate());
        assertEquals(endDate, price.getEndDate());
        assertEquals(priceList, price.getPriceList());
        assertEquals(productId, price.getProductId());
        assertEquals(priority, price.getPriority());
        assertEquals(priceValue, price.getPrice());
        assertEquals(currency, price.getCurrency());
    }
}
