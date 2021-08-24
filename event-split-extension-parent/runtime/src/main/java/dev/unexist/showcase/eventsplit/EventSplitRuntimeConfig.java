/**
 * @package Showcase
 *
 * @file Event split config
 * @copyright 2021 Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.eventsplit;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(phase = ConfigPhase.RUN_TIME, name = "event-split")
@SuppressWarnings("VisibilityModifier")
public class EventSplitRuntimeConfig {

    /**
     * List of topics to subscribe
     **/

    @ConfigItem(name = "topics", defaultValue = "",
            defaultValueDocumentation = "List of topics")
    public String topics;

    /**
     * Address and port of the broker
     **/

    @ConfigItem(name = "broker.server", defaultValue = "localhost:9092",
        defaultValueDocumentation = "Address and port of the Kafka broker")
    public String brokerServer;

    /**
     * Enable cloudevents
     **/

    @ConfigItem(name = "cloudevents.enabled", defaultValue = "false",
        defaultValueDocumentation = "Whether to enable cloudevents")
    public Boolean useCloudEvents;
}
