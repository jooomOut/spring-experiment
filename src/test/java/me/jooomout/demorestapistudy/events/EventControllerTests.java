package me.jooomout.demorestapistudy.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.jooomout.demorestapistudy.common.TestConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
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


//@WebMvcTest // for slicing test
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "target/snippets")
@Import(TestConfiguration.class)
@ActiveProfiles("test")
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
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))

                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())

                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update event"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")

                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment of new Event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment of new Event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new Event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new Event"),
                                fieldWithPath("location").description("location of new Event"),
                                fieldWithPath("basePrice").description("basePrice of new Event"),
                                fieldWithPath("maxPrice").description("maxPrice of new Event"),
                                fieldWithPath("limitOfEnrollment").description("limit of new Event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type")
                        ),
                        //relaxedResponseFields( // relaxed ~ 는 일부분만 만들겠다는 표시. :: 단점 = 정확한 테스트를 할 수 없음!
                        responseFields(
                                fieldWithPath("id").description("Id of new event"),
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin enrollment of new Event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close enrollment of new Event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new Event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new Event"),
                                fieldWithPath("location").description("location of new Event"),
                                fieldWithPath("basePrice").description("basePrice of new Event"),
                                fieldWithPath("maxPrice").description("maxPrice of new Event"),
                                fieldWithPath("limitOfEnrollment").description("limit of new Event"),
                                fieldWithPath("free").description("it tells is this event is free or not"),
                                fieldWithPath("offline").description("it tells is this event is offline or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                // TODO: link 정보는 request에서 테스트가 진행됐는데 response에 당연히 들어가는 거라 테스트에서 걸림.
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query event list"),
                                fieldWithPath("_links.update-event.href").description("link to update existing event"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                )) // create rest docs
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
