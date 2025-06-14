LouveApp/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/louveapp/
│   │   │   │   ├── LouveApp.kt                  ← Application class
│   │   │   │   ├── core/                         ← Base abstractions/utilitários
│   │   │   │   ├── data/                         ← Fontes de dados (mock/real)
│   │   │   │   │   ├── repository/               ← Implementação dos repositórios
│   │   │   │   ├── domain/                       ← Contratos de negócio
│   │   │   │   │   ├── model/                    ← Data classes puras
│   │   │   │   │   ├── repository/               ← Interfaces dos repositórios
│   │   │   │   ├── ui/                           ← Camada de apresentação
│   │   │   │   │   ├── theme/                    ← Design system e theming
│   │   │   │   │   ├── navigation/               ← NavGraph e rotas
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── home/                 ← Tela de listagem de hinos
│   │   │   │   │   │   └── hymn/                 ← Tela de detalhes do hino
│   │   │   │   │   ├── viewmodel/                ← ViewModels de cada tela
│   │   │   ├── res/
│   │   │   │   ├── drawable/                     ← Ícones etc.
│   │   │   │   ├── values/                       ← strings.xml (opcional)
│   │   │   └── AndroidManifest.xml
