import styled from 'styled-components/native';

export const Container = styled.SafeAreaView`
  flex: 1;
  background-color: #ffffff;
  padding-vertical: 16px;
`;

export const Header = styled.View`
  flex-direction: row;
  align-items: center;
  padding-horizontal: 16px;
  padding-vertical: 12px;
`;

export const AppTitle = styled.Text`
  font-size: 20px;
  font-weight: bold;
  margin-left: 8px;
  color: #2d3748;
`;

export const SearchContainer = styled.View`
  flex-direction: row;
  align-items: center;
  background-color: #edf2f7;
  border-radius: 8px;
  margin-horizontal: 16px;
  margin-bottom: 16px;
  padding-horizontal: 12px;
`;

export const SearchInput = styled.TextInput`
  flex: 1;
  height: 40px;
  font-size: 16px;
  color: #2d3748;
`;

export const HymnList = styled.FlatList`
  padding-horizontal: 16px;
  padding-bottom: 16px;
`;

export const HymnItem = styled.TouchableOpacity`
  flex-direction: row;
  justify-content: flex-start;
  align-items: center;
  width: 100%;
  margin-vertical: 8px;
  background-color: #f7fafc;
  border-radius: 8px;
  padding: 16px;
  shadow-color: #000;
  shadow-offset: 0px 2px;
  shadow-opacity: 0.1;
  shadow-radius: 4px;
  elevation: 3;
`;

export const Thumbnail = styled.Image`
  width: 80px;
  height: 80px;
  border-radius: 40px;
  margin-bottom: 8px;
  margin-right: 16px;
`;

export const HymnTitle = styled.Text`
  font-size: 14px;
  font-weight: 600;
  text-align: center;
  color: #2d3748;
`;
