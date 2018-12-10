package me.purefire.restapiwithspring.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests_SpringRunner {

    //@SpringBootTest 디폴트 변수중 webEnvironment() default SpringBootTest.WebEnvironment.MOCK;
    //가지고있기 때문에 Mocking을 한 dispatcherServlet생성되도록 설정되있으므로
    //@AutoConfigureMockMvc 로 MockMvc사용 가능
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void createEvent() throws Exception {

        Integer reqId = 20;
        Event event = Event.builder()
                .id(reqId)
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
                .eventStatus(EventStatus.PUBLISHED)
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
                .andExpect(jsonPath("id").value(Matchers.not(reqId)));//処理に必要なparameter 以外は無視しているか
    }


}

