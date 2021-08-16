/**
 * @package Showcase-Eventbus-Quarkus
 *
 * @file Event split connector
 * @copyright 2021 Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.messaging.connectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.eventbus.EventBus;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.spi.Connector;
import org.eclipse.microprofile.reactive.messaging.spi.OutgoingConnectorFactory;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.eclipse.microprofile.reactive.streams.operators.SubscriberBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@ApplicationScoped
@Connector(EventSplitConnector.CONNECTOR)
public class EventSplitConnector implements OutgoingConnectorFactory {
    public static final String CONNECTOR = "event-split";

    private final Map<String, EventSplitSinkImpl<?>> sinks = new HashMap<>();

    @Override
    public SubscriberBuilder<? extends Message<?>, Void> getSubscriberBuilder(Config config) {
        String name = config.getOptionalValue("channel-name", String.class)
                .orElseThrow(() -> new IllegalArgumentException("channel-name must be set"));
        return sinks.computeIfAbsent(name, EventSplitSinkImpl::new).sink;
    }

    public <T> EventSplitSink<T> sink(String channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Channel must not be null");
        }
        EventSplitSink<?> sink = sinks.get(channel);
        if (sink == null) {
            throw new IllegalArgumentException("Unknown channel");
        }
        //noinspection unchecked
        return (EventSplitSink<T>) sink;
    }

    private static class EventSplitSinkImpl<T> implements EventSplitSink<T> {
        private static final Logger LOGGER = LoggerFactory.getLogger(EventSplitSinkImpl.class);
        private static final ObjectMapper MAPPER = new ObjectMapper();

        private final SubscriberBuilder<? extends Message<T>, Void> sink;
        private final AtomicReference<Throwable> failure = new AtomicReference<>();
        private final AtomicBoolean completed = new AtomicBoolean();
        private final String name;

        @Inject
        EventBus bus;

        private EventSplitSinkImpl(String name) {
            this.name = name;
            this.sink = ReactiveStreams.<Message<T>> builder()
                    .flatMapCompletionStage(this::handleMessage)
                    .onError(err -> failure.compareAndSet(null, err))
                    .onComplete(() -> completed.compareAndSet(false, true))
                    .ignore();
        }

        private CompletionStage<Void> handleMessage(Message message) {
            String payload = (String) message.getPayload();
            String typeName = StringUtils.EMPTY;

            LOGGER.info("Handle message {}", payload);

            try {
                JsonNode json = MAPPER.readTree(payload);

                typeName = json.get("type").asText();
            } catch (JsonProcessingException e) {
                LOGGER.error("Error reading JSON", e);
            }

            this.bus.send(typeName, message);

            return message.ack();
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public List<? extends Message<T>> received() {
            return Collections.emptyList();
        }

        @Override
        public void clear() {
            completed.set(false);
            failure.set(null);
        }

        @Override
        public boolean hasCompleted() {
            return completed.get();
        }

        @Override
        public boolean hasFailed() {
            return getFailure() != null;
        }

        @Override
        public Throwable getFailure() {
            return failure.get();
        }
    }
}
