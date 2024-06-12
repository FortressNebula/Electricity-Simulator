package com.nebula.electricity.foundation.electricity;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ComponentManager<A, B> {
    final HashMap<A, B> all;
    final List<A> toDelete;
    private Function<B, A> idFunction;

    public ComponentManager () {
        all = new HashMap<>();
        toDelete = new ArrayList<>();
        idFunction = $ -> { throw new IllegalStateException("Cannot get ID!"); };
    }

    public void addIDFunction (Function<B, A> idFunction) {
        this.idFunction = idFunction;
    }

    public void add (A id, B component) { all.put(id, component); }

    public void add (B component) { all.put(idFunction.apply(component), component); }
    public void add (List<? extends B> components) {
        for (B component : components)
            all.put(idFunction.apply(component), component);
    }

    public void delete (A id) { all.remove(id); }
    public void delete (List<? extends B> components) {
        for (B component : components)
            all.remove(idFunction.apply(component));
    }

    public void queueDelete (A id) {
        toDelete.add(id);
    }

    public void flush () {
        for (A id : toDelete)
            all.remove(id);
        toDelete.clear();
    }

    public void clear () {
        all.clear();
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
