package org.organization.prices.application.validator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Validator class for price request parameters.
 * <p>
 * Validates date, time, product ID, and brand ID inputs and converts
 * date and time strings to a {@link LocalDateTime} object.
 * </p>
 */
public class PriceRequestValidator {

    /**
     * Date formatter expecting the pattern "dd/MM/yyyy".
     */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Time formatter expecting the pattern "HH:mm".
     */
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Validates the input parameters and returns a {@link LocalDateTime} instance.
     * <p>
     * Throws {@link IllegalArgumentException} if any parameter is invalid or
     * if the date/time strings cannot be parsed according to the expected formats.
     * </p>
     *
     * @param date      the date string in "dd/MM/yyyy" format (must not be null or blank)
     * @param time      the time string in "HH:mm" format (must not be null or blank)
     * @param productId the product ID (must be positive)
     * @param brandId   the brand ID (must be positive)
     * @return the combined {@link LocalDateTime} object from the date and time strings
     * @throws IllegalArgumentException if validation fails or parsing fails
     */
    public static LocalDateTime validate(String date, String time, Long productId, Long brandId) {
        if (date == null || date.isBlank()) {
            throw new IllegalArgumentException("Date must not be blank");
        }

        if (time == null || time.isBlank()) {
            throw new IllegalArgumentException("Time must not be blank");
        }

        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("Product ID must be a positive number");
        }

        if (brandId == null || brandId <= 0) {
            throw new IllegalArgumentException("Brand ID must be a positive number");
        }

        try {
            LocalDate localDate = LocalDate.parse(date, DATE_FORMAT);
            LocalTime localTime = LocalTime.parse(time, TIME_FORMAT);
            return LocalDateTime.of(localDate, localTime);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date or time format. Expected dd/MM/yyyy and HH:mm");
        }
    }
}