/**
 * @package Showcase
 *
 * @file Extension processor
 * @copyright 2021 Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.eventsplit;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

public class EventSplitProcessor {
    private static final String EVENT_SPLIT = "event-split";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(EVENT_SPLIT);
    }

    @BuildStep
    AdditionalBeanBuildItem beans() {
        return new AdditionalBeanBuildItem(EventSplitLifecycle.class, EventSplitDispatcher.class);
    }
}
