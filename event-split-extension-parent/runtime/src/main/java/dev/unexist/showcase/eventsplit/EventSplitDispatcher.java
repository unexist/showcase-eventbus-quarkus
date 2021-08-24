/**
 * @package Showcase
 *
 * @file Event dispatcher
 * @copyright 2021 Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.eventsplit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.CaseFormat;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.jackson.PojoCloudEventDataMapper;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.consumer.KafkaConsumerRecord;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class EventSplitDispatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventSplitDispatcher.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Inject
    EventSplitRuntimeConfig runtimeConfig;

    @Inject
    EventSplitRegistry registry;

    @Inject
    EventBus bus;

    private final ObjectMapper mapper;
    private KafkaConsumer<String, ?> consumer;

    /**
     * Constructor
     **/

    EventSplitDispatcher() {
        this.mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule())
                .registerModule(io.cloudevents.jackson.JsonFormat.getCloudEventJacksonModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Start the dispatcher
     **/

    public void start() {
        Objects.requireNonNull(this.runtimeConfig, "Config cannot be null");

        Vertx vertx = CDI.current().select(Vertx.class).get();

        LOGGER.info("Init event dispatcher: vertx={}, config={}", vertx, this.runtimeConfig);

        /* Create consumer */
        Map<String, String> consumerConfig = new HashMap<>();

        consumerConfig.put("bootstrap.servers", this.runtimeConfig.brokerServer);
        consumerConfig.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        if (BooleanUtils.isTrue(this.runtimeConfig.useCloudEvents)) {
            consumerConfig.put("value.deserializer", "io.cloudevents.kafka.CloudEventDeserializer");
        } else {
            consumerConfig.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        }

        consumerConfig.put("group.id", "eventSplitGroup");
        consumerConfig.put("auto.offset.reset", "earliest");
        consumerConfig.put("enable.auto.commit", "false");

        this.consumer = KafkaConsumer.create(vertx, consumerConfig);

        /* Subscribe to topics */
        if (null != this.runtimeConfig.topics) {
            this.consumer.subscribe(new HashSet<>(Arrays.asList(this.runtimeConfig.topics.split(","))));

            LOGGER.info("Subscribed to topics: {}", this.runtimeConfig.topics);
        }

        /* Set up handler */
        this.consumer.handler(record -> {
            if (BooleanUtils.isTrue(this.runtimeConfig.useCloudEvents)) {
                this.handleMessageAsCloudEvent((KafkaConsumerRecord<String, CloudEvent>) record);
            } else {
                this.handleMessage((KafkaConsumerRecord<String, String>) record);
            }
        });
    }

    /**
     * Handle the messages from the consumer
     *
     * @param  record  The {@link KafkaConsumerRecord} to handle
     **/

    private void handleMessage(KafkaConsumerRecord<String, String> record) {
        String typeName = StringUtils.EMPTY;

        LOGGER.info("Handle message {}", record.value());

        try {
            JsonNode json = MAPPER.readTree(record.value());

            typeName = json.get("type").asText();
        } catch (JsonProcessingException e) {
            LOGGER.info("Error reading JSON", e);
        }

        this.bus.send(typeName, record.value());
    }

    /**
     * Handle the messages from the consumer
     *
     * @param  record  The {@link KafkaConsumerRecord} to handle
     **/

    private void handleMessageAsCloudEvent(KafkaConsumerRecord<String, CloudEvent> record) {
        CloudEvent cloudEvent = record.value();

        LOGGER.info("Handle message type {}", cloudEvent.getType());

        Optional<Class<EventSplitEvent>> candidate = this.registry.findType(cloudEvent.getType());

        if (candidate.isPresent()) {
            PojoCloudEventData<?> cloudEventData = CloudEventUtils.mapData(
                    cloudEvent,
                    PojoCloudEventDataMapper.from(this.mapper, candidate.get()));

            if (null != cloudEventData && null != cloudEventData.getValue()) {
                String typeName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, cloudEvent.getType());

                LOGGER.info("Sent to {}", typeName);

                this.bus.send(typeName, cloudEventData.getValue());
            }
        } else {
            LOGGER.error("Event type {} not found", cloudEvent.getType());
        }
    }
}
