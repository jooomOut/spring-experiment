package me.jooomout.demorestapistudy.configs;

import me.jooomout.demorestapistudy.accounts.Account;
import me.jooomout.demorestapistudy.accounts.AccountRole;
import me.jooomout.demorestapistudy.accounts.AccountService;
import me.jooomout.demorestapistudy.common.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    /*
    * Password + refreshToken 2 가지 Grant Type을 사용할 것임
    * Password -> Hop이 단 한번, 유저 정보를 직접 가지고 있는 경우에만 사용해야 함, 써드파티에서는 절대 불가.
    *
    *  */
    @Test
    @DisplayName("인증 토큰을 발급 받는 테스트")
    void getAuthToken() throws Exception {
        // Given
        String username = "authToken@email.com";
        String password = "jjjjj";
        Account tester = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        accountService.saveAccount(tester);

        String clientId = "myApp";
        String clientSecret = "pass";
        mockMvc.perform(post("/oauth/token") // 해당 URL 은 알아서 생김
                        .with(httpBasic(clientId, clientSecret))
                        .param("username", username)
                        .param("password", password)
                        .param("grant_type", "password")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists())
                ;
        System.out.println(tester.getPassword());

    }

}