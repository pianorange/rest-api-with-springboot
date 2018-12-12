package me.purefire.restapiwithspring.events;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

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
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }


    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
        //@Valid 어노테이션은 값 바인딩시 검증수행하고 문재있는경우 Errors 변수에 파라메터를 보내준다
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        eventValidator.validate(eventDto, errors);

        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        /*
        Event event = Event.builder()
            .name(eventDto.getName())
            .description(eventDto.getDescription())
            .build();
        */
        // 代わりにModelMapper使えるが、JavaReflection使うので性能を考慮した場合、上記の方法がまし
        Event event = modelMapper.map(eventDto, Event.class);

        Event newEvent = this.eventRepository.save(event);

        //location URI 생성위해 HATEOS가 제공하는 메소드사용
        URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
        return ResponseEntity.created(createdUri).body(event);
    }

}
