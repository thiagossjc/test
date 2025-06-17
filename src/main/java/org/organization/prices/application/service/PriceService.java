package org.organization.prices.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.organization.prices.application.dto.PriceResponseDto;
import org.organization.prices.application.mapper.PriceMapper;
import org.organization.prices.application.port.CircuitBreakerPort;
import org.organization.prices.application.port.PriceServicePort;
import org.organization.prices.application.port.TracePort;
import org.organization.prices.domain.execption.PriceNotFoundException;
import org.organization.prices.domain.model.Price;
import org.organization.prices.domain.usecase.FindAllPriceUseCase;
import org.organization.prices.domain.usecase.FindApplicablePriceUseCase;
import org.organization.prices.domain.usecase.StorePriceEventUseCase;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Implementation of the PriceServicePort that provides price-related operations.
 * Applies tracing and circuit-breaking logic to ensure observability and fault tolerance.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PriceService implements PriceServicePort {

    private final FindApplicablePriceUseCase findPriceUseCase;
    private final FindAllPriceUseCase findAllPriceUseCase;
    private final StorePriceEventUseCase storeEventUseCase;
    private final PriceMapper priceMapper;
    private final TracePort tracingPort;
    private final CircuitBreakerPort circuitBreakerPort;

    /**
     * Retrieves all available prices using the defined use case.
     * Applies circuit breaker and traces the operation.
     *
     * @return a Flux of PriceResponseDto
     */
    public Flux<PriceResponseDto> getAllPrices() {
        return tracingPort.traceFlux(
                "PriceService.getAllPrices",
                circuitBreakerPort.executeCircuitBreaker(
                        "priceService",
                        findAllPriceUseCase.findAllPrice()
                                .map(price -> {
                                    log.debug("Mapping price: {}", price);
                                    return priceMapper.toResponse(price);
                                })
                                .switchIfEmpty(Flux.error(new PriceNotFoundException("No prices found"))), // Propagate error
                        PriceNotFoundException.class // Handle specific error
                )
        );
    }

    /**
     * Finds the applicable price for a given product, brand, and date.
     * Uses circuit breaker and tracing, stores the price event if found.
     *
     * @param productId the product ID
     * @param brandId the brand ID
     * @param date the date for which to retrieve the price
     * @return a Mono of PriceResponseDto
     */
    @Override
    public Mono<PriceResponseDto> findApplicablePrice(Long productId, Long brandId, LocalDateTime date) {
        return tracingPort.trace(
                "PriceService.findPrice",
                circuitBreakerPort.executeCircuitBreaker(
                        "priceService",
                        findPriceUseCase.findApplicablePrice(productId, brandId, date)
                                .switchIfEmpty(Mono.error(new PriceNotFoundException("No price found for given criteria"))) // Propagate error
                                .map(price -> {
                                    log.debug("Found price: {}", price);
                                    return price != null ? price : new Price(brandId, date, null, null, productId, 0, BigDecimal.ZERO, null);
                                })
                                .flatMap(price -> storeEventUseCase.storeEvent(price, date).thenReturn(price))
                                .map(priceMapper::toResponse)
                                .doOnError(e -> log.error("Error processing price: {}", e.getMessage())),
                        PriceNotFoundException.class // Handle specific error
                ),
                "productId", productId.toString(),
                "brandId", brandId.toString(),
                "date", date.toString()
        );
    }

    /**
     * Fallback method used when the findPrice operation fails.
     * Returns a RuntimeException wrapped in a traced Mono.
     *
     * @param productId the product ID
     * @param brandId the brand ID
     * @param date the date
     * @param throwable the exception that caused the fallback
     * @return a Mono error response
     */
    @Override
    public Mono<PriceResponseDto> fallbackPrice(Long productId, Long brandId, LocalDateTime date, Throwable throwable) {
        return tracingPort.trace(
                "PriceService.fallbackPrice",
                Mono.error(new RuntimeException("Service unavailable, please try again later", throwable)),
                "error", throwable.getMessage()
        );
    }
}