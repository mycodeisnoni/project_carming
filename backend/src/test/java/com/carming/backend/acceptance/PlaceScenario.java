package com.carming.backend.acceptance;

import com.carming.backend.DBConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PlaceScenario {

    @Autowired
    DBConfig dbConfig;

    @Test
    @DisplayName("장소 검색 테스트")
    void placeApi() {
    }
    
}
