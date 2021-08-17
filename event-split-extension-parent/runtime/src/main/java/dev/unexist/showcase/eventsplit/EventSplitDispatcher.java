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
import org.eclipse.microprofile.reactive.messaging.Message;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class EventSplitDispatcher {
    private final static ObjectMapper MAPPER = new ObjectMapper();

    @Inject
    EventBus bus;

    @Channel("todo_in")
    private CompletionStage<Void> dispatchEvents(Message message) {
        String payload = (String) message.getPayload();
        String typeName = StringUtils.EMPTY;

        System.out.println("Handle message " + payload);

        try {
            JsonNode json = MAPPER.readTree(payload);

            typeName = json.get("type").asText();
        } catch (JsonProcessingException e) {
            System.out.println("Error reading JSON " + e);
        }

        this.bus.send(typeName, message);

        return message.ack();
    }
}
