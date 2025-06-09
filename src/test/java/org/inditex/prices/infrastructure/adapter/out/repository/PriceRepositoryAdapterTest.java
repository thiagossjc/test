package org.inditex.prices.infrastructure.adapter.out.repository;

import org.inditex.prices.domain.execption.PriceNotFoundException;
import org.inditex.prices.domain.model.Price;
import org.inditex.prices.infrastructure.entity.PriceEntity;
import org.inditex.prices.infrastructure.mapper.PriceEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link PriceRepositoryAdapter}.
 * <p>
 * This class verifies the behavior of the repository adapter which
 * interacts with the underlying PriceRepository and maps entities
 * to domain Price objects.
 * </p>
 */
class PriceRepositoryAdapterTest {

    /**
     * Mock of the PriceRepository used for database interactions.
     */
    @Mock
    private PriceRepository priceRepository;

    /**
     * Mock of the mapper that converts between PriceEntity and domain Price.
     */
    @Mock
    private PriceEntityMapper mapper;

    /**
     * Instance of PriceRepositoryAdapter under test.
     * Injects the above mocks automatically.
     */
    @InjectMocks
    private PriceRepositoryAdapter priceRepositoryAdapter;

    /**
     * Initializes mocks before each test method.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reset(priceRepository, mapper); // Restart mocks to avoid interference between tests
    }

    /**
     * Tests that {@code findApplicablePrice} returns a mapped Price object
     * when the repository finds a matching PriceEntity.
     * <p>
     * Sets up a mocked PriceEntity and expects the adapter to return the
     * correctly mapped Price with expected values.
     * </p>
     */
    @Test
    void findApplicablePrice_shouldReturnMappedPrice() {
        // Arrange
        Long productId = 35455L;
        Long brandId = 1L;
        LocalDateTime date = LocalDateTime.now();
        PriceEntity priceEntity = new PriceEntity();
        priceEntity.setId(1L);
        priceEntity.setBrandId(brandId);
        priceEntity.setStartDate(date.minusHours(1));
        priceEntity.setEndDate(date.plusHours(1));
        priceEntity.setPriceList(1);
        priceEntity.setProductId(productId);
        priceEntity.setPriority(1);
        priceEntity.setPrice(new BigDecimal("35.5"));
        priceEntity.setCurrency("EUR");

        Price price = new Price();
        price.setProductId(productId);
        price.setBrandId(brandId);
        price.setPriceList(1);
        price.setStartDate(priceEntity.getStartDate());
        price.setEndDate(priceEntity.getEndDate());
        price.setPriority(1);
        price.setPrice(new BigDecimal("35.5"));
        price.setCurrency("EUR");

        when(priceRepository.findApplicablePrice(productId, brandId, date)).thenReturn(Mono.just(priceEntity));
        when(mapper.toDomain(priceEntity)).thenReturn(price);

        // Act
        Mono<Price> result = priceRepositoryAdapter.findApplicablePrice(productId, brandId, date);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(p -> p.getProductId().equals(35455L)
                        && p.getPrice().equals(new BigDecimal("35.5"))
                        && p.getCurrency().equals("EUR"))
                .verifyComplete();

        verify(priceRepository, times(1)).findApplicablePrice(productId, brandId, date);
        verify(mapper, times(1)).toDomain(priceEntity);
        verifyNoMoreInteractions(priceRepository, mapper);
    }

    /**
     * Tests that {@code findApplicablePrice} throws a {@link PriceNotFoundException}
     * when the repository does not find any matching price.
     * <p>
     * Verifies that the mapper is not invoked and the exception contains the expected message.
     * </p>
     */
    @Test
    void findApplicablePrice_shouldThrowPriceNotFoundException_whenPriceNotFound() {
        // Arrange
        Long productId = 35455L;
        Long brandId = 1L;
        LocalDateTime date = LocalDateTime.now();
        when(priceRepository.findApplicablePrice(productId, brandId, date)).thenReturn(Mono.empty());

        // Act
        Mono<Price> result = priceRepositoryAdapter.findApplicablePrice(productId, brandId, date);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PriceNotFoundException
                        && throwable.getMessage().equals("Price not found"))
                .verify();

        verify(priceRepository, times(1)).findApplicablePrice(productId, brandId, date);
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(priceRepository, mapper);
    }
}