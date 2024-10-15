import React from "react";
import { Search, Music } from "lucide-react-native";
import { hymns } from "../../Hymns/hymns";
import * as S from "./styles";

interface HomeScreenProps {
  readonly navigation: any;
}

export default function HomeScreen({ navigation }: HomeScreenProps) {
  return (
    <S.Container>
      <S.Header>
        <Music size={24} color="#4A5568" />
        <S.AppTitle>Hinos da Harpa</S.AppTitle>
      </S.Header>
      <S.SearchContainer>
        <Search size={20} color="#4A5568" />
        <S.SearchInput placeholder="Search hymns..." placeholderTextColor="#A0AEC0" />
      </S.SearchContainer>
      <S.HymnList
        data={hymns}
        renderItem={({ item }: any) => (
          <S.HymnItem onPress={() => navigation.navigate("HymnDetailScreen", { hymnId: item.id })}>
            <S.Thumbnail source={{ uri: item.thumbnail }} />
            <S.HymnTitle>{item.title}</S.HymnTitle>
          </S.HymnItem>
        )}
        keyExtractor={(item: any) => item.id}
      />
    </S.Container>
  );
}
