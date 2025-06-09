package org.inditex.prices.domain.usecase;

import org.inditex.prices.application.port.PriceRepositoryPort;
import org.inditex.prices.domain.model.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link FindAllPriceUseCase} class.
 * 
 * This class tests the behavior of the use case responsible for retrieving all prices.
 */
class FindAllPriceUseCaseTest {

    /**
     * Mocked repository port used by the use case.
     */
    @Mock
    private PriceRepositoryPort repositoryPort;

    /**
     * Instance of the use case under test.
     */
    private FindAllPriceUseCase useCase;

    /**
     * Initializes mocks and creates a new instance of the use case before each test.
     */
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        useCase = new FindAllPriceUseCase(repositoryPort);
    }

    /**
     * Tests that the use case returns all prices from the repository.
     */
    @Test
    void findAllPrice_shouldReturnAllPrices() {
        Price price1 = new Price();
        Price price2 = new Price();

        when(repositoryPort.getAll())
                .thenReturn(Flux.just(price1, price2));

        StepVerifier.create(useCase.findAllPrice())
                .expectNext(price1)
                .expectNext(price2)
                .verifyComplete();

        verify(repositoryPort).getAll();
    }

    /**
     * Tests that the use case propagates errors when the repository returns an error.
     */
    @Test
    void findAllPrice_whenError_shouldPropagateError() {
        when(repositoryPort.getAll())
                .thenReturn(Flux.error(new RuntimeException("Repository error")));

        StepVerifier.create(useCase.findAllPrice())
                .expectError(RuntimeException.class)
                .verify();

        verify(repositoryPort).getAll();
    }
}
