package com.carming.backend.place.service;

import com.carming.backend.DBConfig;
import com.carming.backend.place.domain.Place;
import com.carming.backend.place.domain.PlaceCategory;
import com.carming.backend.place.dto.request.PlaceSearch;
import com.carming.backend.place.dto.response.PlaceResponseDto;
import com.carming.backend.place.repository.PlaceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PlaceServiceTest {

    @Autowired
    DBConfig dbConfig;

    @Autowired
    PlaceRepository placeRepository;

    @Autowired
    PlaceService placeService;

    @ParameterizedTest
    @MethodSource("placeData")
    @DisplayName("Place 조건 검색 및 가져오기")
    @Transactional
    void getPlacesByPlaceSearch(List<String> regions, String category, Integer count) {
        //given
        List<String> savedRegions = List.of("은평구", "용산구", "노원구");
        savePlaces(savedRegions, count);

        int categoryCount = getCategoryCount(category);

        long sameCount = savedRegions.stream()
                .filter(region -> regions.contains(region))
                .count();

        //when
        List<PlaceResponseDto> placesWithRegion = placeService.getPlaces(new PlaceSearch(regions, category, null, 50, null));

        //then
        assertThat(placesWithRegion.size()).isEqualTo(Math.min(50, (categoryCount * sameCount) * count));
    }

    static Stream<Arguments> placeData() {
        return Stream.of(
                Arguments.of(List.of("은평구"), null, 10),
                Arguments.of(List.of("은평구", "용산구"), null, 10),
                Arguments.of(List.of("용산구"), "FOOD", 10),
                Arguments.of(List.of("은평구", "강남구", "송파구"), "CAFE", 10)
        );
    }

    private int getCategoryCount(String category) {
        if (StringUtils.hasText(category)) {
            return 1;
        }
        return Arrays.stream(PlaceCategory.values()).collect(Collectors.toList()).size();
    }

    private void savePlaces(List<String> regions, int count) {
        PlaceCategory[] categories = PlaceCategory.class.getEnumConstants();
        for (String region : regions) {
            for (PlaceCategory category : categories) {
                for (int i = 0; i < count; i++) {
                    placeRepository.save(createPlace(region, category));
                }
            }
        }
    }

    private Place createPlace(String region, PlaceCategory category) {
        return Place.builder()
                .region(region)
                .category(category)
                .build();
    }

}