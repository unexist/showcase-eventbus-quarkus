/**
 * @package Showcase
 *
 * @file Todo created event
 * @copyright 2021 Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/
 
package dev.unexist.showcase.todo.adapter.events;

import dev.unexist.showcase.eventsplit.EventSplitEvent;

public class TodoCreated extends EventSplitEvent {
    private int id;

    public TodoCreated() {
    }

    public TodoCreated(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
