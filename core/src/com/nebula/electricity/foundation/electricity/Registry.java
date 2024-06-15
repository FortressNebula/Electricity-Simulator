package com.nebula.electricity.foundation.electricity;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Registry<A, B> {
    final HashMap<A, B> all;
    final List<A> toDelete;

    public Registry () {
        all = new HashMap<>();
        toDelete = new ArrayList<>();
    }

    abstract A makeID (B component);
    void onAdded (A id) {}
    void onDeleted (A id) {}
    void onClear () {}

    public void add (A id, B component) {
        all.put(id, component);
        onAdded(id);
    }

    public void add (B component) {
        add(makeID(component), component);
    }

    public void add (List<? extends B> components) {
        for (B component : components)
            add(makeID(component), component);
    }

    public void delete (A id) {
        all.remove(id);
        onDeleted(id);
    }
    public void delete (List<? extends B> components) {
        for (B component : components) {
            delete(makeID(component));
        }
    }

    public void queueDelete (A id) {
        toDelete.add(id);
    }

    public void flush () {
        for (A id : toDelete)
            delete(id);
        toDelete.clear();
    }

    public void clear () {
        all.clear();
        onClear();
    }

    public Collection<B> getAll () {
        return all.values();
    }

    public Set<A> getAllIDs () { return all.keySet(); }

    public B get (A id) {
        return all.get(id);
    }

    public <T extends B> List<T> getSubset (Class<T> clazz) {
        return all.values().stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toList());
    }
}
