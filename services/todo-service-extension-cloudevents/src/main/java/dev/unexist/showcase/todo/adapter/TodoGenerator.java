/**
 * @package Showcase
 *
 * @file Todo generator
 * @copyright 2021 Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/
 
package dev.unexist.showcase.todo.adapter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.unexist.showcase.eventsplit.EventSplitEvent;
import dev.unexist.showcase.todo.adapter.events.TodoCreated;
import dev.unexist.showcase.todo.adapter.events.TodoDone;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@ApplicationScoped
public class TodoGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(TodoGenerator.class);
    private static final int DELAY_IN_SECONDS = 5;

    private final Random random = new Random();
    private final ObjectMapper mapper;

    private final List<EventSplitEvent> eventsList = List.of(
            new TodoCreated(1),
            new TodoDone(1),
            new TodoCreated(2),
            new TodoDone(2),
            new TodoCreated(3),
            new TodoDone(3));

    /**
     * Constructor to init the {@link ObjectMapper}
     **/

    TodoGenerator() {
        this.mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule())
                .registerModule(io.cloudevents.jackson.JsonFormat.getCloudEventJacksonModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Event generator that generates random events in an interval of {@code DELAY_IN_SECONDS}
     *
     * @return The generated {@link Record}
     **/

    @Outgoing("todo-out")
    public Multi<Record<Integer, CloudEvent>> generateEvents() {
        return Multi.createFrom()
                .ticks()
                .every(Duration.ofSeconds(DELAY_IN_SECONDS))
                .onOverflow().drop()
                .map(tick -> {
                    int idx = random.nextInt(eventsList.size());
                    EventSplitEvent event = eventsList.get(idx);

                    CloudEvent cloudEvent = this.convertToCloudEvent(event);

                    LOGGER.info("Sent idx={}, type={}", idx, event.getClass().getSimpleName());

                    return Record.of(idx, cloudEvent);
                });
    }

    /**
     * Convert given data to into a {@link CloudEvent}
     *
     * @param  data  The data to covert, must be convertable via {@link ObjectMapper}
     *
     * @return The created event
     **/

    private CloudEvent convertToCloudEvent(Object data) {
        CloudEventBuilder template = CloudEventBuilder.v1()
                .withSource(URI.create("https://unexist.dev"))
                .withType(data.getClass().getSimpleName());

        PojoCloudEventData<Object> cloudEventData = PojoCloudEventData.wrap(data,
                this.mapper::writeValueAsBytes);

        return template.newBuilder()
                .withId(UUID.randomUUID().toString())
                .withData(cloudEventData)
                .build();
    }
}