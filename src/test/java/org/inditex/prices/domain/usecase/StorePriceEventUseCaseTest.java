package org.inditex.prices.domain.usecase;

import org.inditex.prices.domain.model.Price;
import org.inditex.prices.domain.model.PriceEvent;
import org.inditex.prices.application.port.EventPublisherPort;
import org.inditex.prices.application.port.EventStorePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link StorePriceEventUseCase}.
 * <p>
 * Tests the behavior of storing and publishing price events,
 * including handling of null prices and error scenarios.
 * </p>
 */
class StorePriceEventUseCaseTest {

    /**
     * Mock for the event storage port.
     */
    @Mock
    private EventStorePort eventStore;

    /**
     * Mock for the event publishing port.
     */
    @Mock
    private EventPublisherPort eventPublisher;

    /**
     * The use case under test with injected mocks.
     */
    @InjectMocks
    private StorePriceEventUseCase storePriceEventUseCase;

    /**
     * Initializes mocks before each test method.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reset(eventStore, eventPublisher);
    }

    /**
     * Tests that {@code storeEvent} completes successfully when given a valid Price.
     * <p>
     * Verifies that the event is stored and published once.
     * </p>
     */
    @Test
    void storeEvent_shouldStoreAndPublishEvent_whenPriceIsValid() {
        // Arrange
        Price price = new Price();
        price.setProductId(1L);
        price.setBrandId(1L);
        price.setPriceList(1);
        price.setPrice(new BigDecimal("25.0"));
        LocalDateTime queryDate = LocalDateTime.now();

        doNothing().when(eventStore).storeEvent(any(PriceEvent.class));
        when(eventPublisher.publishEvent(any(PriceEvent.class))).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = storePriceEventUseCase.storeEvent(price, queryDate);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(eventStore, times(1)).storeEvent(any(PriceEvent.class));
        verify(eventPublisher, times(1)).publishEvent(any(PriceEvent.class));
    }

    /**
     * Tests that {@code storeEvent} completes empty when given a null Price.
     * <p>
     * Verifies that no events are stored or published.
     * </p>
     */
    @Test
    void storeEvent_shouldReturnEmpty_whenPriceIsNull() {
        // Act
        Mono<Void> result = storePriceEventUseCase.storeEvent(null, LocalDateTime.now());

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verifyNoInteractions(eventStore, eventPublisher);
    }

    /**
     * Tests that {@code storeEvent} returns empty when an error occurs during storing or publishing.
     * <p>
     * Verifies that error is caught and the returned Mono completes empty.
     * </p>
     */
    @Test
    void storeEvent_shouldReturnEmpty_whenErrorOccurs() {
        // Arrange
        Price price = new Price();
        price.setProductId(1L);
        price.setBrandId(1L);
        price.setPriceList(100);
        price.setPrice(new BigDecimal("25.0"));
        LocalDateTime queryDate = LocalDateTime.now();

        doThrow(new RuntimeException("Store failed")).when(eventStore).storeEvent(any(PriceEvent.class));

        // Act
        Mono<Void> result = storePriceEventUseCase.storeEvent(price, queryDate);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(eventStore, times(1)).storeEvent(any(PriceEvent.class));
        verify(eventPublisher, never()).publishEvent(any());
    }
}
