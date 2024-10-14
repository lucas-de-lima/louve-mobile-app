import React from "react";
import { HymnProvider } from "./contexts/HymnContext";
import Navigation from "./navigation/Navigation";

export default function App() {
  return (
    <HymnProvider>
      <Navigation />
    </HymnProvider>
  );
}
