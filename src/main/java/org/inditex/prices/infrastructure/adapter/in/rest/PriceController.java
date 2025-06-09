package org.inditex.prices.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.inditex.prices.application.dto.PriceResponseDto;
import org.inditex.prices.application.port.PriceServicePort;
import org.inditex.prices.application.validator.PriceRequestValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * REST controller responsible for handling price-related HTTP requests.
 * <p>
 * Provides endpoints to retrieve price information based on product, brand, and application date,
 * as well as fetching all available prices.
 * </p>
 */
@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
@Tag(name = "Prices", description = "Endpoints related to product pricing")
public class PriceController {

    /**
     * Service port interface for price business logic.
     */
    private final PriceServicePort priceServicePort;

    @GetMapping("/filter")
    @Operation(summary = "Find applicable price", description = "Returns the price applicable for the product, brand, and date provided.")
    public ResponseEntity<Mono<PriceResponseDto>> getPrice(
            @RequestParam("productId")
            @Parameter(
                    description = "Product ID to query",
                    example = "35455",
                    required = true
            )
            Long productId,

            @RequestParam("brandId")
            @Parameter(
                    description = "Brand ID to query",
                    example = "1",
                    required = true
            )
            Long brandId,

            @RequestParam("date")
            @Parameter(
                    description = "Date of query in format dd/MM/yyyy",
                    example = "14/06/2020",
                    required = true,
                    schema = @Schema(type = "string", pattern = "dd/MM/yyyy")
            )
            String date,

            @RequestParam("time")
            @Parameter(
                    description = "Time of query in format HH:mm",
                    example = "16:00",
                    required = true,
                    schema = @Schema(type = "string", pattern = "HH:mm")
            )
            String time) {

        LocalDateTime applicationDate = PriceRequestValidator.validate(date, time, productId, brandId);
        return ResponseEntity.ok(priceServicePort.findPrice(productId, brandId, applicationDate));
    }

    /**
     * Retrieves all prices.
     *
     * @return A {@link Flux} stream of all {@link PriceResponseDto} objects.
     */
    @GetMapping
    @Operation(summary = "Find all price", description = "Returns all prices.")
    public Flux<PriceResponseDto> getAllPrices() {
        return priceServicePort.getAllPrices();
    }
}