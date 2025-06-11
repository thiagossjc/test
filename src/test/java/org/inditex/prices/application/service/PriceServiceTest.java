package org.inditex.prices.application.service;

import io.opentelemetry.api.trace.Tracer;
import org.inditex.prices.application.dto.PriceRequestDto;
import org.inditex.prices.application.dto.PriceResponseDto;
import org.inditex.prices.application.mapper.PriceMapper;
import org.inditex.prices.application.port.CircuitBreakerPort;
import org.inditex.prices.application.port.TracePort;
import org.inditex.prices.domain.execption.PriceNotFoundException;
import org.inditex.prices.domain.model.Price;
import org.inditex.prices.domain.usecase.FindApplicablePriceUseCase;
import org.inditex.prices.domain.usecase.StorePriceEventUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link PriceService} class.
 */
class PriceServiceTest {

    @Mock
    private FindApplicablePriceUseCase findPriceUseCase;

    @Mock
    private StorePriceEventUseCase storeEventUseCase;

    @Mock
    private TracePort tracingPort;

    @Mock
    private Tracer tracer;

    @Mock
    private CircuitBreakerPort circuitBreakerPort;

    @Mock
    private PriceMapper priceMapper;

    @InjectMocks
    private PriceService priceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindPrice() {
        // Arrange
        PriceRequestDto request = new PriceRequestDto(35455L, 1L, LocalDateTime.parse("2020-06-14T10:00:00"));

        Price price = new Price();
        price.setProductId(35455L);
        price.setBrandId(1L);
        price.setPriceList(0);
        price.setStartDate(LocalDateTime.parse("2020-06-14T00:00:00"));
        price.setEndDate(LocalDateTime.parse("2020-12-31T23:59:59"));
        price.setPrice(new BigDecimal("35.5"));

        PriceResponseDto responseDto = new PriceResponseDto();
        responseDto.setProductId(35455L);
        responseDto.setBrandId(1L);
        responseDto.setPriceList(0);
        responseDto.setPrice(new BigDecimal("35.5"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        responseDto.setStartDate(LocalDateTime.parse("2020-06-14T00:00:00").format(formatter));
        responseDto.setEndDate(LocalDateTime.parse("2020-12-31T23:59:59").format(formatter));

        when(findPriceUseCase.findApplicablePrice(anyLong(), anyLong(), any(LocalDateTime.class))).thenReturn(Mono.just(price));
        when(priceMapper.toResponse(price)).thenReturn(responseDto);
        when(storeEventUseCase.storeEvent(any(Price.class), any(LocalDateTime.class))).thenReturn(Mono.empty());
        when(tracingPort.trace(
                eq("PriceService.findPrice"),
                any(Mono.class),
                eq("productId"), anyString(),
                eq("brandId"), anyString(),
                eq("date"), anyString()
        )).thenAnswer(invocation -> invocation.getArgument(1));
        when(circuitBreakerPort.executeCircuitBreaker(
                eq("priceService"),
                any(Mono.class), eq(PriceNotFoundException.class)))
                .thenAnswer(invocation -> invocation.getArgument(1));

        // Act
        Mono<PriceResponseDto> result = priceService.findApplicablePrice(request.getProductId(), request.getBrandId(), request.getDate());

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(35455L, response.getProductId());
                    assertEquals(1L, response.getBrandId());
                    assertEquals(new BigDecimal("35.5"), response.getPrice());
                })
                .verifyComplete();

        verify(findPriceUseCase, times(1)).findApplicablePrice(35455L, 1L, request.getDate());
        verify(priceMapper, times(1)).toResponse(price);
        verify(storeEventUseCase, times(1)).storeEvent(any(Price.class), any(LocalDateTime.class));
        verify(tracingPort, times(1)).trace(
                eq("PriceService.findPrice"),
                any(Mono.class),
                eq("productId"), anyString(),
                eq("brandId"), anyString(),
                eq("date"), anyString()
        );
        verify(circuitBreakerPort, times(1)).executeCircuitBreaker(eq("priceService"), any(Mono.class), eq(PriceNotFoundException.class));
    }

    @Test
    void testFindPriceNotFound() {
        LocalDateTime now = LocalDateTime.now();

        when(findPriceUseCase.findApplicablePrice(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(Mono.empty());
        when(circuitBreakerPort.executeCircuitBreaker(
                eq("priceService"),
                any(Mono.class), eq(PriceNotFoundException.class)))
                .thenAnswer(invocation -> invocation.getArgument(1));
        when(tracingPort.trace(
                eq("PriceService.findPrice"),
                any(Mono.class),
                eq("productId"), anyString(),
                eq("brandId"), anyString(),
                eq("date"), anyString()
        )).thenAnswer(invocation -> invocation.getArgument(1));

        // Act
        Mono<PriceResponseDto> result = priceService.findApplicablePrice(1L, 1L, now);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PriceNotFoundException
                        && throwable.getMessage().equals("No price found for given criteria"))
                .verify();

        verify(findPriceUseCase, times(1)).findApplicablePrice(1L, 1L, now);
        verify(circuitBreakerPort, times(1)).executeCircuitBreaker(eq("priceService"), any(Mono.class), eq(PriceNotFoundException.class));
        verify(tracingPort, times(1)).trace(
                eq("PriceService.findPrice"),
                any(Mono.class),
                eq("productId"), anyString(),
                eq("brandId"), anyString(),
                eq("date"), anyString()
        );
    }
}