package me.purefire.restapiwithspring.events;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

public class EventResource extends Resource<Event> {

    public EventResource(Event content, Link... links) {
        super(content, links);
        //add(new Link("http://localhost:8080/api/events/" + content.getId()));
        //上記と同じ結果になるが、よりTypeSafe。また、ControllerのMappingPath変わっても修正不要
        add(linkTo(EventController.class).slash(content.getId()).withSelfRel());
    }
}
