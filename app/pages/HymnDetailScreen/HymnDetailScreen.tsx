import React, { useState } from "react";
import { ChevronLeft, Type, Share2, Heart, User } from "lucide-react-native";
import Animated, { useSharedValue, useAnimatedStyle, withSpring } from "react-native-reanimated";

import { hymns } from "../../Hymns/hymns";
import * as S from "./style";

interface HymnDetailScreen {
  readonly navigation: any;
  readonly route: any;
}

export default function HymnDetailScreen({ navigation, route }: HymnDetailScreen) {
  const [fontSize, setFontSize] = useState(22);
  const [isPressed, setIsPressed] = useState({
    increaseFont: false,
    share: false,
    like: false,
    profile: false,
  });

  const { hymnId } = route.params;
  const hymn = hymns.find((h) => h.id === hymnId);

  const increaseFontSize = () => {
    setFontSize((prevSize) => Math.min(prevSize + 2, 30));
  };

  const scale = useSharedValue(1);

  const animatedStyle = useAnimatedStyle(() => {
    return {
      transform: [{ scale: scale.value }],
    };
  });

  const handlePressIn = () => {
    scale.value = withSpring(0.95, { damping: 10 });
  };

  const handlePressOut = () => {
    scale.value = withSpring(1, { damping: 10 });
  };

  const handlePress = (button: string) => {
    setIsPressed((prev) => ({ ...prev, [button]: true }));
    setTimeout(() => {
      setIsPressed((prev) => ({ ...prev, [button]: false }));
    }, 200);
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
        <Animated.View style={[animatedStyle]}>
          <S.AnimatedButton
            onPressIn={isPressed.increaseFont ? handlePressIn : undefined}
            onPressOut={isPressed.increaseFont ? handlePressOut : undefined}
            onPress={increaseFontSize}
          >
            <Type size={24} color="#4A5568" />
          </S.AnimatedButton>
        </Animated.View>
        <Animated.View style={[animatedStyle]}>
          <S.AnimatedButton
            onPressIn={isPressed.share ? handlePressIn : undefined}
            onPressOut={isPressed.share ? handlePressOut : undefined}
            onPress={() => handlePress("share")}
          >
            <Share2 size={24} color="#4A5568" />
          </S.AnimatedButton>
        </Animated.View>
        <Animated.View style={[animatedStyle]}>
          <S.AnimatedButton
            onPressIn={isPressed.like ? handlePressIn : undefined}
            onPressOut={isPressed.like ? handlePressOut : undefined}
            onPress={() => handlePress("like")}
          >
            <Heart size={24} color="#4A5568" />
          </S.AnimatedButton>
        </Animated.View>
        <Animated.View style={[animatedStyle]}>
          <S.AnimatedButton
            onPressIn={isPressed.profile ? handlePressIn : undefined}
            onPressOut={isPressed.profile ? handlePressOut : undefined}
            onPress={() => handlePress("profile")}
          >
            <User size={24} color="#4A5568" />
          </S.AnimatedButton>
        </Animated.View>
      </S.Footer>
    </S.Container>
  );
}
