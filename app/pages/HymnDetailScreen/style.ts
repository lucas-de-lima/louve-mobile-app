import { SafeAreaView, ScrollView, Text, TouchableOpacity, View } from "react-native";
import styled from "styled-components/native";

export const Container = styled(SafeAreaView)`
  flex: 1;
  background-color: #ffffff;
`;

export const Header = styled(View)`
  flex-direction: row;
  align-items: center;
  padding: 16px;
  border-bottom-width: 1px;
  border-bottom-color: #e2e8f0;
`;

export const HeaderTitle = styled(Text)`
  font-size: 18px;
  font-weight: 600;
  margin-left: 16px;
  color: #2d3748;
`;

export const Content = styled(ScrollView)`
  flex: 1;
  padding: 16px;
`;

export const HymnNumber = styled(Text)`
  font-size: 24px;
  font-weight: bold;
  color: #2d3748;
  margin-bottom: 8px;
`;

export const HymnTitle = styled(Text)`
  font-size: 20px;
  font-weight: 600;
  color: #4a5568;
  margin-bottom: 16px;
`;

export const LyricText = styled(Text)`
  margin-bottom: 16px;
  line-height: 24px;
  color: #2d3748;
  padding: 6px;
`;

export const ChorusText = styled(LyricText)`
  font-weight: bold;
`;

export const Footer = styled(View)`
  flex-direction: row;
  justify-content: space-around;
  padding: 12px 0;
  border-top-width: 1px;
  border-top-color: #e2e8f0;
`;

export const AnimatedButton = styled(TouchableOpacity)`
  background-color: #f7fafc;
  padding: 12px;
  border-radius: 8px;
  align-items: center;
  justify-content: center;
  shadow-color: #000;
  shadow-offset: 0px 4px;
  shadow-opacity: 0.1;
  shadow-radius: 4px;
  elevation: 3;
  flex: 1;
  max-width: 100px;
  max-height: 60px;
  margin: 0 8px;

  &:active {
    transform: scale(0.96);
  }
`;

export const FooterButtonText = styled(Text)`
  margin-top: 4px;
  font-size: 12px;
  color: #2d3748;
`;

export const ErrorText = styled(Text)`
  font-size: 18px;
  color: red;
  text-align: center;
  margin-top: 20px;
`;