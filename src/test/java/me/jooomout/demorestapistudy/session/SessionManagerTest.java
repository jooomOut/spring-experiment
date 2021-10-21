package me.jooomout.demorestapistudy.session;

import me.jooomout.demorestapistudy.accounts.Account;
import me.jooomout.demorestapistudy.accounts.AccountDto;
import me.jooomout.demorestapistudy.common.BaseControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SessionManagerTest extends BaseControllerTest {

    @Autowired
    SessionManager sessionManager;
    static AccountDto accountDto = AccountDto.builder()
            .email("user@email.com")
            .password("jjjjj")
            .build();

    @Test
    void createSession(){
        // 생성
        MockHttpServletResponse response = new MockHttpServletResponse();
        sessionManager.createSession(accountDto, response);

        // 요청에 쿠키 저장
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(response.getCookies());

        // 조회
        Object result = sessionManager.getSession(request);

        assertThat(result).isEqualTo(accountDto);
    }

}