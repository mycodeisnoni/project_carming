package com.carming.backend.member.dto.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class AuthNumbersDtoTest {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @ParameterizedTest
    @ValueSource(strings = {"123456789", "1546138764", "11153157894", "010-4624-4564", "01448641231"})
    @DisplayName("올바르지 않은 핸드폰 번호 유형")
    void incorrectPhoneNumber(String phoneNumber) {
        //given
        AuthNumbersDto authNumbersDto = new AuthNumbersDto();
        authNumbersDto.setPhone(phoneNumber);

        //when
        Set<ConstraintViolation<AuthNumbersDto>> validate = validator.validate(authNumbersDto);

        //then
        assertThat(CollectionUtils.isEmpty(validate)).isFalse();
    }
}