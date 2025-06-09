package org.inditex.prices.infrastructure.adapter.out.repository;

import org.inditex.prices.domain.model.PriceEvent;
import org.inditex.prices.infrastructure.entity.PriceEventEntity;
import org.inditex.prices.infrastructure.mapper.PriceEventEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link EventStoreRepositoryAdapter}.
 * <p>
 * This class verifies the behavior of the adapter responsible for
 * persisting price query events in the database using the underlying repository
 * and entity mapper.
 * </p>
 */
class EventStoreRepositoryAdapterTest {

    /**
     * Mock of the repository used to save PriceEvent entities.
     */
    @Mock
    private PriceEventRepository priceEventRepository;

    /**
     * Mock of the mapper that converts between PriceEvent and PriceEventEntity.
     */
    @Mock
    private PriceEventEntityMapper priceEventEntityMapper;

    /**
     * Instance of EventStoreRepositoryAdapter under test.
     */
    @InjectMocks
    private EventStoreRepositoryAdapter eventStoreRepositoryAdapter;

    /**
     * Initializes the mocks before each test method.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reset(priceEventRepository, priceEventEntityMapper);
    }

    /**
     * Tests that {@code storeEvent} correctly maps the domain event to
     * an entity and saves it using the repository.
     */
    @Test
    void storeEvent_shouldMapAndSavePriceEventEntity() {

        PriceEvent priceEvent = PriceEvent.builder()
                .productId(35455L)
                .brandId(1L)
                .priceList(0)
                .queryDate(LocalDateTime.now())
                .price(new BigDecimal(0))
                .eventType("PRICE_QUERY")
                .createdAt(LocalDateTime.now())
                .build();

        // Arrange
        PriceEventEntity entity = new PriceEventEntity();
        entity.setProductId(35455L);
        entity.setBrandId(1L);
        entity.setQueryDate(LocalDateTime.parse("2020-06-14T10:00:00"));

        when(priceEventEntityMapper.toEntity(priceEvent)).thenReturn(entity);
        when(priceEventRepository.save(entity)).thenReturn(Mono.just(entity));

        // Act
        eventStoreRepositoryAdapter.storeEvent(priceEvent);

        // Assert
        verify(priceEventEntityMapper, times(1)).toEntity(priceEvent);
        verify(priceEventRepository, times(1)).save(entity);
        verifyNoMoreInteractions(priceEventEntityMapper, priceEventRepository);
    }
}
