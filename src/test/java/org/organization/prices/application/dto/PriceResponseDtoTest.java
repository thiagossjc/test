package org.organization.prices.application.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the PriceResponseDto class.
 */
class PriceResponseDtoTest {

    private final Long productId = 1L;
    private final Long brandId = 2L;
    private final Integer priceList = 3;
    private final LocalDateTime startDate = LocalDateTime.of(2024, 6, 1, 0, 0);
    private final LocalDateTime endDate = LocalDateTime.of(2024, 6, 30, 23, 59);
    private final BigDecimal price = new BigDecimal("19.99");


    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");


    /**
     * Test the constructor and getter methods.
     */
    @Test
    void toStringShouldContainFormattedDates() {
        String formattedStart = startDate.format(FORMATTER);
        String formattedEnd = endDate.format(FORMATTER);

        PriceResponseDto dto = new PriceResponseDto(productId, brandId, priceList, formattedStart, formattedEnd, price);
        String output = dto.toString();

        assertTrue(output.contains("startDate=" + formattedStart));
        assertTrue(output.contains("endDate=" + formattedEnd));
    }

}
