/**
 * @package Showcase
 *
 * @file Extension lifecycle
 * @copyright 2021 Christoph Kappel <christoph@unexist.dev>
 * @version Id
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.eventsplit;

import io.quarkus.runtime.StartupEvent;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.DefinitionException;
import javax.enterprise.inject.spi.DeploymentException;
import javax.inject.Inject;
import javax.interceptor.Interceptor;

@Dependent
public class EventSplitLifecycle {

    @Inject
    EventSplitDispatcher dispatcher;

    /**
     * Handle application start
     *
     * @param  event  The handled event
     **/

    void onApplicationStart(@Observes @Priority(Interceptor.Priority.LIBRARY_BEFORE) StartupEvent event) {
        try {
            this.dispatcher.start();
        } catch (Exception e) {
            if (e instanceof DeploymentException || e instanceof DefinitionException) {
                throw e;
            }
            throw new DeploymentException(e);
        }
    }
}
