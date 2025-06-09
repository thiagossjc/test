package org.inditex.prices.application.port;

import org.inditex.prices.application.dto.PriceResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Interface for price service operations.
 */
public interface PriceServicePort {

    /**
     * Retrieves all prices.
     *
     * @return a Flux emitting all price response DTOs
     */
    Flux<PriceResponseDto> getAllPrices();

    /**
     * Finds the applicable price based on product ID, brand ID, and date.
     *
     * @param productId the product identifier
     * @param brandId the brand identifier
     * @param date the date for which the price is requested
     * @return a Mono emitting the applicable price response DTO, or empty if not found
     */
    Mono<PriceResponseDto> findPrice(Long productId, Long brandId, LocalDateTime date);

    /**
     * Fallback method used when a price cannot be retrieved due to an error.
     *
     * @param productId the product identifier
     * @param brandId the brand identifier
     * @param date the date for which the price is requested
     * @param throwable the error that caused the fallback
     * @return a Mono emitting a fallback price response DTO
     */
    Mono<PriceResponseDto> fallbackPrice(Long productId, Long brandId, LocalDateTime date, Throwable throwable);
}
