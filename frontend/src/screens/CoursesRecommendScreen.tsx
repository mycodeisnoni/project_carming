import styled from 'styled-components/native';
import TagChip from '../components/TagChip';
import {
  ActivityIndicator,
  Avatar,
  IconButton,
  Tooltip,
  useTheme,
} from 'react-native-paper';
import {useEffect, useRef, useState} from 'react';
import Carousel from 'react-native-snap-carousel-v4';
import CourseRecommendCard from '../components/CourseRecommendCard';
import {Dimensions, ScrollView, StyleSheet, Text, View} from 'react-native';
import {Category, Course, Tag} from '../types';
import {useDispatch, useSelector} from 'react-redux';
import {RootState} from '../redux/store';
import {
  addCheckedTag,
  deleteCheckedTag,
  deletePlaceFromCourseCartById,
  setCourseTagList,
  setCourseToCourseCart,
} from '../redux/slices/courseSlice';
import {useGetCoursesQuery} from '../apis/courseApi';

interface CoursesRecommendScreenProps {}

const CoursesRecommendScreen: React.FC<CoursesRecommendScreenProps> = ({}) => {
  const theme = useTheme();
  const dispatch = useDispatch();
  const carouselRef = useRef<any>(null);

  const {courseCart, courseTagList, checkedTagList} = useSelector(
    (state: RootState) => state.course,
  );
  const tags = useSelector((state: RootState) => state.tag);

  const {
    data: courses,
    error,
    isFetching,
    isError,
    isSuccess,
  } = useGetCoursesQuery({regions: ['은평구'], size: 10});
  const [carouselData, setCarouselData] = useState<any[]>([]);

  useEffect(() => {
    dispatch(setCourseTagList(tags.courseTags));
  }, [tags]);

  useEffect(() => {
    const activeIdx = carouselRef.current ? carouselRef.current._activeItem : 0;
    makeCarouselData(activeIdx);
  }, [courses]);

  const tagPressed = (tag: Tag) => {
    checkedTagList.includes(tag)
      ? dispatch(deleteCheckedTag(tag))
      : dispatch(addCheckedTag(tag));
  };

  const courseAddBtnPressed = () => {
    if (courses) {
      const course: Course = courses[carouselRef.current._activeItem];
      dispatch(setCourseToCourseCart(course));
    }
  };

  const canclePlaceBtnPressed = (placeId: number) => {
    dispatch(deletePlaceFromCourseCartById(placeId));
  };

  const makeCarouselData = (index: number) => {
    if (courses) {
      const tmpList: any[] = courses.map((course, idx) => ({
        course: course,
        isActive: idx === index,
      }));
      setCarouselData(tmpList);
    }
  };

  const carouselSection = () => {
    if (isFetching) {
      return (
        <ActivityIndicator
          size={'large'}
          animating={true}
          color={theme.colors.onPrimary}
        />
      );
    }
    if (isError) {
      return <Text style={{fontSize: 40}}>😭</Text>;
    }
    if (isSuccess) {
      return (
        <Carousel
          style={{flex: 1}}
          layout={'default'}
          vertical={false}
          layoutCardOffset={9}
          ref={carouselRef}
          data={carouselData}
          renderItem={CourseRecommendCard}
          sliderWidth={screenWidth}
          itemWidth={screenWidth - 80}
          inactiveSlideShift={0}
          useScrollView={true}
          onScrollIndexChanged={index => {
            makeCarouselData(index);
          }}
        />
      );
    }
  };

  return (
    <>
      <StyledView style={{marginTop: 10, marginBottom: 20}}>
        <ScrollView
          style={styles.tagScrollViewStyle}
          horizontal={true}
          showsHorizontalScrollIndicator={false}>
          {courseTagList.map(tag => {
            return (
              <TagChip
                key={tag.id}
                style={{marginLeft: 5}}
                text={tag.name}
                selected={checkedTagList.includes(tag)}
                selectedBackgroundColor={theme.colors.secondary}
                onPress={() => tagPressed(tag)}
              />
            );
          })}
        </ScrollView>
      </StyledView>

      <CenterView>{carouselSection()}</CenterView>

      <StyledView style={{justifyContent: 'center'}}>
        <IconButton
          icon="arrow-down-drop-circle"
          iconColor={theme.colors.background}
          size={35}
          style={{marginVertical: 20}}
          onPress={() => courseAddBtnPressed()}
        />
      </StyledView>

      <StyledView style={{height: 70}}>
        {courseCart.map(place => {
          return (
            <Tooltip key={place.id} title={place.name} enterTouchDelay={1}>
              <View style={{marginRight: 5}}>
                <Avatar.Image size={50} source={{uri: place.image}} />
                <IconButton
                  style={{position: 'absolute', right: -17, top: -17}}
                  icon="close-circle"
                  iconColor={theme.colors.background}
                  size={15}
                  onPress={() => canclePlaceBtnPressed(place.id)}
                />
              </View>
            </Tooltip>
          );
        })}
      </StyledView>
    </>
  );
};

const {width: screenWidth} = Dimensions.get('window');

const StyledView = styled(View)`
  align-items: center;
  flex-direction: row;
  padding-left: 20px;
  padding-right: 20px;
`;

const CenterView = styled(View)`
  align-items: center;
  justify-content: center;
  flex: 1;
`;

const styles = StyleSheet.create({
  tagScrollViewStyle: {
    flexDirection: 'row',
  },
});

export default CoursesRecommendScreen;
