package me.purefire.restapiwithspring.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.purefire.restapiwithspring.common.ErrorsSerializer;
import me.purefire.restapiwithspring.common.RestDocsConfiguration;
import me.purefire.restapiwithspring.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
public class EventControllerTests_SpringBootTest {

    //McckMvc 사용하면 웹서버 띄우지 않고도 Mocking되있는 dispatcherServlet 생성해서 요청과 응답확인 가능해 컨트롤러
    //테스트용으로 자주쓰임
    //Web관련 빈만 테스트하는 것이기 때문에(구역별로 계층별로) 슬라이싱 테스트라부름
    //단위테스트라 보기에는 너무 많은 것들이 개입되있음 디스패처서블릿이가진 여러 데이터핸들러, 맵퍼, 컨버터 다 동작하는 테스트라
    //이벤트 컨트롤러 만을 테스트하면 단위테스트

    //Repository 가져다 쓰려고하면 Web관련 빈만 생성되기 때문에 exception 발생 Mock 만들어 stubbing해줘야 함
    //    @MockBean WebMvcTest 사용 할때 (SpringContainer 이용테스트시 SpringApplicationContext의 빈과 교체해준다)
    //    @Mock     Mockito JUnitRunner 이용 Test

    //@SpringBootTest 디폴트 변수중 webEnvironment() default SpringBootTest.WebEnvironment.MOCK;
    //가지고있기 때문에 Mocking을 한 dispatcherServlet생성되도록 설정되있으므로
    //@AutoConfigureMockMvc 로 MockMvc사용 가능
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ErrorsSerializer errorsSerializer;


    //Repository 가져다 쓰려고하면 Web관련 빈만 생성되기 때문에 exception 발생 Mock 만들어 stubbing해줘야 함
    //    @MockBean WebMvcTest 사용 할때 (SpringContainer 이용테스트시 SpringApplicationContext의 빈과 교체해준다)
    //    @Mock     Mockito JUnitRunner 이용 Test

    @Test
    @TestDescription("정상적인 테스트")
    public void createEvent() throws Exception {

        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,10,12,10))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,10,14,10))
                .beginEventDateTime(LocalDateTime.of(2018,11,11,12,10))
                .endEventDateTime(LocalDateTime.of(2018,11,11,12,10))
                .basePrice(10)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("tokyo sibuya")
                .build();

        System.out.printf(objectMapper.writeValueAsString(event));

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event))
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,"application/hal+json;charset=UTF-8"))
                .andExpect(jsonPath("free").value(Matchers.not(true)))//処理に必要なparameter 以外は無視しているか
                .andExpect(jsonPath("id").value(Matchers.not("20")));//処理に必要なparameter 以外は無視しているか
    }

    /**
     * SpringBoot properties 파일 이용 ObjectMapper 확장
     * 원치 않는 값이 포함된 리퀘스트가 오면 Bad Request를 Response
     *
     * BedRequest vs 받기로 한 값 이외에는 무시(필요한 항목만 가진 BeanClass만들어서
     *
     * deserialization할때 알려지지 않은 프로퍼티가 넘어오면 실패하라
     * spring.jackson.deserialization.fail-on-unknown-properties=true
     * json to Object  -> deserialization
     * Object to json -> serialization
     * 반대로 serialization시 설정도 있음.
     * spring.jackson.serialization.fail-on-empty-beans=
     * @throws Exception perform 예외
     */
    @Test
    @TestDescription("입력 받을 수 없는 값을 사용한 경우에 에러 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {

        Event event = Event.builder()
                .id(20)
                .name("Spring")
                .description("REST API Development")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,11,12,10))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,11,14,10))
                .beginEventDateTime(LocalDateTime.of(2018,11,11,12,10))
                .endEventDateTime(LocalDateTime.of(2018,11,11,12,10))
                .basePrice(100)
                .maxPrice(200)
                .free(true)
                .limitOfEnrollment(100)
                .location("tokyo sibuya")
                .build();

        System.out.printf(objectMapper.writeValueAsString(event));

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event))
        )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    /**
     * BindingResult는 항상 어노테이션Valid 바로 다음 인자로 사용(스프링 MVC)
     * Bean에 값들을 바인딩시 Valid는 JS303이용 값 검증 가능
     * 어노테이션 NotNull, NotEmpty, Min, Max ... 사용 입력값 바인딩할 때 에러 확인 가능
     *
     * 도메인 Validator 만들기
     * Validator 인터페이스 사용하기 (없이 만들어도 상관없음)
     *
     * 테스트 설명 용 어노테이션 만들기
     * Target, Retention
     *
     * 테스트 할 것
     *   입력값이 이상한 경우 에러
     *   비즈니스 로직으로 검사할 수 있는 에러
     *   에러 응답 메시지에 에러에 대한 정보가 담겨있어야 한다.
     *
     * @throws Exception perform 예외
     */
    @Test
    @TestDescription("입력값이 비어있는 경우에 에러 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(this.objectMapper.writeValueAsString(eventDto))
        )
                    .andExpect(status().isBadRequest());
    }

    /**
     *カスタムValidatorを使ったバリデーション
     * 前後チェック
     * Min Max チェック
     *
     * @throws Exception perform 예외
     */
    @Test
    @TestDescription("입력값이 잘못된 이벤트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {

        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development")
                //StartDate > EndDate
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,12,12,10))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,11,14,10))
                .beginEventDateTime(LocalDateTime.of(2018,11,11,12,10))
                .endEventDateTime(LocalDateTime.of(2018,11,12,12,10))
                //basePrice > MaxPrice
                .basePrice(1000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("tokyo sibuya")
                .build();

        //
        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists());
    }

    @Test
    @TestDescription("정상적인 테스트")
    public void createEvent_BussinessLogic() throws Exception {

        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,10,12,10))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,10,14,10))
                .beginEventDateTime(LocalDateTime.of(2018,11,11,12,10))
                .endEventDateTime(LocalDateTime.of(2018,11,11,12,10))
                .basePrice(10)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("tokyo sibuya")
                .build();

        System.out.printf(objectMapper.writeValueAsString(event));

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event))
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,"application/hal+json;charset=UTF-8"))
                .andExpect(jsonPath("free").value(false))//処理に必要なparameter 以外は無視しているか
                .andExpect(jsonPath("offline").value(true))//処理に必要なparameter 以外は無視しているか
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()));//処理に必要なparameter 以外は無視しているか
    }

    @Test
    @TestDescription("정상적인 테스트")
    public void createEvent_HATEOS() throws Exception {

        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,10,12,10))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,10,14,10))
                .beginEventDateTime(LocalDateTime.of(2018,11,11,12,10))
                .endEventDateTime(LocalDateTime.of(2018,11,11,12,10))
                .basePrice(10)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("tokyo sibuya")
                .build();

        System.out.printf(objectMapper.writeValueAsString(event));

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event))
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,"application/hal+json;charset=UTF-8"))
                .andExpect(jsonPath("free").value(false))//処理に必要なparameter 以外は無視しているか
                .andExpect(jsonPath("offline").value(true))//処理に必要なparameter 以外は無視しているか
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))//処理に必要なparameter 以外は無視しているか
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("create-event",
                        links(
                             linkWithRel("self").description("link to self"),
                             linkWithRel("query-events").description("link to query events"),
                             linkWithRel("update-event").description("link to update event")
                        ),
                        requestHeaders(
                             headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                             headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
                        ),
                        requestFields(
                             fieldWithPath("name").description("name of new event"),
                             fieldWithPath("description").description("description of new event"),
                             fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment"),
                             fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment"),
                             fieldWithPath("beginEventDateTime").description("date time of begin event"),
                             fieldWithPath("endEventDateTime").description("date time of end event"),
                             fieldWithPath("location").description("location of event"),
                             fieldWithPath("basePrice").description("basePrice of event"),
                             fieldWithPath("maxPrice").description("maxPrice of event"),
                             fieldWithPath("limitOfEnrollment").description("limit of enrollment")
                        ),
                        responseHeaders(
                             headerWithName(HttpHeaders.LOCATION).description("Location Header"),
                             headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        //in this case not include _links{ ] So Caused SnippetException
                        //org.springframework.restdocs.snippet.SnippetException: The following parts of the payload were not documented:
                        //resresponseFields() <- must include all response field
                        relaxedResponseFields(
                                fieldWithPath("id").description("id of event"),
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment"),
                                fieldWithPath("beginEventDateTime").description("date time of begin event"),
                                fieldWithPath("endEventDateTime").description("date time of end event"),
                                fieldWithPath("location").description("location of event"),
                                fieldWithPath("basePrice").description("basePrice of event"),
                                fieldWithPath("maxPrice").description("maxPrice of event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
                                fieldWithPath("free").description("it tells if this event is free or not"),
                                fieldWithPath("offline").description("it tells if this event is event or not"),
                                fieldWithPath("eventStatus").description("event status")
                        )
                ));
    }

    @Test
    @TestDescription("정상적인 테스트")
    public void createEventForResponseFields() throws Exception {

        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,10,12,10))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,10,14,10))
                .beginEventDateTime(LocalDateTime.of(2018,11,11,12,10))
                .endEventDateTime(LocalDateTime.of(2018,11,11,12,10))
                .basePrice(10)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("tokyo sibuya")
                .build();

        System.out.printf(objectMapper.writeValueAsString(event));

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event))
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,"application/hal+json;charset=UTF-8"))
                .andExpect(jsonPath("free").value(false))//処理に必要なparameter 以外は無視しているか
                .andExpect(jsonPath("offline").value(true))//処理に必要なparameter 以外は無視しているか
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))//処理に必要なparameter 以外は無視しているか
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update event"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
                        ),
                        requestFields(
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment"),
                                fieldWithPath("beginEventDateTime").description("date time of begin event"),
                                fieldWithPath("endEventDateTime").description("date time of end event"),
                                fieldWithPath("location").description("location of event"),
                                fieldWithPath("basePrice").description("basePrice of event"),
                                fieldWithPath("maxPrice").description("maxPrice of event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location Header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("id of event"),
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment"),
                                fieldWithPath("beginEventDateTime").description("date time of begin event"),
                                fieldWithPath("endEventDateTime").description("date time of end event"),
                                fieldWithPath("location").description("location of event"),
                                fieldWithPath("basePrice").description("basePrice of event"),
                                fieldWithPath("maxPrice").description("maxPrice of event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
                                fieldWithPath("free").description("it tells if this event is free or not"),
                                fieldWithPath("offline").description("it tells if this event is event or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("_links.self.href").description("_links to self"),
                                fieldWithPath("_links.query-events.href").description("_links to query-events"),
                                fieldWithPath("_links.update-event.href").description("_links to update-event"),
                                fieldWithPath("_links.profile.href").description("_links to profile")
                        )
                ));
    }
}

