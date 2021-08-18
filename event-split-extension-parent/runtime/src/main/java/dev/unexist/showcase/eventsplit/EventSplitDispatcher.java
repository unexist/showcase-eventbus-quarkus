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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

@ApplicationScoped
public class EventSplitDispatcher {
    private final static ObjectMapper MAPPER = new ObjectMapper();
    private KafkaConsumer<String, String> consumer;

    @Inject
    EventSplitRuntimeConfig config;

    @Inject
    EventBus bus;

    /**
     * Start the dispatcher
     **/

    public void start() {
        Vertx vertx = CDI.current().select(Vertx.class).get();

        System.out.println("Init event dispatcher: vertx=" + vertx + ", config=" + this.config);

        Objects.requireNonNull(this.config, "Config cannot be null");

        /* Create consumer */
        Map<String, String> consumerConfig = new HashMap<>();

        consumerConfig.put("bootstrap.servers", this.config.brokerServer);
        consumerConfig.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerConfig.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerConfig.put("group.id", "eventSplitGroup");
        consumerConfig.put("auto.offset.reset", "earliest");
        consumerConfig.put("enable.auto.commit", "false");

        this.consumer = KafkaConsumer.create(vertx, consumerConfig);

        /* Subscribe to topics */
        if (null != this.config.topics) {
            System.out.println("Subscribe to topics: " + this.config.topics);

            this.consumer.subscribe(new HashSet<>(Arrays.asList(this.config.topics.split(","))));
        }

        /* Set up handler */
        this.consumer.handler(record -> {
            String typeName = StringUtils.EMPTY;

            System.out.println("Handle message " + record.value());

            try {
                JsonNode json = MAPPER.readTree(record.value());

                typeName = json.get("type").asText();
            } catch (JsonProcessingException e) {
                System.out.println("Error reading JSON " + e);
            }

            this.bus.send(typeName, record.value());
        });
    }
}
