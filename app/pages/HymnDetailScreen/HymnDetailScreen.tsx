import React, { useState } from "react";
import { View, Text, ScrollView, TouchableOpacity, StyleSheet } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { ChevronLeft, Type, Share2, Heart, User } from "lucide-react-native";
import { hymns } from "../../Hymns/hymns";

interface HymnDetailScreen {
  readonly navigation: any;
  readonly route: any;
}

export default function HymnDetailScreen({ navigation, route }: HymnDetailScreen) {
  const [fontSize, setFontSize] = useState(16);

  const { hymnId } = route.params;
  const hymn = hymns.find((h) => h.id === hymnId);

  const increaseFontSize = () => {
    setFontSize((prevSize) => Math.min(prevSize + 2, 24));
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <ChevronLeft size={24} color="#4A5568" onPress={() => navigation.goBack()} />
        <Text style={styles.headerTitle}>Harp Hymns</Text>
      </View>
      <ScrollView style={styles.content} contentContainerStyle={{ alignItems: "center" }}>
        {hymn ? (
          <>
            <Text style={styles.hymnNumber}>Hymn {hymn.number}</Text>
            <Text style={styles.hymnTitle}>{hymn.title}</Text>
            {hymn.lyrics.map((section) => (
              <Text
                key={section.content}
                style={[
                  styles.lyricText,
                  { fontSize },
                  section.type === "chorus" && styles.chorusText,
                ]}
              >
                {section.content}
              </Text>
            ))}
          </>
        ) : (
          <Text style={styles.errorText}>Hymn not found</Text>
        )}
      </ScrollView>
      <View style={styles.footer}>
        <TouchableOpacity style={styles.footerButton} onPress={increaseFontSize}>
          <Type size={24} color="#4A5568" />
          <Text style={styles.footerButtonText}>Increase Font</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.footerButton}>
          <Share2 size={24} color="#4A5568" />
          <Text style={styles.footerButtonText}>Share</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.footerButton}>
          <Heart size={24} color="#4A5568" />
          <Text style={styles.footerButtonText}>Like</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.footerButton}>
          <User size={24} color="#4A5568" />
          <Text style={styles.footerButtonText}>Profile</Text>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#FFFFFF",
  },
  header: {
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: 16,
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: "#E2E8F0",
  },
  headerTitle: {
    fontSize: 18,
    fontWeight: "600",
    marginLeft: 16,
    color: "#2D3748",
  },
  content: {
    flex: 1,
    padding: 16,
  },
  hymnNumber: {
    fontSize: 24,
    fontWeight: "bold",
    color: "#2D3748",
    marginBottom: 8,
  },
  hymnTitle: {
    fontSize: 20,
    fontWeight: "600",
    color: "#4A5568",
    marginBottom: 16,
  },
  lyricText: {
    marginBottom: 16,
    lineHeight: 24,
    color: "#2D3748",
  },
  chorusText: {
    fontWeight: "bold",
  },
  footer: {
    flexDirection: "row",
    justifyContent: "space-around",
    paddingVertical: 12,
    borderTopWidth: 1,
    borderTopColor: "#E2E8F0",
  },
  footerButton: {
    alignItems: "center",
  },
  errorText: {
    fontSize: 18,
    color: "red",
    textAlign: "center",
    marginTop: 20,
  },
  footerButtonText: {
    marginTop: 4,
    fontSize: 12,
    color: "#2D3748",
  },
});
