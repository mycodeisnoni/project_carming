package com.carming.backend.member.controller;

import com.carming.backend.common.JsonMapper;
import com.carming.backend.exception.InvalidRequest;
import com.carming.backend.exception.controller.ExceptionHandler;
import com.carming.backend.member.domain.Gender;
import com.carming.backend.member.dto.request.MemberCreateDto;
import com.carming.backend.member.exception.NotAuthentication;
import com.carming.backend.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(value = {MemberController.class, ExceptionHandler.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import(ExceptionHandler.class)
class MemberControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    MemberService memberService;

    @Test
    @DisplayName("회원가입 성공")
    void signupSuccess() throws Exception {
        //given
        final String PHONE = "01051391314";
        final String PASSWORD = "123456";
        final String NAME = "테스트봇";
        MemberCreateDto request = createTestRequest(PHONE, PASSWORD, NAME);
        String json = JsonMapper.toJson(request);

        BDDMockito.doNothing().when(memberService).validAuthenticated(request);
        BDDMockito.given(memberService.saveMember(request)).willReturn(1L);

        //expected
        mockMvc.perform(MockMvcRequestBuilders.post("/api/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""))
                .andDo(MockMvcResultHandlers.print());

        BDDMockito.then(memberService).should(Mockito.times(1)).validAuthenticated(request);
        BDDMockito.then(memberService).should(Mockito.times(1)).saveMember(request);
    }

    @Test
    @DisplayName("회원가입 - 인증번호 유효시간 지나 errorResponse")
    void fail_signUp() throws Exception {
        //given
        final String PHONE = "01051391314";
        final String PASSWORD = "123456";
        final String NAME = "테스트봇";
        MemberCreateDto request = createTestRequest(PHONE, PASSWORD, NAME);
        String json = JsonMapper.toJson(request);
        InvalidRequest exception = new InvalidRequest();

        BDDMockito.doThrow(exception).when(memberService).validAuthenticated(request);
        BDDMockito.given(memberService.saveMember(request)).willReturn(1L);

        //excpected
        mockMvc.perform(MockMvcRequestBuilders.post("/api/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(exception.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("회원 가입 - 인증 실패시 errorResponse")
    void notAuthentication() throws Exception {
        //given
        final String PHONE = "01051391314";
        final String PASSWORD = "123456";
        final String NAME = "테스트봇";
        MemberCreateDto request = createTestRequest(PHONE, PASSWORD, NAME);
        String json = JsonMapper.toJson(request);
        NotAuthentication exception = new NotAuthentication();

        BDDMockito.doThrow(exception).when(memberService).validAuthenticated(request);
        BDDMockito.given(memberService.saveMember(request)).willReturn(1L);

        //expected
        mockMvc.perform(MockMvcRequestBuilders.post("/api/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("403"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(exception.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }


    private MemberCreateDto createTestRequest(String phone, String password, String name) {
        return MemberCreateDto.builder()
                .phone(phone)
                .password(password)
                .passwordConfirm(password)
                .name(name)
                .nickname(name)
                .birthDate("1993/02/06")
                .gender(Gender.MALE)
                .cardDto(null)
                .build();
    }
}