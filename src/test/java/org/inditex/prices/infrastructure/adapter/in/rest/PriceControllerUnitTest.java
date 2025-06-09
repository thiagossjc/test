package org.inditex.prices.infrastructure.adapter.in.rest;

import org.inditex.prices.application.dto.PriceResponseDto;
import org.inditex.prices.application.port.PriceServicePort;
import org.inditex.prices.application.validator.PriceRequestValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link PriceController}.
 *
 * This test class verifies the REST controller's behavior for retrieving price information
 * based on application date, product ID, and brand ID.
 *
 * The expected behavior is aligned with the use case that selects the applicable price
 * from the PRICES table based on start/end date and priority rules.
 */
class PriceControllerUnitTest {

    private PriceServicePort priceServicePort;
    private PriceController priceController;

    @BeforeEach
    void setup() {
        priceServicePort = mock(PriceServicePort.class);
        priceController = new PriceController(priceServicePort);
    }

    /**
     * Utility method to perform the standard validation and verification
     * of the getPrice endpoint.
     */
    private void runTest(LocalDateTime applicationDate, Long productId, Long brandId, BigDecimal expectedPrice) {
        PriceResponseDto mockResponse = new PriceResponseDto();
        mockResponse.setProductId(productId);
        mockResponse.setBrandId(brandId);
        mockResponse.setPrice(expectedPrice);

        try (MockedStatic<PriceRequestValidator> validatorMock = Mockito.mockStatic(PriceRequestValidator.class)) {
            String date = String.format("%02d/%02d/%d", applicationDate.getDayOfMonth(), applicationDate.getMonthValue(), applicationDate.getYear());
            String time = String.format("%02d:%02d", applicationDate.getHour(), applicationDate.getMinute());

            validatorMock.when(() -> PriceRequestValidator.validate(date, time, productId, brandId))
                    .thenReturn(applicationDate);

            when(priceServicePort.findPrice(productId, brandId, applicationDate))
                    .thenReturn(Mono.just(mockResponse));

            ResponseEntity<Mono<PriceResponseDto>> responseEntity = priceController.getPrice(productId, brandId, date, time);

            assertNotNull(responseEntity);
            assertEquals(200, responseEntity.getStatusCodeValue());

            Mono<PriceResponseDto> responseMono = responseEntity.getBody();
            assertNotNull(responseMono);

            StepVerifier.create(responseMono)
                    .expectNextMatches(dto ->
                            dto.getProductId().equals(productId)
                                    && dto.getBrandId().equals(brandId)
                                    && dto.getPrice().compareTo(expectedPrice) == 0
                    )
                    .verifyComplete();

            validatorMock.verify(() -> PriceRequestValidator.validate(date, time, productId, brandId));
            verify(priceServicePort).findPrice(productId, brandId, applicationDate);
        }
    }

    /**
     * Test 1: Request at 10:00 on June 14 for product 35455 and brand 1 (ZARA).
     * Expected price: 35.50 (priority 0).
     */
    @Test
    void testScenario1() {
        runTest(LocalDateTime.of(2020, 6, 14, 10, 0), 35455L, 1L, BigDecimal.valueOf(35.50));
    }

    /**
     * Test 2: Request at 16:00 on June 14 for product 35455 and brand 1 (ZARA).
     * Expected price: 25.45 (priority 1).
     */
    @Test
    void testScenario2() {
        runTest(LocalDateTime.of(2020, 6, 14, 16, 0), 35455L, 1L, BigDecimal.valueOf(25.45));
    }

    /**
     * Test 3: Request at 21:00 on June 14 for product 35455 and brand 1 (ZARA).
     * Expected price: 35.50 (priority 0).
     */
    @Test
    void testScenario3() {
        runTest(LocalDateTime.of(2020, 6, 14, 21, 0), 35455L, 1L, BigDecimal.valueOf(35.50));
    }

    /**
     * Test 4: Request at 10:00 on June 15 for product 35455 and brand 1 (ZARA).
     * Expected price: 30.50 (priority 1).
     */
    @Test
    void testScenario4() {
        runTest(LocalDateTime.of(2020, 6, 15, 10, 0), 35455L, 1L, BigDecimal.valueOf(30.50));
    }

    /**
     * Test 5: Request at 21:00 on June 16 for product 35455 and brand 1 (ZARA).
     * Expected price: 38.95 (priority 1).
     */
    @Test
    void testScenario5() {
        runTest(LocalDateTime.of(2020, 6, 16, 21, 0), 35455L, 1L, BigDecimal.valueOf(38.95));
    }

    /**
     * Tests the getAllPrices endpoint returns all prices from the database.
     */
    @Test
    void testGetAllPrices() {
        PriceResponseDto p1 = new PriceResponseDto();
        p1.setProductId(1L);
        p1.setBrandId(1L);
        p1.setPrice(BigDecimal.valueOf(10.0));

        PriceResponseDto p2 = new PriceResponseDto();
        p2.setProductId(2L);
        p2.setBrandId(2L);
        p2.setPrice(BigDecimal.valueOf(20.0));

        when(priceServicePort.getAllPrices()).thenReturn(Flux.just(p1, p2));

        Flux<PriceResponseDto> allPricesFlux = priceController.getAllPrices();

        StepVerifier.create(allPricesFlux)
                .expectNext(p1)
                .expectNext(p2)
                .verifyComplete();

        verify(priceServicePort).getAllPrices();
    }
}