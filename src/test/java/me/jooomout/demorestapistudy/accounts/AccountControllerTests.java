package me.jooomout.demorestapistudy.accounts;

import me.jooomout.demorestapistudy.common.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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
