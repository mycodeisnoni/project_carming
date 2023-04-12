package com.carming.backend.member.dto.request;

import com.carming.backend.login.authentication.PasswordEncoder;
import com.carming.backend.member.domain.Card;
import com.carming.backend.member.domain.CardCompany;
import com.carming.backend.member.domain.Gender;
import com.carming.backend.member.domain.Member;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
@Data
public class MemberCreateDto {

    @Pattern(regexp = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$",
            message = "휴대폰번호가 올바르지 않습니다. 다시 입력해주세요.")
    private String phone;

    private String password;

    private String passwordConfirm;

    private String nickname;

    private String name;

    private Gender gender;

    private String birthDate; // 1993/02/06

    private CardDto card;

    @Builder
    public MemberCreateDto(String phone, String password, String passwordConfirm,
                           String nickname, String name, Gender gender,
                           String birthDate, CardDto cardDto) {
        this.phone = phone;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.nickname = nickname;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.card = cardDto;
    }

    public Member toEntity() {
        return Member.builder()
                .phone(phone)
                .password(PasswordEncoder.encode(password))
                .nickname(nickname)
                .name(name)
                //Todo change image
                .profile("example.com")
                .gender(gender)
                .birthday(LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("yyyy/MM/dd")))
                .build();
    }

    @NoArgsConstructor
    @Data
    public static class CardDto {

        private String cardNumber;

        private String cvv;

        private String cardExp;

        private String cardPassword;

        private CardCompany companyName;

        @Builder
        public CardDto(String cardNumber, String cvv, String cardExp, String cardPassword, CardCompany companyName) {
            this.cardNumber = cardNumber;
            this.cvv = cvv;
            this.cardExp = cardExp;
            this.cardPassword = cardPassword;
            this.companyName = companyName;
        }

        public Card toEntity() {
            return Card.builder()
                    .number(cardNumber)
                    .cvv(cvv)
                    .expiredDate(cardExp)
                    .password(cardPassword)
                    .companyName(companyName)
                    .build();
        }
    }
}
