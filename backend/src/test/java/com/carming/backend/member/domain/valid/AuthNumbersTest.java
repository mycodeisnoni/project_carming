package com.carming.backend.member.domain.valid;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AuthNumbersTest {

    final int RANGE = 10;
    final int COUNT = 6;

    @Test
    @DisplayName("인증번호의 사이즈는 6이며 숫자로 구성되어 있다.")
    void getAuthNumber() {
        //given
        String authNumbers = AuthNumberFactory.createValidNumbers().getAuthNumbers();

        //expected
        assertThat(authNumbers.length()).isEqualTo(6);
        char[] numbers = authNumbers.toCharArray();
        for (char number : numbers) {
            assertThat(Character.isDigit(number)).isTrue();
        }
    }
}