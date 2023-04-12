package com.carming.backend.common;

import com.carming.backend.member.domain.Member;
import com.carming.backend.member.domain.valid.AuthNumberFactory;
import com.carming.backend.member.domain.valid.AuthenticationInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JsonMapperTest {

    @Test
    @DisplayName("JSON 변환")
    void convertToJson() {
        //given
        String authNumbers = AuthNumberFactory.createValidNumbers().getAuthNumbers();
        AuthenticationInfo beforeJson = new AuthenticationInfo(authNumbers, true);
        String authInfo = JsonMapper.toJson(beforeJson);
        AuthenticationInfo afterJson = JsonMapper.toClass(authInfo, AuthenticationInfo.class);

        //expected
        assertThat(beforeJson.getAuthenticated()).isEqualTo(afterJson.getAuthenticated());
        assertThat(beforeJson.getAuthNumbers()).isEqualTo(afterJson.getAuthNumbers());
    }
    @Test
    @DisplayName("JSON 파싱 실패시 FailConvertJson 예외를 던진다")
    void failConvertToJson() {
        //given
        String notJson = "{nono}";

        //expected
        assertThatThrownBy(() -> JsonMapper.toClass(notJson, Member.class))
                .isInstanceOf(FailConvertJson.class);
    }
}