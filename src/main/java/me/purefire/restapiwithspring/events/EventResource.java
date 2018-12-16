package me.purefire.restapiwithspring.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.ResourceSupport;

public class EventResource extends ResourceSupport {

    @JsonUnwrapped
    private Event event;

    public EventResource(Event evnet) {
        this.event = evnet;
    }

    public Event getEvent() {
        return event;
    }



}
