/**
 * @package Showcase
 *
 * @file Extension lifecycle
 * @copyright 2021-present Christoph Kappel <christoph@unexist.dev>
 * @version Id
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.eventsplit;

import io.quarkus.runtime.StartupEvent;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.DefinitionException;
import jakarta.enterprise.inject.spi.DeploymentException;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptor;

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
