#!/bin/bash

# Script simples para criar release
# Uso: ./create-release.sh 1.2.0

VERSION=$1

if [ -z "$VERSION" ]; then
    echo "❌ Erro: Forneça uma versão"
    echo "Uso: ./create-release.sh 1.2.0"
    exit 1
fi

echo "🚀 Criando release v$VERSION..."

# Criar tag
git tag -a "v$VERSION" -m "Release v$VERSION"

# Push da tag
git push origin "v$VERSION"

echo "✅ Release v$VERSION criada!"
echo "📋 GitHub Action criará a release automaticamente"
echo "🔗 Verifique em: https://github.com/lucas-de-lima/louve-mobile-app/releases"
