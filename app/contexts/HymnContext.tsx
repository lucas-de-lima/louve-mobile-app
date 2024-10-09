import React, { createContext, useState, ReactNode } from "react";

interface HymnContextProps {
  selectedHymn: string | null;
  setSelectedHymn: (hymn: string) => void;
}

export const HymnContext = createContext<HymnContextProps | undefined>(undefined);

export const HymnProvider = ({ children }: { children: ReactNode }) => {
  const [selectedHymn, setSelectedHymn] = useState<string | null>(null);

  return (
    <HymnContext.Provider value={{ selectedHymn, setSelectedHymn }}>
      {children}
    </HymnContext.Provider>
  );
};
