package org.organization.prices.application.port;

import org.organization.prices.domain.model.PriceEvent;
import reactor.core.publisher.Mono;

/**
 * Port interface for publishing {@link PriceEvent} to an external event system.
 */
public interface EventPublisherPort {

    /**
     * Publishes a price event asynchronously.
     *
     * @param event the price event to be published
     * @return a {@link Mono} indicating completion or error
     */
    Mono<Void> publishEvent(PriceEvent event);
}
