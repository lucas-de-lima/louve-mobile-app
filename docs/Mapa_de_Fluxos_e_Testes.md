# Mapa de Fluxos e Implementação do Código - Louve App

**Público-Alvo:** Desenvolvedores, Arquitetos, Novos membros da equipe.

**Propósito:** Mapear de forma detalhada todos os fluxos de código do app, baseado na implementação real, para servir como referência técnica e guia de navegação no código.

---

## 📋 **ÍNDICE**

1. [Arquitetura Geral](#arquitetura-geral)
2. [Fluxo de Inicialização do App](#fluxo-de-inicialização-do-app)
3. [Sistema de Navegação](#sistema-de-navegação)
4. [Sistema de Temas](#sistema-de-temas)
5. [Sistema de Autenticação](#sistema-de-autenticação)
6. [Sistema de Favoritos](#sistema-de-favoritos)
7. [Sistema de Busca de Hinos](#sistema-de-busca-de-hinos)
8. [Sistema de Sincronização](#sistema-de-sincronização)
9. [Injeção de Dependência](#injeção-de-dependência)
10. [Estrutura de Dados](#estrutura-de-dados)

---

## 🏗️ **ARQUITETURA GERAL**

### **Camadas Implementadas:**

```
┌─────────────────────────────────────────────────────────────┐
│                         UI LAYER                            │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │    Screens      │  │   Components    │  │  Navigation │ │
│  │  - HomeScreen   │  │ - SearchField   │  │ - NavGraph  │ │
│  │  - SettingsScr  │  │ - HymnCard      │  │ - Routes    │ │
│  │  - ProfileScr   │  │ - TopAppBar     │  │ - BottomNav │ │
│  │  - HymnDetail   │  │ - BottomNavBar  │  │             │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
│                              │                              │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │   ViewModels    │  │     Theme       │  │   Common    │ │
│  │ - HomeViewModel │  │ - LouveTheme    │  │ - UiState   │ │
│  │ - SettingsVM    │  │ - AppThemes     │  │ - AuthHelpr │ │
│  │ - AuthViewModel │  │ - ThemeData     │  │             │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       DOMAIN LAYER                          │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │     Models      │  │  Repositories   │  │   Config    │ │
│  │  - Hymn         │  │ - AuthRepo      │  │             │ │
│  │  - UserProfile  │  │ - HymnRepo      │  │             │ │
│  │  - UserSettings │  │ - FavoritesRepo │  │             │ │
│  │  - Result       │  │ - SettingsRepo  │  │             │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                        DATA LAYER                           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │ Repository Impl │  │   DataSources   │  │   Services  │ │
│  │ - FirebaseAuth  │  │ - HymnDataSrc   │  │ - Migration │ │
│  │ - FirestoreUser │  │ - DataStore     │  │ - Sync      │ │
│  │ - DefaultFavs   │  │ - Firebase      │  │ - Analytics │ │
│  │ - DefaultSetts  │  │                 │  │ - Connect   │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

---

## 🚀 **FLUXO DE INICIALIZAÇÃO DO APP**

### **1. Inicialização Principal**

```kotlin
// Arquivo: MainActivity.kt
MainActivity.onCreate() {
    installSplashScreen()           // Instala splash screen nativo
    enableEdgeToEdge()              // Habilita modo edge-to-edge
    setContent {
        LouveApp(viewModel)         // Inicia composição principal
    }
}
```

### **2. Configuração do Tema**

```kotlin
// Arquivo: MainActivity.kt - LouveApp()
val currentTheme by viewModel.currentTheme.collectAsState()
val selectedTheme = AllThemes.find { it.name == currentTheme } ?: DefaultTheme

LouveAppTheme(themeData = selectedTheme) {
    Box(modifier = Modifier.fillMaxSize()) {
        LouveTheme.backgrounds.screenBackground()    // Fundo global
        NavGraph(navController = navController)      // Navegação
    }
}
```

### **3. Carregamento de Dados**

```kotlin
// Arquivo: MainViewModel.kt
val currentTheme: StateFlow<String> = settingsRepository.theme
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = "Padrão Claro"
    )
```

---

## 🧭 **SISTEMA DE NAVEGAÇÃO**

### **1. Estrutura Hierárquica**

```
NavGraph (Raiz)
├── SPLASH → SplashScreen
├── MAIN → MainScreen (com BottomNavigation)
│   ├── home_screen → HomeScreen
│   ├── favorites_screen → FavoritesScreen
│   ├── discover_screen → DiscoverScreen
│   ├── more_screen → MoreScreen
│   └── hymnDetail/{id} → HymnDetailScreen
├── SETTINGS → SettingsScreen
├── PROFILE → ProfileScreen
├── ABOUT → AboutScreen
└── SUPPORT → SupportScreen
```

### **2. Implementação de Rotas**

```kotlin
// Arquivo: NavGraph.kt
object Routes {
    const val SPLASH = "splash"
    const val MAIN = "main"
    const val HYMN_DETAIL = "hymnDetail/{id}"
    const val SETTINGS = "settings"
    const val PROFILE = "profile"
    const val ABOUT = "about"
    const val SUPPORT = "support"
}
```

### **3. Navegação Inferior**

```kotlin
// Arquivo: BottomNavItem.kt
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Harpa : BottomNavItem("home_screen", "Harpa", Icons.Outlined.Home)
    data object Favorites : BottomNavItem("favorites_screen", "Favoritos", Icons.Outlined.FavoriteBorder)
    data object Discover : BottomNavItem("discover_screen", "Descubra", Icons.Outlined.Search)
    data object More : BottomNavItem("more_screen", "Mais", Icons.Outlined.Menu)
}
```

---

## 🎨 **SISTEMA DE TEMAS**

### **1. Estrutura de Dados**

```kotlin
// Arquivo: LouveThemeData.kt
data class LouveThemeData(
    val id: String,                    // Identificador único
    val name: String,                  // Nome exibido
    val category: ThemeCategory,       // Light/Dark/Custom
    val colors: ColorScheme,           // Paleta Material 3
    val typography: Typography,        // Tipografia
    val backgrounds: LouveBackgrounds, // Fundos personalizados
    val isDefault: Boolean = false     // Tema padrão
)

data class LouveBackgrounds(
    val screenBackground: @Composable () -> Unit,      // Fundo principal
    val detailScreenBackground: @Composable () -> Unit // Fundo imersivo
)
```

### **2. Fluxo de Aplicação**

```
1. MainViewModel observa settingsRepository.theme
2. MainActivity recebe tema atual via StateFlow
3. LouveAppTheme aplica cores e tipografia
4. screenBackground é renderizado uma vez na MainActivity
5. Todas as telas usam containerColor = Color.Transparent
6. detailScreenBackground é usado apenas em HymnDetailScreen
```

### **3. Temas Implementados**

```kotlin
// Arquivo: AppThemes.kt
val AllThemes = listOf(
    DefaultTheme,        // Tema padrão claro
    DarkTheme,           // Tema escuro
    SweetCandyTheme      // Tema gradiente personalizado
)
```

---

## 🔐 **SISTEMA DE AUTENTICAÇÃO**

### **1. Fluxo de Login**

```
UI (SettingsScreen)
    ↓ rememberGoogleSignInLauncher
GoogleSignInHelper
    ↓ onIdTokenReceived
AuthViewModel.signIn()
    ↓ AuthCredentials.Google
FirebaseAuthRepositoryImpl.signIn()
    ↓ GoogleAuthProvider.getCredential
Firebase Authentication
    ↓ authResult.user
UserRepository.ensureUserStructure()
    ↓ Firestore Transaction
DataMigrationService.migrateLocalDataToCloud()
```

### **2. Estados de Autenticação**

```kotlin
// Arquivo: AuthUiState.kt
sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: UserProfile) : AuthUiState()
    data class Error(val error: AuthError, val retry: () -> Unit) : AuthUiState()
}
```

### **3. Tratamento de Erros**

```kotlin
// Arquivo: AuthUiState.kt
sealed class AuthError {
    object NetworkError : AuthError()
    object InvalidCredentials : AuthError()
    object UserCancelled : AuthError()
    data class FirebaseError(val code: String, val message: String) : AuthError()
    data class UnknownError(val message: String) : AuthError()
}
```

---

## ⭐ **SISTEMA DE FAVORITOS**

### **1. Arquitetura Híbrida**

```
FavoritesRepository (Interface)
    ↓
DefaultFavoritesRepository (Mediador)
    ├── LocalFavoritesRepository (DataStore)
    └── UserRepository (Firestore)
```

### **2. Fluxo de Adição**

```
HymnDetailScreen.onToggleFavorite()
    ↓
HymnDetailViewModel.onToggleFavorite()
    ↓
FavoritesRepository.addFavorite()
    ↓
DefaultFavoritesRepository.addFavorite()
    ├── Se usuário logado → UserRepository.addFavorite()
    └── Se usuário não logado → LocalFavoritesRepository.addFavorite()
```

### **3. Observação em Tempo Real**

```kotlin
// Arquivo: DefaultFavoritesRepository.kt
override fun getFavoriteHymnIds(): Flow<Result<Set<String>>> {
    return authRepository.getCurrentUser().flatMapLatest { user ->
        if (user != null) {
            remoteRepository.getFavoriteHymnIds()    // Firestore
        } else {
            localRepository.getFavorites().map { favorites ->
                Result.Success(favorites)            // DataStore
            }
        }
    }
}
```

---

## 🔍 **SISTEMA DE BUSCA DE HINOS**

### **1. Algoritmo de Busca**

```kotlin
// Arquivo: HomeViewModel.kt
private fun filterHymns() {
    val queryWords = query.normalizeForSearch().split(' ')
    
    val filteredHymns = originalHymns.filter { hymn ->
        val searchableContent = (
            hymn.title + " " +
            hymn.number.toString().padStart(3, '0') + " " +
            hymn.verses.joinToString(" ") + " " +
            hymn.chorus
        ).normalizeForSearch()
        
        queryWords.all { word -> searchableContent.contains(word) }
    }
}
```

### **2. Normalização de Texto**

```kotlin
// Arquivo: HomeViewModel.kt
fun String.normalizeForSearch(): String {
    val unaccented = this.unaccent()  // Remove acentos
    return unaccented.lowercase()     // Converte para minúsculas
        .replace(Regex("[.,!?;:]"), "") // Remove pontuação
}
```

### **3. Debounce de Busca**

```kotlin
// Arquivo: HomeViewModel.kt
fun onSearchQueryChanged(query: String) {
    _uiState.update { it.copy(searchQuery = query) }
    searchJob?.cancel()
    searchJob = viewModelScope.launch {
        delay(300)  // Debounce de 300ms
        filterHymns()
    }
}
```

---

## 🔄 **SISTEMA DE SINCRONIZAÇÃO**

### **1. Serviços de Sincronização**

#### **DataMigrationService**
```kotlin
// Migração automática após login
suspend fun migrateLocalDataToCloud(): Result<Unit> {
    val localData = backupLocalData()
    val cloudData = checkCloudData()
    
    syncThemeIntelligently(localData.theme, cloudData)
    syncFavoritesIntelligently(localData.favorites, cloudData.favorites)
    
    return Result.Success(Unit)
}
```

#### **BidirectionalSyncService**
```kotlin
// Sincronização bidirecional
suspend fun syncRemoteToLocal(): Result<Unit> {
    val favoritesResult = syncFavoritesRemoteToLocal()
    val settingsResult = syncSettingsRemoteToLocal()
    return Result.Success(Unit)
}
```

#### **ConnectivityMonitorService**
```kotlin
// Monitoramento de conectividade
fun startMonitoring() {
    val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()
    
    connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
}
```

### **2. Estratégias de Merge**

#### **Favoritos**
```kotlin
private fun mergeFavorites(local: Set<String>, remote: Set<String>): Set<String> {
    return when {
        local.isEmpty() -> remote
        remote.isEmpty() -> local
        else -> local.union(remote)  // União preserva todos
    }
}
```

#### **Tema**
```kotlin
private fun mergeTheme(local: String, remote: String): String {
    return when {
        local == DefaultTheme.name -> remote    // Preferir remoto se local é padrão
        remote == DefaultTheme.name -> local   // Preferir local se remoto é padrão
        else -> local                          // Em conflito, preferir local
    }
}
```

---

## 🏠 **FLUXO DETALHADO DAS TELAS**

### **1. HomeScreen (Tela Principal)**

#### **Inicialização:**
```
HomeViewModel.init() 
    ↓
loadInitialHymns()
    ↓ 
hymnRepository.getAllHymns() (HymnRepositoryImpl)
    ↓
HymnDataSource.allHymns (640 hinos em memória)
    ↓
filterHymns() (converte Hymn → HymnUi)
    ↓
_uiState.update { hymns = uiHymns }
```

#### **Busca:**
```
SearchField.onQueryChanged
    ↓
HomeViewModel.onSearchQueryChanged()
    ↓
debounce(300ms)
    ↓
filterHymns() com query normalizada
    ↓
UI atualizada automaticamente via StateFlow
```

### **2. HymnDetailScreen (Detalhes do Hino)**

#### **Carregamento:**
```
NavGraph → hymnDetail/{id}
    ↓
HymnDetailViewModel.setHymnId(id)
    ↓
observeHymnDetails() combina 3 flows:
    ├── authRepository.getCurrentUser()
    ├── favoritesRepository.getFavoriteHymnIds()
    └── hymnRepository.getHymnById(id)
    ↓
_uiState atualizado com dados combinados
```

#### **Favoritar:**
```
UI.onToggleFavorite()
    ↓
HymnDetailViewModel.onToggleFavorite()
    ↓
favoritesRepository.addFavorite(hymnId)
    ↓
DefaultFavoritesRepository decide:
    ├── Se logado → UserRepository.addFavorite()
    └── Se não logado → LocalFavoritesRepository.addFavorite()
```

### **3. SettingsScreen (Configurações)**

#### **Carregamento:**
```
SettingsViewModel.init()
    ↓
settingsRepository.theme
    ↓
DefaultSettingsRepository.theme (Flow)
    ├── Se logado → userRepository.getUserSettings()
    └── Se não logado → localSettingsRepository.theme
    ↓
map para SettingsUiState
    ↓
UI renderizada com temas disponíveis
```

#### **Mudança de Tema:**
```
ThemePreviewCard.onClick()
    ↓
SettingsViewModel.selectTheme(themeName)
    ↓
settingsRepository.saveTheme(themeName)
    ↓
DefaultSettingsRepository.saveTheme()
    ├── Se logado → userRepository.updateUserSettings()
    └── Se não logado → localSettingsRepository.saveTheme()
    ↓
MainViewModel observa mudança automaticamente
    ↓
UI atualizada em tempo real
```

---

## 💾 **ESTRUTURA DE DADOS**

### **1. Modelo de Domínio**

```kotlin
// Arquivo: Hymn.kt
data class Hymn(
    val id: Int,
    val number: Int,
    val title: String,
    val verses: List<String>,
    val chorus: String? = null
)

// Arquivo: UserProfile.kt
data class UserProfile(
    val uid: String,
    val name: String?,
    val email: String?,
    val photoUrl: String?,
    val createdAt: Long = System.currentTimeMillis()
)

// Arquivo: UserSettings.kt
data class UserSettings(
    val themeId: String = "default_light"
)
```

### **2. Estrutura no Firestore**

```
/users/{userId}/
├── uid: "string"
├── name: "string"
├── email: "string"
├── photoUrl: "string"
├── createdAt: timestamp
├── users/
│   └── settings/
│       └── themeId: "default_light"
└── favorites/
    └── hymns/
        └── ids: ["1", "15", "23"]
```

### **3. Estrutura no DataStore**

```kotlin
// Favoritos locais
private object PreferencesKeys {
    val FAVORITE_HYMN_IDS = stringSetPreferencesKey("favorite_hymn_ids")
}

// Configurações locais
private object Keys {
    val APP_THEME = stringPreferencesKey("app_theme")
}
```

---

## 🔧 **INJEÇÃO DE DEPENDÊNCIA**

### **1. Módulos Hilt**

```kotlin
// Arquivo: AppModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindAuthRepository(impl: FirebaseAuthRepositoryImpl): AuthRepository
    
    @Binds @Singleton
    abstract fun bindUserRepository(impl: FirestoreUserRepositoryImpl): UserRepository
    
    @Binds @Singleton
    abstract fun bindFavoritesRepository(impl: DefaultFavoritesRepository): FavoritesRepository
    
    @Binds @Singleton
    abstract fun bindSettingsRepository(impl: DefaultSettingsRepository): SettingsRepository
}
```

### **2. Providers**

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    
    @Provides @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
    
    @Provides @Singleton
    fun provideHymnRepository(): HymnRepository = HymnRepositoryImpl()
}
```

---

## 📊 **PERFORMANCE E OTIMIZAÇÕES**

### **1. Hinos em Memória**

```kotlin
// Arquivo: HymnDataSource.kt (gerado)
object HymnDataSource {
    val allHymns: List<Hymn> = listOf(
        Hymn(id = 1, number = 1, title = "Chuvas de Graça", ...),
        // ... 640 hinos compilados em código
    )
}
```

**Benefícios:**
- Acesso instantâneo (sem I/O)
- Busca extremamente rápida
- Sem parsing de JSON em runtime

### **2. StateFlow e Caching**

```kotlin
// Padrão usado em todos os ViewModels
val uiState: StateFlow<UiState> = repository.data
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InitialState
    )
```

### **3. Debounce em Buscas**

```kotlin
// Arquivo: HomeViewModel.kt
searchJob?.cancel()
searchJob = viewModelScope.launch {
    delay(300)  // Evita buscas excessivas
    filterHymns()
}
```

---

## 🎯 **FLUXOS CRÍTICOS IMPLEMENTADOS**

### **1. Login Completo**

```
1. Usuário clica "Entrar com Google"
2. GoogleSignInHelper.rememberGoogleSignInLauncher()
3. Google Sign-In UI → idToken
4. AuthViewModel.signIn(AuthCredentials.Google(idToken))
5. FirebaseAuthRepositoryImpl.signIn()
6. Firebase Auth cria sessão
7. UserRepository.ensureUserStructure()
8. DataMigrationService.migrateLocalDataToCloud()
9. Dados locais migrados para nuvem
10. UI atualizada com perfil do usuário
```

### **2. Mudança de Tema**

```
1. Usuário seleciona tema em SettingsScreen
2. SettingsViewModel.selectTheme()
3. SettingsRepository.saveTheme()
4. DefaultSettingsRepository decide local vs remoto
5. Dados salvos (DataStore ou Firestore)
6. MainViewModel observa mudança
7. MainActivity re-renderiza com novo tema
8. Todas as telas atualizadas automaticamente
```

### **3. Sincronização Automática**

```
1. ConnectivityMonitorService detecta rede
2. handleConnectivityChange(true)
3. syncDataWhenOnline() para usuários logados
4. BidirectionalSyncService.syncRemoteToLocal()
5. Favoritos e configurações sincronizados
6. UI atualizada automaticamente
```

---

## 📱 **COMPONENTES DE UI IMPLEMENTADOS**

### **1. Componentes Reutilizáveis**

```kotlin
// TopAppBars especializadas
- HomeTopAppBar (com botão configurações)
- FavoritesTopAppBar (simples)
- DiscoverTopAppBar (simples)
- MoreTopAppBar (simples)
- HymnDetailTopAppBar (com controles de fonte)

// Componentes de lista
- HymnCardItem (card com número e título)
- HymnListItem (item simples de lista)
- SearchField (campo de busca com debounce)

// Navegação
- LouveBottomNavBar (barra inferior transparente)
- BottomNavItem (definição de rotas)
```

### **2. Padrões de Layout**

```kotlin
// Padrão usado em todas as telas
Scaffold(
    topBar = { SpecificTopAppBar() },
    bottomBar = { LouveBottomNavBar() },
    containerColor = Color.Transparent
) { innerPadding ->
    // Conteúdo da tela
}
```

---

## 🔧 **CONFIGURAÇÕES TÉCNICAS**

### **1. Build Configuration**

```kotlin
// Arquivo: build.gradle.kts
android {
    compileSdk = 35
    defaultConfig {
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    
    // Configuração de assinatura para release
    signingConfigs {
        create("release") {
            // Lê de keystore.properties
        }
    }
}
```

### **2. Dependências Principais**

```kotlin
// Firebase
implementation(libs.firebase.auth.ktx)
implementation(libs.firebase.firestore.ktx)
implementation(libs.firebase.analytics)

// Google Sign-In
implementation(libs.google.gms.auth)

// Jetpack
implementation(libs.androidx.compose.bom)
implementation(libs.androidx.navigation.compose)
implementation(libs.datastore.preferences)

// Hilt
implementation(libs.hilt.android)
ksp(libs.hilt.compiler)
```

---

## 📋 **CHECKLIST DE VALIDAÇÃO**

### **Funcionalidades Implementadas:**
- [x] Sistema de navegação com barra inferior
- [x] Sistema de temas dinâmicos
- [x] Autenticação com Google Sign-In
- [x] Favoritos locais e remotos
- [x] Busca inteligente de hinos
- [x] Sincronização automática de dados
- [x] Migração de dados após login
- [x] Splash screen animada
- [x] Sistema de analytics
- [x] Tratamento de erros robusto

### **Arquitetura Implementada:**
- [x] Clean Architecture (ui/domain/data)
- [x] MVVM com StateFlow
- [x] Repository Pattern
- [x] Injeção de dependência com Hilt
- [x] Fluxo de dados unidirecional

### **Performance:**
- [x] Hinos carregados em memória
- [x] Busca com debounce
- [x] StateFlow para caching automático
- [x] Edge-to-edge para imersão visual

---

*Este documento reflete a implementação real do código do Louve App em dezembro de 2024.* 