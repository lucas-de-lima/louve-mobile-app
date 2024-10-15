import React from "react";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import HomeScreen from "../pages/HomeScreen/HomeScreen";
import HymnDetailScreen from "../pages/HymnDetailScreen/HymnDetailScreen";

const Stack = createNativeStackNavigator();

export default function Navigation() {
  return (
    <Stack.Navigator initialRouteName="HomeScreen">
      <Stack.Screen name="HomeScreen" component={HomeScreen} options={{ headerShown: false }} />
      <Stack.Screen
        name="HymnDetailScreen"
        component={HymnDetailScreen}
        options={{ headerShown: false }}
      />
    </Stack.Navigator>
  );
}
