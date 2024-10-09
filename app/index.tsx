import React from "react";
import Home from "./pages/Home";
import { HymnProvider } from "./contexts/HymnContext";

export default function App() {
  return (
    <HymnProvider>
      <Home />
    </HymnProvider>
  );
}
