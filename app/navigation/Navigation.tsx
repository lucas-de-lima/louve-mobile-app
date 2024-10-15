import React from "react";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import HomeScreen from "../pages/HomeScreen/HomeScreen";
import HymnDetailScreen from "../pages/HymnDetailScreen/HymnDetailScreen";
import { TransitionPresets } from "@react-navigation/stack";

const Stack = createNativeStackNavigator();

export default function Navigation() {
  return (
    <Stack.Navigator
      initialRouteName="HomeScreen"
      screenOptions={{
        gestureEnabled: true,
        ...TransitionPresets.SlideFromRightIOS.transitionSpec,
      }}
    >
      <Stack.Screen name="HomeScreen" component={HomeScreen} options={{ headerShown: false }} />
      <Stack.Screen
        name="HymnDetailScreen"
        component={HymnDetailScreen}
        options={{ headerShown: false }}
      />
    </Stack.Navigator>
  );
}
