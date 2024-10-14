import React from "react";
import { HymnProvider } from "./contexts/HymnContext";
import Navigation from "./navigation/Navigation";
import { NavigationContainer } from "@react-navigation/native";

export default function App() {
  return (
    <HymnProvider>
      <Navigation />
    </HymnProvider>
  );
}
