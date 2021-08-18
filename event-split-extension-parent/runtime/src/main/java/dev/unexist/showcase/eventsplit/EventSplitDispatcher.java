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
import io.vertx.core.eventbus.EventBus;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.reactive.messaging.Channel;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class EventSplitDispatcher {
    private final static ObjectMapper MAPPER = new ObjectMapper();

    @Inject
    EventBus bus;

    EventSplitDispatcher() {
        System.out.println("Init event dispatcher");
    }

    @Channel("todo_in")
    private void dispatchEvents(String message) {
        String typeName = StringUtils.EMPTY;

        System.out.println("Handle message " + message);

        try {
            JsonNode json = MAPPER.readTree(message);

            typeName = json.get("type").asText();
        } catch (JsonProcessingException e) {
            System.out.println("Error reading JSON " + e);
        }

        this.bus.send(typeName, message);
    }
}
