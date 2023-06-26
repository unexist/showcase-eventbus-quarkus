/**
 * @package Showcase
 *
 * @file Dispatched event registry
 * @copyright 2021-present Christoph Kappel <christoph@unexist.dev>
 * @version $Id\$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/
 
package dev.unexist.showcase.eventsplit;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@ApplicationScoped
public class EventSplitRegistry {
    private final ConcurrentMap<String, Class<? extends EventSplitEvent>> eventTypes;

    public EventSplitRegistry() {
        this.eventTypes = new ConcurrentHashMap<>();
    }

    void add(Class<? extends EventSplitEvent> eventType) {
        String name = eventType.getSimpleName();

        eventTypes.put(name, eventType);
    }

    @SuppressWarnings("unchecked")
    <T extends EventSplitEvent> Optional<Class<T>> findType(String name) {
        return Optional.ofNullable((Class<T>) eventTypes.get(name));
    }
}
