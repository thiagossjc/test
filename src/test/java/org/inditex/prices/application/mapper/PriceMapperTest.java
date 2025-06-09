package org.inditex.prices.application.mapper;

import org.inditex.prices.application.dto.PriceResponseDto;
import org.inditex.prices.domain.model.Price;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link PriceMapper} class.
 */
class PriceMapperTest {

    private final PriceMapper priceMapper = new PriceMapper();

    /**
     * Tests that the {@code toResponse} method correctly maps all fields
     * from a {@link Price} object to a {@link PriceResponseDto}.
     */
    @Test
    void toResponse_shouldMapAllFields() {
        // Arrange
        Price price = new Price();
        price.setProductId(1L);
        price.setBrandId(2L);
        price.setPriceList(3);
        price.setStartDate(LocalDateTime.of(2024, 6, 1, 0, 0));
        price.setEndDate(LocalDateTime.of(2024, 6, 30, 23, 59));
        price.setPrice(new BigDecimal("19.99"));

        // Act
        PriceResponseDto dto = priceMapper.toResponse(price);

        // Assert
        assertNotNull(dto, "DTO should not be null");
        assertEquals(price.getProductId(), dto.getProductId());
        assertEquals(price.getBrandId(), dto.getBrandId());
        assertEquals(price.getPriceList(), dto.getPriceList());
        assertEquals(price.getStartDate(), dto.getStartDate());
        assertEquals(price.getEndDate(), dto.getEndDate());
        assertEquals(price.getPrice(), dto.getPrice());
    }

    /**
     * Tests that the {@code toResponse} method returns {@code null} when the input {@link Price} is {@code null}.
     */
    @Test
    void toResponse_shouldReturnNull_whenPriceIsNull() {
        // Act
        PriceResponseDto dto = priceMapper.toResponse(null);

        // Assert
        assertNull(dto, "DTO should be null when input price is null");
    }
}
