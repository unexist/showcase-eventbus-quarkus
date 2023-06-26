/**
 * @package Showcase-Eventbus-Quarkus
 *
 * @file Todo sink
 * @copyright 2020-present Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.adapter;

import dev.unexist.showcase.todo.adapter.events.TodoCreated;
import dev.unexist.showcase.todo.adapter.events.TodoDone;
import io.quarkus.vertx.ConsumeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TodoSink {
    private static final Logger LOGGER = LoggerFactory.getLogger(TodoSink.class);

    /**
     * Apparently this doesn't work without a proper message codec:
     * Uncaught exception received by Vert.x: java.lang.IllegalArgumentException: No message codec for type: class dev.unexist.showcase.todo.adapter.events.TodoCreated
     **/

//    @Incoming("todo-created")
//    public void consumeTodoCreated(TodoCreated todoCreated) {
//        LOGGER.info("Received todo-created with id={}", todoCreated.getId());
//    }

    @ConsumeEvent("todo-created")
    public void consumeTodoDone(TodoCreated todoCreated) {
        LOGGER.info("Received todo-created with id={}", todoCreated.getId());
    }

    @ConsumeEvent("todo-done")
    public void consumeTodoDone(TodoDone todoDone) {
        LOGGER.info("Received todo-done with id={}", todoDone.getId());
    }
}