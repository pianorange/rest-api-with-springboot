package me.purefire.restapiwithspring.events;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    public void createEvent() throws Exception {
        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                )
                .andExpect(status().isCreated());
    }




}

