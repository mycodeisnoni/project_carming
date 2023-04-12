package com.carming.backend.login.service;

import com.carming.backend.login.authentication.JwtConst;
import com.carming.backend.login.authentication.JwtProvider;
import com.carming.backend.login.dto.request.LoginRequestDto;
import com.carming.backend.login.dto.response.LoginResponseDto;
import com.carming.backend.member.domain.Member;
import com.carming.backend.member.exception.MemberNotFound;
import com.carming.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LoginService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final MemberRepository memberRepository;

    public LoginResponseDto login(LoginRequestDto request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getPhone(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        Member foundMember = memberRepository.findByPhone(request.getPhone())
                .orElseThrow(MemberNotFound::new);

        String accessToken = JwtProvider.generateToken(authentication);
        return new LoginResponseDto(JwtConst.TOKEN_TYPE, accessToken, foundMember.getNickname(), foundMember.getProfile());
    }
}
