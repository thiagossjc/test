package org.organization.prices.application.port;

import org.organization.prices.domain.model.PriceEvent;

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

