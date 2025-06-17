package org.organization.prices.application.mapper;

import org.organization.prices.application.dto.PriceResponseDto;
import org.organization.prices.domain.model.Price;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link PriceMapper} class.
 * Verifies the correct mapping between {@link Price} and {@link PriceResponseDto}.
 */
class PriceMapperTest {

    /**
     * Formatter for parsing and formatting dates in the pattern "dd/MM/yyyy HH:mm:ss".
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Instance of {@link PriceMapper} under test.
     */
    private final PriceMapper priceMapper = new PriceMapper();

    /**
     * Tests that the {@link PriceMapper#toResponse(Price)} method correctly maps all fields
     * from a {@link Price} object to a {@link PriceResponseDto}, including date fields formatted as strings.
     */
    @Test
    void toResponse_shouldMapAllFields() {
        // Arrange
        Price price = new Price();
        price.setProductId(1L);
        price.setBrandId(2L);
        price.setPriceList(3);
        LocalDateTime startDate = LocalDateTime.of(2024, 6, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 6, 30, 23, 59);
        price.setStartDate(startDate);
        price.setEndDate(endDate);
        price.setPrice(new BigDecimal("19.99"));

        // Act
        PriceResponseDto dto = priceMapper.toResponse(price);

        // Assert
        assertNotNull(dto, "DTO should not be null");
        assertEquals(price.getProductId(), dto.getProductId(), "Product ID should match");
        assertEquals(price.getBrandId(), dto.getBrandId(), "Brand ID should match");
        assertEquals(price.getPriceList(), dto.getPriceList(), "Price list should match");
        assertEquals(startDate.format(DATE_TIME_FORMATTER), dto.getStartDate(), "Start date should match formatted string");
        assertEquals(endDate.format(DATE_TIME_FORMATTER), dto.getEndDate(), "End date should match formatted string");
        assertEquals(price.getPrice(), dto.getPrice(), "Price should match");
    }

    /**
     * Tests that the {@link PriceMapper#toResponse(Price)} method returns {@code null} when the input {@link Price} is {@code null}.
     */
    @Test
    void toResponse_shouldReturnNull_whenPriceIsNull() {
        // Act
        PriceResponseDto dto = priceMapper.toResponse(null);

        // Assert
        assertNull(dto, "DTO should be null when input price is null");
    }

    /**
     * Tests that the {@link PriceMapper#toResponse(Price)} method correctly handles a {@link Price} object
     * with null date fields, ensuring the resulting {@link PriceResponseDto} has null date strings.
     */
    @Test
    void toResponse_shouldHandleNullDates() {
        // Arrange
        Price price = new Price();
        price.setProductId(1L);
        price.setBrandId(2L);
        price.setPriceList(3);
        price.setStartDate(null);
        price.setEndDate(null);
        price.setPrice(new BigDecimal("19.99"));

        // Act
        PriceResponseDto dto = priceMapper.toResponse(price);

        // Assert
        assertNotNull(dto, "DTO should not be null");
        assertEquals(price.getProductId(), dto.getProductId(), "Product ID should match");
        assertEquals(price.getBrandId(), dto.getBrandId(), "Brand ID should match");
        assertEquals(price.getPriceList(), dto.getPriceList(), "Price list should match");
        assertNull(dto.getStartDate(), "Start date should be null");
        assertNull(dto.getEndDate(), "End date should be null");
        assertEquals(price.getPrice(), dto.getPrice(), "Price should match");
    }

    /**
     * Tests that the {@link PriceMapper#toResponse(Price)} method correctly handles a {@link Price} object
     * with a null price, ensuring the resulting {@link PriceResponseDto} has a null price field.
     */
    @Test
    void toResponse_shouldHandleNullPrice() {
        // Arrange
        Price price = new Price();
        price.setProductId(1L);
        price.setBrandId(2L);
        price.setPriceList(3);
        LocalDateTime startDate = LocalDateTime.of(2024, 6, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 6, 30, 23, 59);
        price.setStartDate(startDate);
        price.setEndDate(endDate);
        price.setPrice(null);

        // Act
        PriceResponseDto dto = priceMapper.toResponse(price);

        // Assert
        assertNotNull(dto, "DTO should not be null");
        assertEquals(price.getProductId(), dto.getProductId(), "Product ID should match");
        assertEquals(price.getBrandId(), dto.getBrandId(), "Brand ID should match");
        assertEquals(price.getPriceList(), dto.getPriceList(), "Price list should match");
        assertEquals(startDate.format(DATE_TIME_FORMATTER), dto.getStartDate(), "Start date should match formatted string");
        assertEquals(endDate.format(DATE_TIME_FORMATTER), dto.getEndDate(), "End date should match formatted string");
        assertNull(dto.getPrice(), "Price should be null");
    }
}