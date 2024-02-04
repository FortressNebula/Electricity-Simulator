package com.nebula.electricity.content.events;

import java.util.ArrayList;

public abstract class Event {
    protected ArrayList<Observer> observers = new ArrayList<>();

    public void add (Observer o) {
        observers.add(o);
    }

    public void post () {
        observers.forEach(o -> o.notify(getName()));
    }

    protected abstract String getName ();
}
