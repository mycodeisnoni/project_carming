package com.carming.backend.member.repository;

import com.carming.backend.member.domain.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.carming.backend.member.domain.QMember.member;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Member> findByPhone(String phoneNumber) {
        return Optional.ofNullable(queryFactory
                .selectFrom(member)
                .where(member.phoneNumber.eq(phoneNumber))
                .fetchFirst());
    }
}
