/**
 * @package Showcase
 * @file Event split sink
 * @copyright 2021 Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 *         This program can be distributed under the terms of the Apache License v2.0.
 *         See the file LICENSE for details.
 **/

package dev.unexist.showcase.messaging.connectors;

import org.eclipse.microprofile.reactive.messaging.Message;

import java.util.List;

public interface EventSplitSink<T> {

    /**
     * @return the channel name.
     */

    String name();

    /**
     * @return the list, potentially empty, of the received messages. The implementation must return a copy of the list.
     *         The {@link #clear()} method allows flushing the list.
     **/

    List<? extends Message<T>> received();

    /**
     * Clears the list of received messages. It also reset the received failure (if any) and the received completion
     * event.
     **/

    void clear();

    /**
     * @return {@code true} if the channel received the completion event.
     **/

    boolean hasCompleted();

    /**
     * @return {@code true} if the channel received the failure event.
     **/

    boolean hasFailed();

    /**
     * @return the failure if {@link #hasFailed()} returned {@code true}.
     **/

    Throwable getFailure();
}
