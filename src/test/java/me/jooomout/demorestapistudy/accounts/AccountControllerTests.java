package me.jooomout.demorestapistudy.accounts;

import me.jooomout.demorestapistudy.common.BaseControllerTest;
import me.jooomout.demorestapistudy.events.Event;
import me.jooomout.demorestapistudy.events.EventDto;
import me.jooomout.demorestapistudy.events.EventRepository;
import me.jooomout.demorestapistudy.events.EventStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

public class AccountControllerTests extends BaseControllerTest {
    @Autowired
    AccountController accountController;

    @Test
    @DisplayName("정상 로그인 시도")
    void login() throws Exception {
        AccountDto accountDto = AccountDto.builder()
                .email("user@email.com")
                .password("jjjjj")
                .build();

        mockMvc.perform(post("/api/account/login")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(accountDto))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(cookie().exists("id"))
        ;
    }
    @Test
    @DisplayName("ID 잘못된 로그인 시도")
    void login_wrongId() throws Exception {
        AccountDto accountDto = AccountDto.builder()
                .email("no@il.com")
                .password("jjjjj")
                .build();
        mockMvc.perform(post("/api/account/login")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(accountDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }
    @Test
    @DisplayName("PW 잘못된 로그인 시도")
    void login_wrongPw() throws Exception {
        AccountDto accountDto = AccountDto.builder()
                .email("user@email.com")
                .password("123")
                .build();

        mockMvc.perform(post("/api/account/login")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(accountDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }
}
