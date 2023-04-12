package com.carming.backend.member.service;

import com.carming.backend.member.dto.request.AuthNumbersDto;
import com.carming.backend.member.dto.request.PhoneNumberDto;
import com.carming.backend.member.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;

class AuthNumbersServiceTest {

    private final StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);

    private final ValueOperations valueOperations = mock(ValueOperations.class);

    private final MemberRepository memberRepository = mock(MemberRepository.class);

    private AuthNumbersService authNumbersService;

    @BeforeEach
    public void setUp() {
        BDDMockito.given(redisTemplate.opsForValue()).willReturn(valueOperations);
        authNumbersService = new AuthNumbersService(redisTemplate, memberRepository);
    }

    @Test
    @DisplayName("인증번호 저장")
    void saveAuthNumbers() {
        //given
        String phoneNumber = "01051391314";
        PhoneNumberDto request = createPhoneNumber(phoneNumber);
        BDDMockito.willDoNothing().given(valueOperations).set(eq(phoneNumber), any(), eq(Duration.ofMinutes(3L)));

        //when
        String authNumbers = authNumbersService.saveAuthNumbers(request);

        //then
        BDDMockito.then(valueOperations).should(Mockito.times(1)).set(eq(phoneNumber), any(),eq(Duration.ofMinutes(3L)));
        assertThat(authNumbers.length()).isEqualTo(6);
    }

    @Test
    @DisplayName("인증번호 검증 - 성공")
    void validAuthNumbers() throws JsonProcessingException {
//        //given
//        String phoneNumber = "01051391314";
//        String requestAuth = "123456";
//        AuthNumbersDto request = createAuthNumberDto(phoneNumber, requestAuth);
//        String authInfo = objectMapper.writeValueAsString(new AuthenticationInfo(requestAuth, false));
//
//        BDDMockito.given(valueOperations.get(phoneNumber)).willReturn(requestAuth);
//        BDDMockito.willDoNothing().given(valueOperations).set(eq(phoneNumber), any(), eq(Duration.ofMinutes(60L)));
//
//        //when
//        String authNumbers = authNumbersService.validAuthNumbers(request);
//
//        //then
//        BDDMockito.then(valueOperations).should(times(1)).set(eq(phoneNumber), any(),eq(Duration.ofMinutes(60L)));
//        assertThat(authNumbers).isEqualTo(requestAuth);
    }

    @Test
    @DisplayName("인증번호 검증 - 실패")
    void failValidAuthNumbers() throws JsonProcessingException {
//        //given
//        String phoneNumber = "01051391314";
//        String requestAuth = "123456";
//        final String WRONG_NUMBER = "654321";
//        AuthNumbersDto request = createAuthNumberDto(phoneNumber, requestAuth);
//        String authInfo = objectMapper.writeValueAsString(new AuthenticationInfo(requestAuth, false));
//
//        BDDMockito.given(valueOperations.get(phoneNumber)).willReturn(WRONG_NUMBER);
//        BDDMockito.willDoNothing().given(valueOperations).set(eq(phoneNumber), any(), eq(Duration.ofMinutes(60L)));
//
//        //expected
//        Assertions.assertThatThrownBy(() -> authNumbersService.validAuthNumbers(request))
//                .isInstanceOf(InvalidAuthRequest.class);
//        BDDMockito.then(valueOperations).should(times(0)).set(eq(phoneNumber), any(), eq(Duration.ofMinutes(60L)));
    }

    private PhoneNumberDto createPhoneNumber(String phoneNumber) {
        PhoneNumberDto phoneNumberDto = new PhoneNumberDto();
        phoneNumberDto.setPhone(phoneNumber);
        return phoneNumberDto;
    }

    private AuthNumbersDto createAuthNumberDto(String phoneNumber, String authNumber) {
        AuthNumbersDto authNumbersDto = new AuthNumbersDto();
        authNumbersDto.setPhone(phoneNumber);
        authNumbersDto.setAuthNumber(authNumber);
        return authNumbersDto;
    }
}