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

import io.quarkus.vertx.ConsumeEvent;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TodoSink {
    private static final Logger LOGGER = LoggerFactory.getLogger(TodoSink.class);

    @Channel("todo_out")
    Emitter<String> emitter;

    @ConsumeEvent("todo_in1")
    public void consumeTodoIn1(String message) {
        LOGGER.info("consumeTest1: {}", message);

        this.emitter.send(message);
    }

    @ConsumeEvent("todo_in2")
    public void consumeTodoIn2(String message) {
        LOGGER.info("consumeTest2: {}", message);

        this.emitter.send(message);
    }

    @ConsumeEvent("todo_in3")
    public void consumeTodoIn3(String message) {
        LOGGER.info("consumeTest3: {}", message);

        this.emitter.send(message);
    }
}