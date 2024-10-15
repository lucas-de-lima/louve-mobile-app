import React, { useState } from "react";
import { ChevronLeft, Type, Share2, Heart, User } from "lucide-react-native";
import { hymns } from "../../Hymns/hymns";
import * as S from "./style";

interface HymnDetailScreen {
  readonly navigation: any;
  readonly route: any;
}

export default function HymnDetailScreen({ navigation, route }: HymnDetailScreen) {
  const [fontSize, setFontSize] = useState(22);

  const { hymnId } = route.params;
  const hymn = hymns.find((h) => h.id === hymnId);

  const increaseFontSize = () => {
    setFontSize((prevSize) => Math.min(prevSize + 2, 30));
  };

  return (
    <S.Container>
      <S.Header>
        <ChevronLeft size={24} color="#4A5568" onPress={() => navigation.goBack()} />
        <S.HeaderTitle>Hinos da Harpa</S.HeaderTitle>
      </S.Header>
      <S.Content contentContainerStyle={{ alignItems: "center" }}>
        {hymn ? (
          <>
            <S.HymnNumber style={{ fontSize: fontSize + 16 }}>Hino {hymn.number}</S.HymnNumber>
            <S.HymnTitle style={{ fontSize: fontSize + 8 }}>{hymn.title}</S.HymnTitle>
            {hymn.lyrics.map((section) => {
              const isChorus = section.type === "chorus";
              return (
                <S.LyricText
                  key={section.content}
                  style={{
                    fontSize: fontSize,
                    fontWeight: isChorus ? "bold" : "normal",
                  }}
                >
                  {section.content}
                </S.LyricText>
              );
            })}
          </>
        ) : (
          <S.ErrorText>Hino não encontrado!</S.ErrorText>
        )}
      </S.Content>
      <S.Footer>
        <S.AnimatedButton onPress={increaseFontSize}>
          <Type size={24} color="#4A5568" />
          <S.FooterButtonText>Increase Font</S.FooterButtonText>
        </S.AnimatedButton>
        <S.AnimatedButton>
          <Share2 size={24} color="#4A5568" />
          <S.FooterButtonText>Share</S.FooterButtonText>
        </S.AnimatedButton>
        <S.AnimatedButton>
          <Heart size={24} color="#4A5568" />
          <S.FooterButtonText>Like</S.FooterButtonText>
        </S.AnimatedButton>
        <S.AnimatedButton>
          <User size={24} color="#4A5568" />
          <S.FooterButtonText>Profile</S.FooterButtonText>
        </S.AnimatedButton>
      </S.Footer>
    </S.Container>
  );
}
