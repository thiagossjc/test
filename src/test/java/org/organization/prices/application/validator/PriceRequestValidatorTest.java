package org.organization.prices.application.validator;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link PriceRequestValidator} class.
 * <p>
 * These tests cover validation of date, time, product ID, and brand ID inputs.
 * </p>
 */
class PriceRequestValidatorTest {

    /**
     * Tests that a valid date, time, productId, and brandId
     * produce a correct {@link LocalDateTime} instance.
     */
    @Test
    void validate_withValidInput_shouldReturnLocalDateTime() {
        String date = "14/06/2020";
        String time = "15:30";
        Long productId = 1L;
        Long brandId = 2L;

        LocalDateTime result = PriceRequestValidator.validate(date, time, productId, brandId);

        assertNotNull(result);
        assertEquals(2020, result.getYear());
        assertEquals(6, result.getMonthValue());
        assertEquals(14, result.getDayOfMonth());
        assertEquals(15, result.getHour());
        assertEquals(30, result.getMinute());
    }

    /**
     * Tests that passing a null date throws an IllegalArgumentException
     * with the expected message.
     */
    @Test
    void validate_withNullDate_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            PriceRequestValidator.validate(null, "12:00", 1L, 1L));
        assertEquals("Date must not be blank", exception.getMessage());
    }

    /**
     * Tests that passing a blank time string throws an IllegalArgumentException
     * with the expected message.
     */
    @Test
    void validate_withBlankTime_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            PriceRequestValidator.validate("01/01/2020", " ", 1L, 1L));
        assertEquals("Time must not be blank", exception.getMessage());
    }

    /**
     * Tests that passing a non-positive productId throws an IllegalArgumentException
     * with the expected message.
     */
    @Test
    void validate_withInvalidProductId_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            PriceRequestValidator.validate("01/01/2020", "12:00", 0L, 1L));
        assertEquals("Product ID must be a positive number", exception.getMessage());
    }

    /**
     * Tests that passing a non-positive brandId throws an IllegalArgumentException
     * with the expected message.
     */
    @Test
    void validate_withInvalidBrandId_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            PriceRequestValidator.validate("01/01/2020", "12:00", 1L, -1L));
        assertEquals("Brand ID must be a positive number", exception.getMessage());
    }

    /**
     * Tests that passing an invalid date format throws an IllegalArgumentException
     * with the expected message.
     */
    @Test
    void validate_withInvalidDateFormat_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            PriceRequestValidator.validate("2020-06-14", "12:00", 1L, 1L));
        assertEquals("Invalid date or time format. Expected dd/MM/yyyy and HH:mm", exception.getMessage());
    }

    /**
     * Tests that passing an invalid time format throws an IllegalArgumentException
     * with the expected message.
     */
    @Test
    void validate_withInvalidTimeFormat_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            PriceRequestValidator.validate("14/06/2020", "12:00:00", 1L, 1L));
        assertEquals("Invalid date or time format. Expected dd/MM/yyyy and HH:mm", exception.getMessage());
    }
}
