/**
 * @package Showcase
 *
 * @file Dispatched event recorder
 * @copyright 2021-present Christoph Kappel <christoph@unexist.dev>
 * @version $Id\$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/
 
package dev.unexist.showcase.eventsplit;

import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class EventSplitRecorder {
    public void addEventType(BeanContainer beanContainer, String eventType) {
        EventSplitRegistry registry =
                beanContainer.instance(EventSplitRegistry.class);

        registry.add(createClass(eventType));
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends EventSplitEvent> createClass(String name) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            return (Class<? extends EventSplitEvent>) classLoader.loadClass(name);
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
