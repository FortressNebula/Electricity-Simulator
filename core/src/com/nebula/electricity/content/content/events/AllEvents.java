package com.nebula.electricity.content.content.events;

import com.nebula.electricity.content.events.Event;

public class AllEvents {
    public static Event INIT = new Event() {
        @Override
        protected String getName () {
            return "init";
        }
    };
}
