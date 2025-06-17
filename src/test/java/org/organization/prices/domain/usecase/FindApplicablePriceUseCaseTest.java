package org.organization.prices.domain.usecase;

import org.organization.prices.application.port.PriceRepositoryPort;
import org.organization.prices.domain.model.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link FindApplicablePriceUseCase} class.
 *
 * This class tests the behavior of the use case responsible for finding
 * the applicable price given product, brand, and date criteria.
 */
class FindApplicablePriceUseCaseTest {

    /**
     * Mocked repository port used by the use case.
     */
    @Mock
    private PriceRepositoryPort repositoryPort;

    /**
     * Instance of the use case under test.
     */
    private FindApplicablePriceUseCase useCase;

    /**
     * Initializes mocks and creates a new instance of the use case before each test.
     */
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        useCase = new FindApplicablePriceUseCase(repositoryPort);
    }

    /**
     * Tests that the use case returns a Price when the repository provides one.
     */
    @Test
    void findPrice_shouldReturnPrice() {
        Long productId = 1L;
        Long brandId = 2L;
        LocalDateTime date = LocalDateTime.now();

        Price price = new Price(); // You can populate fields as needed

        when(repositoryPort.findApplicablePrice(productId, brandId, date))
                .thenReturn(Mono.just(price));

        StepVerifier.create(useCase.findApplicablePrice(productId, brandId, date))
                .expectNext(price)
                .verifyComplete();

        verify(repositoryPort).findApplicablePrice(productId, brandId, date);
    }

    /**
     * Tests that the use case propagates errors when the repository returns an error.
     */
    @Test
    void findPrice_whenError_shouldPropagateError() {
        Long productId = 1L;
        Long brandId = 2L;
        LocalDateTime date = LocalDateTime.now();

        when(repositoryPort.findApplicablePrice(productId, brandId, date))
                .thenReturn(Mono.error(new RuntimeException("Repository error")));

        StepVerifier.create(useCase.findApplicablePrice(productId, brandId, date))
                .expectError(RuntimeException.class)
                .verify();

        verify(repositoryPort).findApplicablePrice(productId, brandId, date);
    }
}