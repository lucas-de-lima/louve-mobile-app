import React from "react";
import { View, Text, Image, StyleSheet } from "react-native";

interface HymnThumbnailProps {
  readonly hymnNumber: number;
}

export default function HymnThumbnail({ hymnNumber }: HymnThumbnailProps) {
  return (
    <View style={styles.thumbnailContainer}>
      <Image source={require("../bg.jpg")} style={styles.thumbnailBackground} />
      <Text style={styles.thumbnailText}>22</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  thumbnailContainer: {
    width: 80,
    height: 80,
    position: "relative", // Para permitir que o texto seja posicionado sobre a imagem
    justifyContent: "center", // Centraliza o número verticalmente
    alignItems: "center", // Centraliza o número horizontalmente
  },
  thumbnailBackground: {
    width: "100%",
    height: "100%",
    position: "absolute", // A imagem fica como fundo
    borderRadius: 40, // Para manter o formato redondo, se necessário
  },
  thumbnailText: {
    fontSize: 24,
    color: "white",
    fontWeight: "bold",
    zIndex: 1, // Garante que o texto fique por cima da imagem
  },
});
