package org.inditex.prices.application.port;

import org.inditex.prices.domain.model.PriceEvent;

/**
 * Interface for storing price query events.
 */
public interface EventStorePort {

    /**
     * Stores a price event.
     *
     * @param priceEvent the price event to store
     */
    void storeEvent(PriceEvent priceEvent);
}

