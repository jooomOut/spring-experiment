package me.jooomout.demorestapistudy.session;

import me.jooomout.demorestapistudy.common.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.MediaTypes;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SessionInfoControllerTest extends BaseControllerTest {

    @Test
    @DisplayName("세션 출력")
    void sessionInfo() throws Exception {
        mockMvc.perform(get("/session-info")
                        .accept(MediaTypes.HAL_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

}