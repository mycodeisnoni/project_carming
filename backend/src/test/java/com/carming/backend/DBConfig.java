package com.carming.backend;

import com.carming.backend.course.domain.Course;
import com.carming.backend.course.repository.CourseRepository;
import com.carming.backend.member.domain.Gender;
import com.carming.backend.member.domain.Member;
import com.carming.backend.member.repository.MemberRepository;
import com.carming.backend.place.domain.Place;
import com.carming.backend.place.domain.PlaceCategory;
import com.carming.backend.place.domain.PlaceTag;
import com.carming.backend.place.repository.PlaceRepository;
import com.carming.backend.place.repository.PlaceTagRepository;
import com.carming.backend.review.domain.Review;
import com.carming.backend.review.domain.ReviewTag;
import com.carming.backend.review.repository.ReviewRepository;
import com.carming.backend.review.repository.ReviewTagRepository;
import com.carming.backend.tag.domain.Category;
import com.carming.backend.tag.domain.Tag;
import com.carming.backend.tag.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
public class DBConfig {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ReviewTagRepository reviewTagRepository;

    @Autowired
    private PlaceTagRepository placeTagRepository;

    @Transactional
    public void init() {
        List<Tag> tags = saveReviewTag();
        savePlaceTag(tags);
    }


    private void savePlaceTag(List<Tag> tags) {
        Random random = new Random();

        List<Place> places = savePlace();
        for (Place place : places) {
            placeTagRepository.save(new PlaceTag(place, tags.get(random.nextInt(4) + 4)));
            placeTagRepository.save(new PlaceTag(place, tags.get(random.nextInt(4) + 4)));
            placeTagRepository.save(new PlaceTag(place, tags.get(random.nextInt(4) + 4)));
        }
    }

    private List<Tag> saveReviewTag() {
        Random random = new Random();

        List<Review> reviews = saveReview();
        List<Tag> tags = saveTags();
        for (Review review : reviews) {
            reviewTagRepository.save(new ReviewTag(review, tags.get(random.nextInt(4))));
            reviewTagRepository.save(new ReviewTag(review, tags.get(random.nextInt(4))));
        }

        return tags;
    }

    private List<Tag> saveTags() {
        List<Tag> tags = new ArrayList<>();
        tags.add(tagRepository.save(new Tag("데이트하기 좋은", Category.COURSE)));
        tags.add(tagRepository.save(new Tag("분위기 좋은", Category.COURSE)));
        tags.add(tagRepository.save(new Tag("드라이브 코스", Category.COURSE)));
        tags.add(tagRepository.save(new Tag("놀거리가 많은", Category.COURSE)));

        tags.add(tagRepository.save(new Tag("커피가 맛있는", Category.CAFE)));
        tags.add(tagRepository.save(new Tag("분위기 있는", Category.CAFE)));
        tags.add(tagRepository.save(new Tag("시그니처 메뉴", Category.CAFE)));
        tags.add(tagRepository.save(new Tag("조용한", Category.CAFE)));

        tags.add(tagRepository.save(new Tag("맛있는", Category.FOOD)));
        tags.add(tagRepository.save(new Tag("청결한", Category.FOOD)));
        tags.add(tagRepository.save(new Tag("양이 많은", Category.FOOD)));
        tags.add(tagRepository.save(new Tag("친절한", Category.FOOD)));

        return tags;
    }


    private List<Review> saveReview() {
        List<Course> courses = saveCourse();
        List<Member> members = saveMembers();
        List<Review> reviews = new ArrayList<>();
        mappingCourse(new Review(4, "꽤 재미있는 듯?"), courses.get(0), members.get(0), reviews);
        mappingCourse(new Review(3, "그닥"), courses.get(0), members.get(1), reviews);
        mappingCourse(new Review(2, "별로"), courses.get(0), members.get(2), reviews);
        mappingCourse(new Review(1, "별로인듯 가지마쇼"), courses.get(1), members.get(0), reviews);
        mappingCourse(new Review(2, "낫배드"), courses.get(1), members.get(4), reviews);
        mappingCourse(new Review(3, "평타치"), courses.get(1), members.get(5), reviews);
        mappingCourse(new Review(4, "괜찮은데?"), courses.get(1), members.get(6), reviews);
        reviewRepository.saveAll(reviews);
        return reviews;
    }

    private void mappingCourse(Review review, Course course, Member member, List<Review> reviews) {
        review.changeCourse(course);
        review.changeMember(member);
        reviews.add(review);
    }

    private List<Course> saveCourse() {
        Course course1 = courseRepository.save(new Course("1|2|3|4|5", "노원구|은평구|관악구", "노잼은 아닌 코스", 3, 9));
        Course course2 = courseRepository.save(new Course("1|4|5|10|7", "서대문구|은평구|관악구", "평타코스", 4, 10));
        return List.of(course1, course2);
    }

    private List<Place> savePlace() {
        Place place1 = placeRepository.save(new Place("어반플랜트 합정", "070-4192-0378", PlaceCategory.CAFE, 126.9171881, 37.54789417, "마포구", "서울 마포구 독막로4길 3", 275, 1155, "데이트코스|브런치카페|식물카페", "http://t1.daumcdn.net/localfiy/searchregister_1152174062"));
        Place place2 = placeRepository.save(new Place("피오니 홍대점", "02-333-5325", PlaceCategory.CAFE, 126.91976584, 37.55008538, "마포구", "서울 마포구 독막로7길 51", 214, 813, "일회용품없는가게", "http://t1.daumcdn.net/cfile/276E0A4C550A37652A"));
        Place place3 = placeRepository.save(new Place("프릳츠 도화점", "02-3275-2045", PlaceCategory.CAFE, 126.94907049, 37.54101958, "마포구", "서울 마포구 새창로2길 17", 288, 1123, "레트로풍|베이커리카페|빈티지분위기", "http://t1.kakaocdn.net/fiy_reboot/place/0E03C7048DF94AB187582A216C7CA500"));
        Place place4 = placeRepository.save(new Place("카페공명", "070-8869-6304", PlaceCategory.CAFE, 126.92635262, 37.5598709, "마포구", "서울 마포구 연희로 11", 127, 470, "공부하기좋은|베이커리카페|북카페|이색카페", "http://t1.daumcdn.net/localfiy/searchregister_1919726515"));
        return List.of(place1, place2, place3, place4);
    }

    private List<Member> saveMembers() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encoded = passwordEncoder.encode("1234");
        Member member1 = memberRepository.save(new Member("01051391314", encoded, "린중", "김인중", "example.com", Gender.MALE, LocalDate.of(1996, 02, 07)));
        Member member2 = memberRepository.save(new Member("01055555555", encoded, "훼린", "주해린", "example.com", Gender.FEMALE, LocalDate.of(1997, 11, 21)));
        Member member3 = memberRepository.save(new Member("01033333333", encoded, "정윤", "심정윤", "example.com", Gender.MALE, LocalDate.of(1997, 11, 21)));
        Member member4 = memberRepository.save(new Member("01044444444", encoded, "썽환", "조성환", "example.com", Gender.FEMALE, LocalDate.of(1997, 11, 21)));
        Member member5 = memberRepository.save(new Member("01066666666", encoded, "광", "이신광", "example.com", Gender.MALE, LocalDate.of(1997, 11, 21)));
        Member member6 = memberRepository.save(new Member("01077777777", encoded, "쎄솔", "오세솔", "example.com", Gender.FEMALE, LocalDate.of(1997, 11, 21)));
        Member member7 = memberRepository.save(new Member("01088888888", encoded, "준새로이", "선준용", "example.com", Gender.MALE, LocalDate.of(1997, 11, 21)));
        Member member8 = memberRepository.save(new Member("01099999999", encoded, "로그", "바알", "example.com", Gender.MALE, LocalDate.of(1997, 11, 21)));

        return List.of(member1, member2, member3, member4, member5, member6, member7, member8);
    }
}
