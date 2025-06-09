package org.inditex.prices.infrastructure.adapter.out.repository;

import lombok.RequiredArgsConstructor;
import org.inditex.prices.domain.model.PriceEvent;
import org.inditex.prices.application.port.EventStorePort;
import org.inditex.prices.infrastructure.mapper.PriceEventEntityMapper;
import org.inditex.prices.infrastructure.entity.PriceEventEntity;
import org.springframework.stereotype.Component;

/**
 * Adapter for storing price query events in the database.
 */
@Component
@RequiredArgsConstructor
public class EventStoreRepositoryAdapter implements EventStorePort {

    /**
     * Repository for accessing price event entities.
     */
    private final PriceEventRepository priceEventRepository;

    /**
     * Mapper for converting between domain model and persistence entity.
     */
    private final PriceEventEntityMapper priceEventEntityMapper;

    /**
     * Stores a price query event by converting the domain model to
     * a persistence entity and saving it.
     *
     * @param priceEvent the price event domain model to store
     */
    @Override
    public void storeEvent(PriceEvent priceEvent) {
        PriceEventEntity priceEventEntity = priceEventEntityMapper.toEntity(priceEvent);
        priceEventRepository.save(priceEventEntity).subscribe();
    }
}