package zerobase.dividends.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import zerobase.dividends.model.Auth;
import zerobase.dividends.persist.entity.MemberEntity;
import zerobase.dividends.security.TokenProvider;
import zerobase.dividends.service.MemberService;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @MockBean
    private MemberService memberService;

    @MockBean
    private TokenProvider tokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원 가입 성공")
    void signupSuccess() throws Exception {
        //given
        given(memberService.register(any()))
                .willReturn(MemberEntity.builder()
                        .username("a")
                        .password("b")
                        .roles(Arrays.asList("ROLE_READ"))
                        .build());
        Auth.SignUp signUp = new Auth.SignUp();
        signUp.setUsername("a");
        signUp.setPassword("b");
        signUp.setRoles(Arrays.asList("ROLE_READ"));
        //when
        //then
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                signUp
                        )))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.username").value("a"))
                .andExpect(jsonPath("$.roles").value("ROLE_READ"))
                .andDo(print());
    }
}