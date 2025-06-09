package org.inditex.prices.infrastructure.adapter.in.grpc;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * A stub implementation of the {@link SpanBuilder} interface for testing purposes.
 * <p>
 * This class provides a minimal implementation that returns a predefined {@link Span}
 * instance and ignores most configuration methods.
 * </p>
 */
public class SpanBuilderStubTest implements SpanBuilder {

    /**
     * The predefined span instance to return on {@link #startSpan()}.
     */
    private final Span span;

    /**
     * Constructs a new {@code SpanBuilderStub} with the given span instance.
     *
     * @param span the {@link Span} instance to be returned by this builder
     */
    public SpanBuilderStubTest(Span span) {
        this.span = span;
    }

    /**
     * Starts the span by returning the predefined span instance.
     *
     * @return the predefined {@link Span} instance
     */
    @Override
    public Span startSpan() {
        return span;
    }

    /**
     * Sets the parent context for the span.
     * <p>
     * This stub ignores the context and returns itself.
     * </p>
     *
     * @param context the parent {@link Context}
     * @return this {@code SpanBuilderStub} instance
     */
    @Override
    public SpanBuilder setParent(Context context) {
        return this;
    }

    /**
     * Removes any parent from the span.
     * <p>
     * This stub ignores this setting and returns itself.
     * </p>
     *
     * @return this {@code SpanBuilderStub} instance
     */
    @Override
    public SpanBuilder setNoParent() {
        return this;
    }

    /**
     * Adds a link to another span.
     * <p>
     * This stub returns {@code null} and does not store the link.
     * </p>
     *
     * @param spanContext the linked span's context
     * @return {@code null}
     */
    @Override
    public SpanBuilder addLink(SpanContext spanContext) {
        return null;
    }

    /**
     * Adds a link to another span with attributes.
     * <p>
     * This stub returns {@code null} and does not store the link or attributes.
     * </p>
     *
     * @param spanContext the linked span's context
     * @param attributes  the attributes associated with the link
     * @return {@code null}
     */
    @Override
    public SpanBuilder addLink(SpanContext spanContext, Attributes attributes) {
        return null;
    }

    /**
     * Sets a string attribute on the span.
     * <p>
     * This stub ignores the attribute and returns itself.
     * </p>
     *
     * @param key   the attribute key
     * @param value the attribute value
     * @return this {@code SpanBuilderStub} instance
     */
    @Override
    public SpanBuilder setAttribute(String key, String value) {
        return this;
    }

    /**
     * Sets a long attribute on the span.
     * <p>
     * This stub ignores the attribute and returns itself.
     * </p>
     *
     * @param key   the attribute key
     * @param value the attribute value
     * @return this {@code SpanBuilderStub} instance
     */
    @Override
    public SpanBuilder setAttribute(String key, long value) {
        return this;
    }

    /**
     * Sets a double attribute on the span.
     * <p>
     * This stub ignores the attribute and returns itself.
     * </p>
     *
     * @param key   the attribute key
     * @param value the attribute value
     * @return this {@code SpanBuilderStub} instance
     */
    @Override
    public SpanBuilder setAttribute(String key, double value) {
        return this;
    }

    /**
     * Sets a boolean attribute on the span.
     * <p>
     * This stub ignores the attribute and returns itself.
     * </p>
     *
     * @param key   the attribute key
     * @param value the attribute value
     * @return this {@code SpanBuilderStub} instance
     */
    @Override
    public SpanBuilder setAttribute(String key, boolean value) {
        return this;
    }

    /**
     * Sets an attribute with a typed key and value on the span.
     * <p>
     * This stub ignores the attribute and returns itself.
     * </p>
     *
     * @param <T>   the type of the attribute value
     * @param key   the typed attribute key
     * @param value the attribute value
     * @return this {@code SpanBuilderStub} instance
     */
    @Override
    public <T> SpanBuilder setAttribute(AttributeKey<T> key, T value) {
        return this;
    }

    /**
     * Sets all attributes from the given {@link Attributes} object.
     * <p>
     * This stub defers to the default implementation.
     * </p>
     *
     * @param attributes the attributes to set
     * @return this {@code SpanBuilderStub} instance
     */
    @Override
    public SpanBuilder setAllAttributes(Attributes attributes) {
        return SpanBuilder.super.setAllAttributes(attributes);
    }

    /**
     * Sets the kind of span.
     * <p>
     * This stub ignores the span kind and returns itself.
     * </p>
     *
     * @param spanKind the {@link SpanKind} to set
     * @return this {@code SpanBuilderStub} instance
     */
    @Override
    public SpanBuilder setSpanKind(SpanKind spanKind) {
        return this;
    }

    /**
     * Sets the start timestamp with given time and unit.
     * <p>
     * This stub returns {@code null} and does not set the timestamp.
     * </p>
     *
     * @param l        the timestamp value
     * @param timeUnit the unit of the timestamp
     * @return {@code null}
     */
    @Override
    public SpanBuilder setStartTimestamp(long l, TimeUnit timeUnit) {
        return null;
    }

    /**
     * Sets the start timestamp with given {@link Instant}.
     * <p>
     * This stub defers to the default implementation.
     * </p>
     *
     * @param startTimestamp the start timestamp as {@link Instant}
     * @return this {@code SpanBuilderStub} instance
     */
    @Override
    public SpanBuilder setStartTimestamp(Instant startTimestamp) {
        return SpanBuilder.super.setStartTimestamp(startTimestamp);
    }
}
