package me.jooomout.demorestapistudy.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Description;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


//@WebMvcTest // for slicing test
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("정상적인 이벤트 생성")
    void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API - SPRING")
                .beginEnrollmentDateTime(LocalDateTime.of(2021, 9, 29, 23, 36))
                .closeEnrollmentDateTime(LocalDateTime.of(2021, 9, 30, 01, 30))
                .beginEventDateTime(LocalDateTime.of(2021, 10, 1, 0,0,0))
                .endEventDateTime(LocalDateTime.of(2021, 10, 3, 23,59,59))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("중앙대")
                .build();


        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(Matchers.not(true)))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
        ;
    }

    @Test
    void createEvent_Bad_Request() throws Exception {
        Event event = new Event().builder()
                .id(100)
                .name("Spring")
                .description("REST API - SPRING")
                .beginEnrollmentDateTime(LocalDateTime.of(2021, 9, 29, 23, 36))
                .closeEnrollmentDateTime(LocalDateTime.of(2021, 9, 30, 01, 30))
                .beginEventDateTime(LocalDateTime.of(2021, 10, 1, 0,0,0))
                .endEventDateTime(LocalDateTime.of(2021, 10, 3, 23,59,59))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("중앙대")
                .free(true)
                .offline(false)
                .build();


        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                ;
    }

    @Test
    void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                )
                .andExpect(status().isBadRequest())
                ;
    }

    @Test
    @DisplayName("잘못된 입력값 검증")
    void createEvent_Bad_Request_Wrong_Input() throws Exception {
        // 시작일보다 끝나는 일이 더 빠르고 가격도 이상한 경우 > validator 만들어야 함
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API - SPRING")
                .beginEnrollmentDateTime(LocalDateTime.of(2021, 9, 29, 23, 36))
                .closeEnrollmentDateTime(LocalDateTime.of(2021, 9, 26, 01, 30))
                .beginEventDateTime(LocalDateTime.of(2021, 10, 1, 0,0,0))
                .endEventDateTime(LocalDateTime.of(2021, 10, 3, 23,59,59))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("중앙대")
                .build();


        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                //.andExpect(jsonPath("$[0].field").exists()) // TODO: 글로벌 에러의 겨우 field가 없음 후조치하기
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
                //.andExpect(jsonPath("$[0].rejectedValue").exists())
        ;
    }
}
