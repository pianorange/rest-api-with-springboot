package me.purefire.restapiwithspring.events;

import me.purefire.restapiwithspring.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

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
            // body(errors) - > json return   X ( 그래서 ErrorsSerializer class 추가)
            //event는 JavaBeanSpec 준수 -> ObjectMapper사용　BeanSerializer 사용해서 Json변환
            //errors는 별도 ErrorsSerializer 구현 필요
            return getBadRequestResponseEntity(errors);
        }

        eventValidator.validate(eventDto, errors);

        if (errors.hasErrors()) {
            return getBadRequestResponseEntity(errors);
        }

        /*
        Event event = Event.builder()
            .name(eventDto.getName())
            .description(eventDto.getDescription())
            .build();
        */
        // 代わりにModelMapper使えるが、JavaReflection使うので性能を考慮した場合、上記の方法がまし
        Event event = modelMapper.map(eventDto, Event.class);
       //ReqParameterに渡された値を基準にしてFree項目を設定
        event.update();
        Event newEvent = this.eventRepository.save(event);

        //Spring HATEOS  linkTo : location URI 생성위해  제공하는 메소드
        //@RequestMapping(value = "/api/events"의 값을 가져와서 URL 생성
        ControllerLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();

        // body(event) - > json return
        EventResource eventResource = new EventResource(event);
       // eventResource.add(new Link());
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        //Rel に含まれるURLはHTTP　METHODによって動作が決めれられるのでself,update-eventは同じLink
        //selfLinkBuilder.withSelfRel() は共通処理になりうる要素なのでeventResourceClass の中に実装
        //eventResource.add(selfLinkBuilder.withSelfRel());
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        eventResource.add(new Link("http://localhost:8080/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createdUri).body(eventResource);
    }

    /**
     * Pageable : Abstract interface for pagination information.
     * @param pageable pageable
     * @return ResponseEntity result
     */
    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> eventPagedResourcesAssembler) {
        Page<Event> page = this.eventRepository.findAll(pageable);
        //repository 에서 받아온 page를 리소스로 변경(페이지에 관련된 링크도 같이 생성해줌 ex ) prev, next, last, first 등)
        //var pageResources = eventPagedResourcesAssembler.toResource(page);
        //but 한건에 대한 링크가 포함 되지 않으므로 완벽한 haiteos 가 아님
        var pageResources = eventPagedResourcesAssembler.toResource(page, e -> new EventResource(e));
        pageResources.add(new Link("http://localhost:8080/docs/index.html#resources-events-list").withRel("profile"));
        return ResponseEntity.ok(pageResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if(optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);
        eventResource.add(new Link("http://localhost:8080/docs/index.html#resources-events-get").withRel("profile"));
        return ResponseEntity.ok(eventResource);
    }

    /**
     *
     * request 본문을 받으니까 @RequestBody
     * EventDto validation 위해 @Valid
     * eventValidator.validate(eventDto, errors); 형태로 hasErrors()체크가능
     * error발생시 스프링이 errors에 담아줌
     * @param id
     * @param eventDto
     * @return
     */
    @PutMapping("{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                      @RequestBody @Valid EventDto eventDto,
                                      Errors errors) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);

        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (errors.hasErrors()) {
            return getBadRequestResponseEntity(errors);
        }

        this.eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return getBadRequestResponseEntity(errors);
        }

        Event existingEvent = optionalEvent.get();

        //map (from , to ) parameter
        this.modelMapper.map(eventDto, existingEvent);
        Event updatedEvent = this.eventRepository.save(existingEvent);
        EventResource eventResource = new EventResource(updatedEvent);
        eventResource.add(new Link("http://localhost:8080/docs/index.html#resources-events-update").withRel("profile"));
        return ResponseEntity.ok(eventResource);
    }

    private ResponseEntity getBadRequestResponseEntity(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

}
