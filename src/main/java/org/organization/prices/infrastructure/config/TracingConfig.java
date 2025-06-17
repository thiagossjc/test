package org.organization.prices.infrastructure.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.zipkin.ZipkinSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up OpenTelemetry with Zipkin exporter.
 */
@Configuration
public class TracingConfig {

    /**
     * Logger instance for logging tracing configuration events.
     */
    private static final Logger logger = LoggerFactory.getLogger(TracingConfig.class);

    /**
     * Name of the service for tracing, read from application properties.
     */
    @Value("${spring.application.name:organization-prices}")
    private String serviceName;

    /**
     * Zipkin endpoint URL for sending trace data, read from application properties.
     */
    @Value("${opentelemetry.zipkin.endpoint:http://localhost:9411/api/v2/spans}")
    private String zipkinEndpoint;

    /**
     * Attribute key used for setting the service name in trace resources.
     */
    private static final AttributeKey<String> SERVICE_NAME_KEY = AttributeKey.stringKey("service.name");

    /**
     * Creates and configures the OpenTelemetry instance with Zipkin exporter.
     *
     * @return configured OpenTelemetry instance
     */
    @Bean
    public OpenTelemetry openTelemetry() {
        ZipkinSpanExporter zipkinExporter = ZipkinSpanExporter.builder()
                .setEndpoint(zipkinEndpoint)
                .build();

        Resource serviceResource = Resource.getDefault().merge(
                Resource.create(Attributes.of(SERVICE_NAME_KEY, serviceName))
        );

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(SimpleSpanProcessor.create(zipkinExporter))
                .setResource(serviceResource)
                .build();

        OpenTelemetrySdk openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .build();

        logger.info("OpenTelemetry initialized for service '{}', sending to '{}'", serviceName, zipkinEndpoint);
        return openTelemetry;
    }

    /**
     * Provides a tracer instance for the configured service.
     *
     * @param openTelemetry the OpenTelemetry instance
     * @return tracer instance for the service
     */
    @Bean
    public Tracer tracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer(serviceName);
    }
}
