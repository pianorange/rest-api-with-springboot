package me.purefire.restapiwithspring.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Ignore annotation 使ってテストメソッド含んだテストクラスじゃないことを宣言
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
@Ignore
public class BaseControllerTest {

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
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ModelMapper modelMapper;

    @Autowired
    ErrorsSerializer errorsSerializer;

}
