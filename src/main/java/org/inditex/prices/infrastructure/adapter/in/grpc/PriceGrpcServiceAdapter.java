package org.inditex.prices.infrastructure.adapter.in.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.inditex.prices.application.port.PriceServicePort;
import org.inditex.prices.application.validator.PriceRequestValidator;
import org.inditex.prices.infrastructure.adapter.grpc.PriceRequest;
import org.inditex.prices.infrastructure.adapter.grpc.PriceResponse;
import org.inditex.prices.infrastructure.adapter.grpc.PriceServiceGrpc;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@GrpcService
@RequiredArgsConstructor
@Slf4j
/**
 * gRPC service adapter for retrieving price information.
 *
 * <p>
 * This class implements the gRPC service defined in the Protobuf schema,
 * handling requests to fetch price details for a given product, brand, and date.
 * It integrates with OpenTelemetry for distributed tracing and uses reactive programming
 * with Project Reactor to handle asynchronous data flows.
 * </p>
 */
public class PriceGrpcServiceAdapter extends PriceServiceGrpc.PriceServiceImplBase {

    /**
     * Service port interface that provides the business logic for retrieving prices.
     */
    private final PriceServicePort service;

    /**
     * OpenTelemetry tracer used for creating and managing spans for distributed tracing.
     */
    private final Tracer tracer;

    /**
     * Handles the gRPC request to retrieve price information for a given product, brand, and date/time.
     *
     * <p>This method performs the following steps:
     * <ul>
     *   <li>Starts an OpenTelemetry span for distributed tracing.</li>
     *   <li>Validates the incoming request parameters (date, time, productId, brandId).</li>
     *   <li>Invokes the business service to find the applicable price asynchronously.</li>
     *   <li>Maps the service response to a gRPC PriceResponse message.</li>
     *   <li>Returns the response via the StreamObserver or handles errors appropriately.</li>
     * </ul>
     * </p>
     *
     * @param request the incoming gRPC request containing product ID, brand ID, date, and time
     * @param responseObserver the gRPC StreamObserver used to send the response or errors back to the client
     */
    @Override
    public void getPrice(PriceRequest request, StreamObserver<PriceResponse> responseObserver) {
        Span span = tracer.spanBuilder("PriceGrpcService.getPrice")
                .setAttribute("productId", request.getProductId())
                .setAttribute("brandId", request.getBrandId())
                .setAttribute("date", request.getDate())
                .setAttribute("time", request.getTime())
                .startSpan();

        Mono.just(request)
                .flatMap(req -> {
                    LocalDateTime dateTime;
                    try {
                        dateTime = PriceRequestValidator.validate(
                                req.getDate(), req.getTime(), req.getProductId(), req.getBrandId()
                        );
                    } catch (IllegalArgumentException e) {
                        return Mono.error(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
                    }

                    return service.findPrice(req.getProductId(), req.getBrandId(), dateTime)
                            .switchIfEmpty(Mono.error(Status.NOT_FOUND.withDescription("Price not found").asRuntimeException()))
                            .map(price -> PriceResponse.newBuilder()
                                    .setProductId(price.getProductId())
                                    .setBrandId(price.getBrandId())
                                    .setPriceList(price.getPriceList())
                                    .setStartDate(price.getStartDate().toString())
                                    .setEndDate(price.getEndDate().toString())
                                    .setPrice(price.getPrice().doubleValue())
                                    .build());
                })
                .doOnNext(response -> {
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                })
                .doOnError(error -> {
                    log.error("gRPC error: ", error);
                    span.recordException(error);
                    responseObserver.onError(error instanceof StatusRuntimeException
                            ? error
                            : Status.INTERNAL.withDescription(error.getMessage()).asRuntimeException());
                })
                .doFinally(signal -> span.end())
                .subscribe();
    }
}