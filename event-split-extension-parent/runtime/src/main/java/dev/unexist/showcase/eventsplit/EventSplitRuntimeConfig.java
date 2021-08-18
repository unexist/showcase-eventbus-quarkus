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
public class EventSplitRuntimeConfig {

    /**
     * List of topics to listen to
     **/

    @ConfigItem(name = "topics", defaultValue = "", defaultValueDocumentation = "List of topics")
    public String topics;

    /**
     * List of topics to listen to
     **/

    @ConfigItem(name = "broker.server", defaultValue = "localhost:9092", defaultValueDocumentation = "Host of Kafka broker with port")
    public String brokerServer;
}
