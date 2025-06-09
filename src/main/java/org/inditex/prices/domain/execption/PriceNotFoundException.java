package org.inditex.prices.domain.execption;

/**
 * Exception thrown when no applicable price is found for the given criteria.
 */
public class PriceNotFoundException extends RuntimeException {

    /**
     * Constructs a new PriceNotFoundException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public PriceNotFoundException(String message) {
        super(message);
    }
}
