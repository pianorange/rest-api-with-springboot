package me.purefire.restapiwithspring.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
// 클래스 안의 모든 메소드는 produces = MediaTypes.HAL_JSON_UTF8_VALUE 응답함
public class EventController {

    //DI 2가지
    // 1. Autowired
    //    @Autowired
    //    EventRepository eventRepository;

    // 2. 생성자(since Spring4.3)
    // 생성자가 한개고 받아올 파라메터가 이미 bean 등록 되어있다면 autowired 필요 없음
    private final EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody Event event) {

        Event newEvent = this.eventRepository.save(event);

        //location URI 생성위해 HATEOS가 제공하는 메소드사용
        URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
        return ResponseEntity.created(createdUri).body(event);
    }

}
