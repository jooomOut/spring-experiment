package me.jooomout.demorestapistudy.events;

import me.jooomout.demorestapistudy.accounts.Account;
import me.jooomout.demorestapistudy.accounts.AccountRepository;
import me.jooomout.demorestapistudy.accounts.AccountRole;
import me.jooomout.demorestapistudy.accounts.AccountService;
import me.jooomout.demorestapistudy.common.BaseControllerTest;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


//@WebMvcTest // for slicing test

public class EventControllerTests extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;
    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    public void setUp(){
        this.eventRepository.deleteAll();
        this.accountRepository.deleteAll();
    }


    private String getBearerToken() throws Exception {
        String username = "user@email.com";
        String password = "jjjjj";
        Account tester = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        accountService.saveAccount(tester);

        String clientId = "myApp";
        String clientSecret = "pass";
        ResultActions perform = mockMvc.perform(post("/oauth/token") // ?????? URL ??? ????????? ??????
                        .with(httpBasic(clientId, clientSecret))
                        .param("username", username)
                        .param("password", password)
                        .param("grant_type", "password"));

        var responseBody = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();

        return parser.parseMap(responseBody).get("access_token").toString();
    }

    @Test
    @DisplayName("???????????? ????????? ??????")
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
                .location("?????????")
                .build();


        mockMvc.perform(post("/api/events/")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + getBearerToken())
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
                        //relaxedResponseFields( // relaxed ~ ??? ???????????? ??????????????? ??????. :: ?????? = ????????? ???????????? ??? ??? ??????!
                        relaxedResponseFields(
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
                                // TODO: link ????????? request?????? ???????????? ??????????????? response??? ????????? ???????????? ?????? ??????????????? ??????.
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
                .location("?????????")
                .free(true)
                .offline(false)
                .build();


        mockMvc.perform(post("/api/events/")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + getBearerToken())
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
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                ;
    }

    @Test
    @DisplayName("????????? ????????? ??????")
    void createEvent_Bad_Request_Wrong_Input() throws Exception {
        // ??????????????? ????????? ?????? ??? ????????? ????????? ????????? ?????? > validator ???????????? ???
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
                .location("?????????")
                .build();


        mockMvc.perform(post("/api/events")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                //.andExpect(jsonPath("$[0].field").exists()) // TODO: ????????? ????????? ?????? field??? ?????? ???????????????
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("_links.index").exists())
                //.andExpect(jsonPath("$[0].rejectedValue").exists())
        ;
    }

    @Test
    @DisplayName("30?????? ???????????? 10?????? ??? ?????? ????????? ???????????? - ?????? O")
    void queryEvents() throws Exception{
        // GIVEN - event 30???
        IntStream.range(0, 30).forEach(i -> {
            this.generateEvent(i);
        });

        // WHEN && THEN
        mockMvc.perform(get("/api/events")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + getBearerToken())
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "name,DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.create-event").exists())
                .andDo(document("query-events")) // TODO: ?????? ?????? ??????????????? ??? controller
                ;
    }

    @Test
    @DisplayName("30?????? ???????????? 10?????? ??? ?????? ????????? ???????????? - ?????? X")
    void queryEvents_No_Auth() throws Exception{
        // GIVEN - event 30???
        IntStream.range(0, 30).forEach(i -> {
            this.generateEvent(i);
        });

        // WHEN && THEN
        mockMvc.perform(get("/api/events")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "name,DESC")
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.create-event").doesNotExist())
                .andDo(document("query-events")) // TODO: ?????? ?????? ??????????????? ??? controller
        ;
    }

    @Test
    @DisplayName("?????? ???????????? ?????? ????????????")
    void getEvent() throws Exception{
        //GIVEN
        Event event = generateEvent(100);

        // WHEN & THEN
        mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event"))
                ;
    }

    @Test
    @DisplayName("?????? ???????????? ?????? ????????????")
    void getEvent404() throws Exception{
        //GIVEN
        Event event = generateEvent(100);

        // WHEN & THEN
        mockMvc.perform(get("/api/events/111111"))
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @DisplayName("?????? ???????????? ???????????? ?????????")
    void updateEvent() throws Exception {
        // GIVEN
        Event event = generateEvent(100);
        String eventName = "Updated Event";
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        eventDto.setName(eventName);


        mockMvc.perform(put("/api/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(eventDto))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(eventName))
                //.andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("free").value(false))
                //.andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))

                .andExpect(jsonPath("_links.self").exists())

                .andDo(document("update-event",
                        links(
                                linkWithRel("self").description("link to self"),
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
                                //headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type")
                        ),
                        //relaxedResponseFields( // relaxed ~ ??? ???????????? ??????????????? ??????. :: ?????? = ????????? ???????????? ??? ??? ??????!
                        relaxedResponseFields(
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
                                // TODO: link ????????? request?????? ???????????? ??????????????? response??? ????????? ???????????? ?????? ??????????????? ??????.
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                )) // create rest docs

                ;
    }

    @Test
    @DisplayName("?????? ???????????? ???????????? ????????? - ???????????? ?????? ??????")
    void updateEvent_404_Not_Found() throws Exception {
        // GIVEN
        Event event = generateEvent(100);
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API - SPRING")
                .beginEnrollmentDateTime(LocalDateTime.of(2021, 9, 29, 23, 36))
                .closeEnrollmentDateTime(LocalDateTime.of(2021, 9, 30, 01, 30))
                .beginEventDateTime(LocalDateTime.of(2021, 10, 1, 0,0,0))
                .endEventDateTime(LocalDateTime.of(2021, 10, 3, 23,59,59))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("?????????")
                .build();

        mockMvc.perform(put("/api/events/{id}", 99999)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @DisplayName("?????? ???????????? ???????????? ????????? - ???????????? ?????? ???")
    void updateEvent_Empty_Input() throws Exception {
        // GIVEN
        Event event = generateEvent(100);
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API - SPRING")
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("?????????")
                .build();

        mockMvc.perform(put("/api/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("?????? ???????????? ???????????? ????????? - ????????? ?????? ???")
    void updateEvent_Wrong_Input() throws Exception {
        // GIVEN
        Event event = generateEvent(100);
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(200);

        mockMvc.perform(put("/api/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    private Event generateEvent(int index){
        Event event = Event.builder()
                .name("event " + index)
                .description("test event")
                .beginEnrollmentDateTime(LocalDateTime.of(2021, 9, 29, 23, 36))
                .closeEnrollmentDateTime(LocalDateTime.of(2021, 9, 30, 01, 30))
                .beginEventDateTime(LocalDateTime.of(2021, 10, 1, 0,0,0))
                .endEventDateTime(LocalDateTime.of(2021, 10, 3, 23,59,59))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("?????????")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();;

        return this.eventRepository.save(event);
    }
}
