/**
 * @package Showcase
 *
 * @file Todo done event
 * @copyright 2021-present Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/
 
package dev.unexist.showcase.todo.adapter.events;

import dev.unexist.showcase.eventsplit.EventSplitEvent;

public class TodoDone extends EventSplitEvent {
    private int id;

    public TodoDone() {
    }

    public TodoDone(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
