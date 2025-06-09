package org.inditex.prices.infrastructure.adapter.in.grpc;

import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.inditex.prices.infrastructure.adapter.grpc.PriceRequest;
import org.inditex.prices.infrastructure.adapter.grpc.PriceResponse;
import org.inditex.prices.infrastructure.adapter.grpc.PriceServiceGrpc;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.net.ServerSocket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Integration tests for the { PriceGrpcServiceGrpc} gRPC service.
 * <p>
 * This test class starts a Spring Boot context on a defined port,
 * dynamically finds an available port for gRPC server,
 * and verifies the end-to-end communication with the PriceService gRPC endpoint.
 * </p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PriceGrpcServiceIntegrationTest {

    /**
     * gRPC client channel used to connect to the server.
     */
    private ManagedChannel channel;

    /**
     * Blocking stub for the PriceService gRPC client.
     */
    private PriceServiceGrpc.PriceServiceBlockingStub stub;

    /**
     * Port on which the gRPC server will listen.
     */
    private static int grpcPort;

    /**
     * Sets the gRPC server port property dynamically before the Spring context loads.
     * <p>
     * This method attempts to find an available port and sets the property
     * {@code grpc.server.port} accordingly.
     * </p>
     *
     * @param registry the dynamic property registry for the Spring context
     */
    @DynamicPropertySource
    static void grpcPortProperties(DynamicPropertyRegistry registry) {
        grpcPort = findAvailablePort();
        registry.add("grpc.server.port", () -> grpcPort);
        System.out.println("Using gRPC port: " + grpcPort);
    }

    /**
     * Attempts to find an available port among preferred ports or falls back to dynamic port.
     *
     * @return an available port number or 0 if none of the preferred ports are free
     */
    private static int findAvailablePort() {
        int[] preferredPorts = {9090, 9091};

        for (int port : preferredPorts) {
            if (isPortAvailable(port)) {
                return port;
            }
        }
        return 0; // Dynamic port assigned by the OS
    }

    /**
     * Checks if a specific port is available for use.
     *
     * @param port the port number to check
     * @return true if the port is available, false otherwise
     */
    private static boolean isPortAvailable(int port) {
        try (ServerSocket socket = new ServerSocket(port)) {
            socket.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Initializes the gRPC client channel and blocking stub before all tests.
     * <p>
     * Waits up to 20 seconds for the server to be ready before proceeding.
     * </p>
     *
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    @BeforeAll
    void setupChannel() throws InterruptedException {
        channel = ManagedChannelBuilder.forAddress("localhost", grpcPort)
                .usePlaintext()
                .build();

        long timeout = System.currentTimeMillis() + 20_000;
        while (System.currentTimeMillis() < timeout && channel.getState(true) != ConnectivityState.READY) {
            Thread.sleep(200);
        }

        if (channel.getState(true) != ConnectivityState.READY) {
            throw new IllegalStateException("gRPC server not ready after 20 seconds");
        }

        stub = PriceServiceGrpc.newBlockingStub(channel);
    }

    /**
     * Shuts down the gRPC client channel after all tests.
     */
    @AfterAll
    void shutdown() {
        if (channel != null) {
            channel.shutdownNow();
        }
    }

    /**
     * Tests the successful retrieval of a price via the gRPC service.
     * <p>
     * Sends a {@link PriceRequest} with product and brand identifiers and a date,
     * then verifies that the response contains expected values.
     * </p>
     */
    @Test
    void testGetPriceSuccess() {
        PriceRequest request = PriceRequest.newBuilder()
                .setProductId(35455)
                .setBrandId(1)
                .setDate("14/06/2020")
                .setTime("10:00")
                .build();

        PriceResponse response = stub.getPrice(request);

        assertNotNull(response);
        assertEquals(35455, response.getProductId());
        assertEquals(1, response.getBrandId());
        assertEquals(35.50, response.getPrice());
    }
}