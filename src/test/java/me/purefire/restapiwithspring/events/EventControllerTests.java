package me.purefire.restapiwithspring.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest
public class EventControllerTests {
//Web과 관련된 모든 bean 들이 등록되고 테스트에서 MockMVC주입 받아 사용가능

    //McckMvc 사용하면 웹서버 띄우지 않고도 Mocking되있는 dispatcherServlet 생성해서 요청과 응답확인 가능해 컨트롤러
    //테스트용으로 자주쓰임
    //Web관련 빈만 테스트하는 것이기 때문에(구역별로 계층별로) 슬라이싱 테스트라부름
    //단위테스트라 보기에는 너무 많은 것들이 개입되있음 디스패처서블릿이가진 여러 데이터핸들러, 맵퍼, 컨버터 다 동작하는 테스트라
    //이벤트 컨트롤러 만을 테스트하면 단위테스트
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    //Repository 가져다 쓰려고하면 Web관련 빈만 생성되기 때문에 exception 발생 Mock 만들어 stubbing해줘야 함
    //    @MockBean WebMvcTest 사용 할때 (SpringContainer 이용테스트시 SpringApplicationContext의 빈과 교체해준다)
    //    @Mock     Mockito JUnitRunner 이용 Test
    @MockBean
    EventRepository eventRepository;

    @Test
    public void createEvent() throws Exception {
        //JSON응답으로 201 생성
        // Location헤더에 생성된 이벤트 조회가능한 Uri 담겨있는지확인
        //id DB들어갈때 자동생성된 값으로 나오는지 확인

        Event event = Event.builder()
                .name("Spring")
                .description("REST API Development")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,11,12,10))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,11,14,10))
                .beginEventDateTime(LocalDateTime.of(2018,11,11,12,10))
                .endEventDateTime(LocalDateTime.of(2018,11,11,12,10))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("tokyo sibuya")
                .build();

        event.setId(10);
        Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,"application/hal+json;charset=UTF-8"));

    }




}

