package com.nebula.electricity.foundation.events;

import java.util.ArrayList;

/**
 * Class representing a simple event that provides no context for the event. Observers can attach simple
 * runnables to be executed when the event fires.
 */
public class SimpleEvent {
    protected ArrayList<Runnable> onNotify = new ArrayList<>();

    public void add (Runnable r) {
        onNotify.add(r);
    }

    public void post () {
        onNotify.forEach(Runnable::run);
    }
}
