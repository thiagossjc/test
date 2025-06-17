package org.organization.prices.application.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PriceRequestDto class.
 */
class PriceRequestDtoTest {

    /**
     * Test the constructor and getter methods.
     */
    @Test
    void testConstructorAndGetters() {
        Long productId = 1L;
        Long brandId = 2L;
        LocalDateTime date = LocalDateTime.of(2024, 6, 1, 12, 0);

        PriceRequestDto dto = new PriceRequestDto(productId, brandId, date);

        assertEquals(productId, dto.getProductId(), "Product ID should match");
        assertEquals(brandId, dto.getBrandId(), "Brand ID should match");
        assertEquals(date, dto.getDate(), "Date should match");
    }

    /**
     * Test the setter methods to ensure field values can be updated.
     */
    @Test
    void testSetters() {
        PriceRequestDto dto = new PriceRequestDto(1L, 2L, LocalDateTime.now());

        Long newProductId = 10L;
        Long newBrandId = 20L;
        LocalDateTime newDate = LocalDateTime.of(2025, 1, 1, 0, 0);

        dto.setProductId(newProductId);
        dto.setBrandId(newBrandId);
        dto.setDate(newDate);

        assertEquals(newProductId, dto.getProductId(), "Updated product ID should match");
        assertEquals(newBrandId, dto.getBrandId(), "Updated brand ID should match");
        assertEquals(newDate, dto.getDate(), "Updated date should match");
    }

    /**
     * Test the equals and hashCode implementations.
     */
    @Test
    void testEqualsAndHashCode() {
        LocalDateTime date = LocalDateTime.now();

        PriceRequestDto dto1 = new PriceRequestDto(1L, 2L, date);
        PriceRequestDto dto2 = new PriceRequestDto(1L, 2L, date);

        assertEquals(dto1, dto2, "DTOs with same values should be equal");
        assertEquals(dto1.hashCode(), dto2.hashCode(), "Hash codes should be equal for equal DTOs");
    }

    /**
     * Test the toString method to ensure it includes field values.
     */
    @Test
    void testToString() {
        PriceRequestDto dto = new PriceRequestDto(1L, 2L, LocalDateTime.of(2025, 6, 7, 10, 30));
        String toString = dto.toString();

        assertTrue(toString.contains("productId=1"), "toString should contain productId");
        assertTrue(toString.contains("brandId=2"), "toString should contain brandId");
        assertTrue(toString.contains("date=2025-06-07T10:30"), "toString should contain date");
    }
}