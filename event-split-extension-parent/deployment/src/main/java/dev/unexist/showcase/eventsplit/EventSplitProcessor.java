/**
 * @package Showcase
 *
 * @file Extension processor
 * @copyright 2021-present Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.eventsplit;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;

import static io.quarkus.deployment.annotations.ExecutionTime.RUNTIME_INIT;

public class EventSplitProcessor {
    private static final String EVENT_SPLIT = "event-split";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(EVENT_SPLIT);
    }

    @BuildStep
    AdditionalBeanBuildItem registerBeans() {
        return AdditionalBeanBuildItem.builder()
                .addBeanClass(EventSplitLifecycle.class)
                .addBeanClass(EventSplitDispatcher.class)
                .addBeanClass(EventSplitRegistry.class)
                .build();
    }

    @BuildStep
    @Record(RUNTIME_INIT)
    void discoverEvents(EventSplitRecorder recorder,
                        BeanArchiveIndexBuildItem beanArchiveIndex,
                        BeanContainerBuildItem beanContainer)
    {
        beanArchiveIndex.getIndex()
                .getAllKnownSubclasses(
                        DotName.createSimple(EventSplitEvent.class.getName())).stream()
                            .map(ClassInfo::name)
                            .map(DotName::toString)
                            .forEach(eventType -> recorder.addEventType(beanContainer.getValue(), eventType));
    }
}
