package org.organization.prices.infrastructure.adapter.out.kafka;

import org.organization.prices.domain.model.PriceEvent;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
@EmbeddedKafka(
        partitions = 1,
        topics = "priceTopic",
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:0",
                "port=0"
        },
        controlledShutdown = true
)
@DirtiesContext
@ComponentScan(
        basePackages = "org.organization.prices",
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                net.devh.boot.grpc.server.autoconfigure.GrpcServerAutoConfiguration.class
                        }
                )
        }
)
@Tag("integration")
@Disabled
class KafkaEventPublisherAdapterIntegrationTest {

    @Autowired
    private KafkaEventPublisherAdapter publisher;

    @Test
    @Disabled
    void testPublishPriceEvent() {
        PriceEvent event = PriceEvent.builder()
                .productId(35455L)
                .brandId(1L)
                .priceList(0)
                .queryDate(LocalDateTime.now())
                .price(new BigDecimal("35.50"))
                .eventType("PRICE_QUERY")
                .createdAt(LocalDateTime.now())
                .build();

        StepVerifier.create(publisher.publishEvent(event))
                .verifyComplete();
    }
}