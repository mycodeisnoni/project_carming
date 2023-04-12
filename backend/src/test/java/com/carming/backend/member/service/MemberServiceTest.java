package com.carming.backend.member.service;

import com.carming.backend.common.JsonMapper;
import com.carming.backend.exception.InvalidRequest;
import com.carming.backend.member.domain.CardCompany;
import com.carming.backend.member.domain.Member;
import com.carming.backend.member.domain.valid.AuthenticationInfo;
import com.carming.backend.member.dto.request.MemberCreateDto;
import com.carming.backend.member.exception.NotAuthentication;
import com.carming.backend.member.repository.CardRepository;
import com.carming.backend.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

class MemberServiceTest {

    private final MemberRepository memberRepository = Mockito.mock(MemberRepository.class);

    private final CardRepository cardRepository = Mockito.mock(CardRepository.class);

    private final StringRedisTemplate redisTemplate = Mockito.mock(StringRedisTemplate.class);

    private final ValueOperations operations = Mockito.mock(ValueOperations.class);

    private MemberService memberService;

    @BeforeEach
    void setUp() {
        BDDMockito.given(redisTemplate.opsForValue()).willReturn(operations);
        memberService = new MemberService(memberRepository, cardRepository, redisTemplate);
    }

    @Test
    @DisplayName("회원가입 전 검증확인")
    void checkValidBeforeSignUp() {
        //given
        final String PHONE_NUMBER = "01051391314";
        MemberCreateDto request = createMemberDto(PHONE_NUMBER);
        String authInfo = JsonMapper.toJson(new AuthenticationInfo(PHONE_NUMBER, true));
        BDDMockito.given(operations.get(eq(PHONE_NUMBER))).willReturn(authInfo);

        //when
        memberService.validAuthenticated(request);

        //then
        BDDMockito.then(operations).should(Mockito.times(1)).get(eq(PHONE_NUMBER));
    }

    @Test
    @DisplayName("3분이 지나서 인증번호가 지워졌을 때 - InvalidRequest")
    void deletedAuthNumber() {
        //given
        final String PHONE_NUMBER = "01051391314";
        MemberCreateDto request = createMemberDto(PHONE_NUMBER);
        BDDMockito.given(operations.get(PHONE_NUMBER)).willReturn(null);

        //expected
        assertThatThrownBy(() -> memberService.validAuthenticated(request))
                .isInstanceOf(InvalidRequest.class);
        BDDMockito.then(operations).should(Mockito.times(1)).get(PHONE_NUMBER);
    }

    @Test
    @DisplayName("검증정보가 아직 false 일때 - NotAuthentication")
    void notAuthenticated() {
        //given
        final String PHONE_NUMBER = "01051391314";
        MemberCreateDto request = createMemberDto(PHONE_NUMBER);
        AuthenticationInfo authInfo = new AuthenticationInfo("123456", false);
        String json = JsonMapper.toJson(authInfo);
        BDDMockito.given(operations.get(PHONE_NUMBER)).willReturn(json);

        //expected
        assertThatThrownBy(() -> memberService.validAuthenticated(request))
                .isInstanceOf(NotAuthentication.class);
        BDDMockito.then(operations).should(Mockito.times(1)).get(PHONE_NUMBER);
    }

    @Test
    @DisplayName("회원가입")
    void signUp() {
        //given
        final String PHONE_NUMBER = "01051391314";
        MemberCreateDto request = createMemberDto(PHONE_NUMBER);
        Member entity = request.toEntity();
        BDDMockito.given(memberRepository.save(any(Member.class))).willReturn(createId(entity, 1L));

        //when
        Long savedMemberId = memberService.saveMember(request);

        //then
        assertThat(savedMemberId).isEqualTo(1L);
        BDDMockito.then(memberRepository).should(Mockito.times(1)).save(any(Member.class));
    }

    private Member createId(Member member, Long generatedId) {
        ReflectionTestUtils.setField(member, "id", generatedId);
        return member;
    }
    private MemberCreateDto createMemberDto(String phoneNumber) {
        return MemberCreateDto.builder()
                .phone(phoneNumber)
                .password("1234")
                .passwordConfirm("1234")
                .nickname("광")
                .name("이신광")
                .birthDate("1993/02/06")
                .cardDto(new MemberCreateDto.CardDto("1", "2", "3", "4", CardCompany.BC))
                .build();
    }
}