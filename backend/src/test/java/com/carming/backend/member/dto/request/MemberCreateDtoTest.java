package com.carming.backend.member.dto.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MemberCreateDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }



    @ParameterizedTest
    @ValueSource(strings = {"01051391314", "01165451321", "0161443434", "01020379709", "01099117687", "01046458285"})
    @DisplayName("올바른 핸드폰 번호")
    void correctPhoneNumber(String phoneNumber) {
        //given
        MemberCreateDto memberCreateDto = MemberCreateDto.builder()
                .phone(phoneNumber)
                .build();
        //when
        Set<ConstraintViolation<MemberCreateDto>> resultSet = validator.validate(memberCreateDto);

        //then
        assertThat(resultSet).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"010-5139-1314", "123456789", "00014231049"})
    @DisplayName("올바르지 않은 핸드폰 번호")
    void notValidPhoneNumber(String phoneNumber) {
        //given
        MemberCreateDto memberCreateDto = MemberCreateDto.builder()
                .phone(phoneNumber)
                .build();

        //when
        Set<ConstraintViolation<MemberCreateDto>> resultSet = validator.validate(memberCreateDto);

        //then
        assertThat(resultSet.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("String to LocalDate")
    void StringToLocalDate() {
        //given
        String birthDate = "1993/02/06";

        //when
        LocalDate localDate = LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        //then
        assertThat(localDate).isEqualTo(LocalDate.of(1993, 02, 06));
    }

    @ParameterizedTest
    @MethodSource("invalidBirthInfo")
    @DisplayName("생년월이 정규식")
    void invalidBirthInfo(String birthYear, String birthMonth, String birthDay, int size) {
        //given
//        MemberCreateDto.BirthInfoDto birthInfoDto = new MemberCreateDto.BirthInfoDto(birthYear, birthMonth, birthDay);

        //when
//        Set<ConstraintViolation<MemberCreateDto.BirthInfoDto>> resultSet = validator.validate(birthInfoDto);

        //then
//        for (ConstraintViolation<MemberCreateDto.BirthInfoDto> birthInfoDtoConstraintViolation : resultSet) {
//            System.out.println(birthInfoDtoConstraintViolation.getMessage());
//        }
//        assertThat(resultSet.size()).isEqualTo(size);
    }

    static Stream<Arguments> invalidBirthInfo() {
        return Stream.of(
                Arguments.of("1993", "12", "07", 0),
                Arguments.of("2103", "11", "08", 1),
                Arguments.of("1997", "14", "11", 1),
                Arguments.of("2003", "07", "32", 1),
                Arguments.of("2222", "35", "01", 2),
                Arguments.of("5000", "11", "71", 2),
                Arguments.of("2011", "57", "51", 2),
                Arguments.of("9582", "17", "88", 3)
        );
    }
}