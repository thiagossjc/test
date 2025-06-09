package org.inditex.prices.infrastructure.adapter.in.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Integration tests for the PriceController REST API.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class PriceControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private static final String BASE_URL = "/api/prices/filter";
    private static final int PRODUCT_ID = 35455;
    private static final int BRAND_ID = 1;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Test
    void testGetPrice_test1() {
        performRequest("2020-06-14T10:00:00", 1, 35.50);
    }

    @Test
    void testGetPrice_test2() {
        performRequest("2020-06-14T16:00:00", 2, 25.45);
    }

    @Test
    void testGetPrice_test3() {
        performRequest("2020-06-14T21:00:00", 1, 35.50);
    }

    @Test
    void testGetPrice_test4() {
        performRequest("2020-06-15T10:00:00", 3, 30.50);
    }

    @Test
    void testGetPrice_test5() {
        performRequest("2020-06-16T21:00:00", 4, 38.95);
    }

    /**
     * Helper method to perform a GET request and assert the expected values.
     *
     * @param applicationDate ISO 8601 date-time string for application date.
     * @param expectedPriceList Expected price list ID to match.
     * @param expectedPrice Expected price value to match.
     */
    private void performRequest(String applicationDate, int expectedPriceList, double expectedPrice) {
        LocalDateTime dateTime = LocalDateTime.parse(applicationDate);
        String date = dateTime.format(DATE_FORMATTER); // e.g., "14/06/2020"
        String time = dateTime.format(TIME_FORMATTER); // e.g., "16:00"

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(BASE_URL)
                        .queryParam("productId", PRODUCT_ID)
                        .queryParam("brandId", BRAND_ID)
                        .queryParam("date", date)
                        .queryParam("time", time)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.productId").isEqualTo(PRODUCT_ID)
                .jsonPath("$.brandId").isEqualTo(BRAND_ID)
                .jsonPath("$.priceList").isEqualTo(expectedPriceList)
                .jsonPath("$.price").isEqualTo(expectedPrice);
    }
}