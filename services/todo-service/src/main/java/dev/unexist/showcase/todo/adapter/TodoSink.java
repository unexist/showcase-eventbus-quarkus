/**
 * @package Showcase-Eventbus-Quarkus
 *
 * @file Todo sink
 * @copyright 2020-present Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.mutiny.core.eventbus.EventBus;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TodoSink {
    private static final Logger LOGGER = LoggerFactory.getLogger(TodoSink.class);

    static final ObjectMapper MAPPER = new ObjectMapper();

    @Inject
    EventBus bus;

    @Channel("todo_out")
    Emitter<String> emitter;

    @Incoming("todo_in")
    public void consumeAll(String message) {
        String typeName = StringUtils.EMPTY;

        LOGGER.info("consumeAll: {}", message);

        try {
            JsonNode json = MAPPER.readTree(message);

            typeName = json.get("type").asText();
        } catch (JsonProcessingException e) {
            LOGGER.error("Error reading JSON", e);
        }

        this.bus.send(typeName, message);
    }

    @ConsumeEvent("todo_in1")
    public void consumeTodoIn1(String message) {
        LOGGER.info("consumeTest1: {}", message);

        this.emitter.send(message);
    }

    @ConsumeEvent("todo_in2")
    public void consumeTodoIn2(String message) {
        LOGGER.info("consumeTest2: {}", message);

        this.emitter.send(message);
    }

    @Incoming("todo_in3")
    public void consumeTodoIn3(String message) {
        LOGGER.info("consumeTest3: {}", message);

        this.emitter.send(message);
    }
}