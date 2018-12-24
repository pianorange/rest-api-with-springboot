package me.purefire.restapiwithspring.index;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.purefire.restapiwithspring.common.ErrorsSerializer;
import me.purefire.restapiwithspring.common.RestDocsConfiguration;
import me.purefire.restapiwithspring.common.TestDescription;
import me.purefire.restapiwithspring.events.EventDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
public class IndexControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ErrorsSerializer errorsSerializer;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void index() throws Exception{
        this.mockMvc.perform(get("/api/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.events").exists());
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

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                //jsonarray に変換されている状態なのでResourceオブジェクトのUnWrrapedが適用されないためcontentの配下に結果が結果が含まれている。
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                //error has been occurred, expect that return index link
                .andExpect(jsonPath("_links.index").exists());
    }

}
