import {useRef} from 'react';
import {Dimensions, StyleSheet, Text, View} from 'react-native';
import {useSelector, useDispatch} from 'react-redux';
import {
  ActivityIndicator,
  Avatar,
  IconButton,
  Tooltip,
  useTheme,
} from 'react-native-paper';
import Carousel from 'react-native-snap-carousel-v4';
import Icon from 'react-native-vector-icons/MaterialCommunityIcons';
import SelectDropdown from 'react-native-select-dropdown';
import styled from 'styled-components/native';
import {Place, Category, Tag} from '../types';
import PlaceRecommendCard from '../components/PlaceRecommendCard';
import TagChip from '../components/TagChip';
import {RootState} from '../redux/store';
import {
  addCheckedTag,
  addPlaceToPlaceCart,
  deleteCheckedTag,
  deletePlaceFromPlaceCartById,
  selectCategory,
} from '../redux/slices/placeSlice';
import {useGetPlacesQuery} from '../apis/placeApi';

interface PlacesRecommendScreenScreenProps {}

const PlacesRecommendScreen: React.FC<
  PlacesRecommendScreenScreenProps
> = ({}) => {
  const theme = useTheme();
  const dispatch = useDispatch();
  const carouselRef = useRef<any>(null);

  const {placeCart, placeTagList, checkedTagList, selectedCategory} =
    useSelector((state: RootState) => state.place);
  const {
    data: places,
    error,
    isLoading,
    isError,
    isSuccess,
  } = useGetPlacesQuery({
    regions: ['마포구'],
    category: selectedCategory,
    size: 10,
  });

  const tagPressed = (tag: Tag) => {
    checkedTagList.includes(tag)
      ? dispatch(deleteCheckedTag(tag))
      : dispatch(addCheckedTag(tag));
  };

  const placeAddBtnPressed = () => {
    if (places) {
      const place: Place = places[carouselRef.current._activeItem];
      dispatch(addPlaceToPlaceCart(place));
    }
  };

  const placeCancelBtnPressed = (placeId: number) => {
    dispatch(deletePlaceFromPlaceCartById(placeId));
  };

  const carouselSection = () => {
    console.log('get places data ::', places);
    console.log('get places error ::', error);
    if (isLoading)
      return (
        <CenterView>
          <ActivityIndicator
            size={'large'}
            animating={true}
            color={theme.colors.onPrimary}
          />
        </CenterView>
      );
    if (isError)
      return (
        <CenterView>
          <Text style={{fontSize: 40}}>😭</Text>
        </CenterView>
      );
    if (isSuccess)
      return (
        <Carousel
          style={{flex: 1}}
          layout={'default'}
          vertical={false}
          layoutCardOffset={9}
          ref={carouselRef}
          data={places}
          renderItem={PlaceRecommendCard}
          sliderWidth={screenWidth}
          itemWidth={screenWidth - 80}
          inactiveSlideShift={0}
          useScrollView={true}
        />
      );
  };

  return (
    <>
      <StyledView style={{marginTop: 10, marginBottom: 20}}>
        <View style={{maxWidth: 120}}>
          <SelectDropdown
            data={Object.keys(Category)}
            defaultValueByIndex={0}
            onSelect={(selectedItem, index) => {
              dispatch(selectCategory(Object.values(Category)[index]));
            }}
            buttonStyle={styles.dropdown2BtnStyle}
            buttonTextStyle={styles.dropdown2BtnTxtStyle}
            renderDropdownIcon={isOpened => {
              return (
                <Icon
                  name={isOpened ? 'chevron-up' : 'chevron-down'}
                  color={'#FFF'}
                  size={18}
                />
              );
            }}
            dropdownIconPosition={'right'}
            dropdownStyle={styles.dropdown2DropdownStyle}
            rowStyle={styles.dropdown2RowStyle}
            rowTextStyle={styles.dropdown2RowTxtStyle}
          />
        </View>
        {placeTagList.map(tag => {
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
      </StyledView>

      {carouselSection()}

      <StyledView style={{justifyContent: 'center'}}>
        <IconButton
          icon="arrow-down-drop-circle"
          iconColor={theme.colors.background}
          size={35}
          style={{marginVertical: 20}}
          onPress={() => placeAddBtnPressed()}
        />
      </StyledView>
      <StyledView style={{height: 70}}>
        {placeCart?.map(place => {
          return (
            <Tooltip key={place.id} title={place.name} enterTouchDelay={1}>
              <View style={{marginRight: 5}}>
                <Avatar.Image size={50} source={{uri: place.image}} />
                <IconButton
                  style={{position: 'absolute', right: -17, top: -17}}
                  icon="close-circle"
                  iconColor={theme.colors.background}
                  size={15}
                  onPress={() => placeCancelBtnPressed(place.id)}
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
  dropdown2BtnStyle: {
    width: '90%',
    height: 30,
    backgroundColor: '#444',
    borderRadius: 8,
  },
  dropdown2BtnTxtStyle: {
    color: '#FFF',
    textAlign: 'center',
    fontWeight: 'bold',
    fontSize: 14,
  },
  dropdown2DropdownStyle: {
    backgroundColor: '#444',
    borderBottomLeftRadius: 12,
    borderBottomRightRadius: 12,
  },
  dropdown2RowStyle: {backgroundColor: '#444', borderBottomColor: '#C5C5C5'},
  dropdown2RowTxtStyle: {
    color: '#FFF',
    textAlign: 'center',
    fontWeight: 'bold',
    fontSize: 14,
  },
});

export default PlacesRecommendScreen;
