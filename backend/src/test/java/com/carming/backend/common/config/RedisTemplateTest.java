package com.carming.backend.common.config;

import com.carming.backend.common.JsonMapper;
import com.carming.backend.member.domain.valid.AuthNumberFactory;
import com.carming.backend.member.domain.valid.AuthenticationInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.*;

@DataRedisTest
public class RedisTemplateTest {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    @DisplayName("인증번호 저장 및 조회 테스트")
    void saveAndFind() {
        //given
        final String phoneNumber = "01012345678";
        final String authNumbers = AuthNumberFactory.createValidNumbers().getAuthNumbers();
        AuthenticationInfo authInfo = new AuthenticationInfo(authNumbers, true);
        String result = JsonMapper.toJson(authInfo);

        //when
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.set(phoneNumber, result);

        //then
        String value = operations.get(phoneNumber);
        assertThat(value).isEqualTo(result);
        AuthenticationInfo redisResult = JsonMapper.toClass(value, AuthenticationInfo.class);
        assertThat(redisResult.getAuthNumbers()).isEqualTo(authNumbers);
        assertThat(redisResult.getAuthenticated()).isTrue();
    }

    @Test
    @DisplayName("레디스 템플릿 시간 경과시 삭제")
    void saveTimeToLive() throws InterruptedException {
        //given
        final String phoneNumber = "01012345678";
        final String validNumber = AuthNumberFactory.createValidNumbers().getAuthNumbers();

        //when
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.set(phoneNumber, validNumber, Duration.ofSeconds(3));
        Thread.sleep(5000);

        //then
        assertThat(operations.get(phoneNumber)).isNull();
    }
}
