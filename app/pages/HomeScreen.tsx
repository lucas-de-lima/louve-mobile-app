import React from "react";
import { View, Text, TextInput, FlatList, TouchableOpacity, StyleSheet, Image } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Search, Music } from "lucide-react-native";

const hymns = [
  { id: "1", title: "Amazing Grace", thumbnail: "https://via.placeholder.com/80" },
  { id: "2", title: "How Great Thou Art", thumbnail: "https://via.placeholder.com/80" },
  { id: "3", title: "It Is Well With My Soul", thumbnail: "https://via.placeholder.com/80" },
  // Adicione mais hinos aqui
];

interface HymnItemProps {
  title: string;
  thumbnail: string;
  onPress: () => void;
}

interface HomeScreenProps {
  readonly navigation: any;
}

const HymnItem = ({ title, thumbnail, onPress }: HymnItemProps) => (
  <TouchableOpacity style={styles.hymnItem} onPress={onPress}>
    <Image source={{ uri: thumbnail }} style={styles.thumbnail} />
    <Text style={styles.hymnTitle}>{title}</Text>
  </TouchableOpacity>
);

export default function HomeScreen({ navigation }: HomeScreenProps) {
  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Music size={24} color="#4A5568" />
        <Text style={styles.appTitle}>Harp Hymns</Text>
      </View>
      <View style={styles.searchContainer}>
        <Search size={20} color="#4A5568" style={styles.searchIcon} />
        <TextInput
          style={styles.searchInput}
          placeholder="Search hymns..."
          placeholderTextColor="#A0AEC0"
        />
      </View>
      <FlatList
        data={hymns}
        renderItem={({ item }) => (
          <HymnItem
            title={item.title}
            thumbnail={item.thumbnail}
            onPress={() => navigation.navigate("HymnDetailScreen", { hymnId: item.id })}
          />
        )}
        keyExtractor={(item) => item.id}
        contentContainerStyle={styles.hymnList}
        horizontal={false}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#FFFFFF",
    paddingVertical: 16,
  },
  header: {
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: 16,
    paddingVertical: 12,
  },
  appTitle: {
    fontSize: 20,
    fontWeight: "bold",
    marginLeft: 8,
    color: "#2D3748",
  },
  searchContainer: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#EDF2F7",
    borderRadius: 8,
    marginHorizontal: 16,
    marginBottom: 16,
    paddingHorizontal: 12,
  },
  searchIcon: {
    marginRight: 8,
  },
  searchInput: {
    flex: 1,
    height: 40,
    fontSize: 16,
    color: "#2D3748",
  },
  hymnList: {
    paddingHorizontal: 16,
    paddingBottom: 16,
  },
  hymnItem: {
    flexDirection: "row",
    justifyContent: "flex-start",
    alignItems: "center",
    width: "100%",
    marginVertical: 8,
    backgroundColor: "#F7FAFC",
    borderRadius: 8,
    padding: 16,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  thumbnail: {
    width: 80,
    height: 80,
    borderRadius: 40,
    marginBottom: 8,
    marginRight: 16,
  },
  hymnTitle: {
    fontSize: 14,
    fontWeight: "600",
    textAlign: "center",
    color: "#2D3748",
  },
});
