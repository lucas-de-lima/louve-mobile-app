# Sistema de Categorização de Hinos - Louve App

**Versão:** 2.0  
**Data:** 2025  
**Público-Alvo:** IA Generativa, Desenvolvedores, Curadores de Conteúdo  
**Propósito:** Documentação técnica completa para categorização automática e manual dos 640 hinos da Harpa Cristã

---

## 📋 **ÍNDICE**

1. [Visão Geral do Sistema](#visão-geral-do-sistema)
2. [Arquitetura da Categorização](#arquitetura-da-categorização)
3. [Categorias Detalhadas](#categorias-detalhadas)
4. [Intensidade Espiritual](#intensidade-espiritual)
5. [Momentos Espirituais](#momentos-espirituais)
6. [Prompt Técnico para IA](#prompt-técnico-para-ia)
7. [Exemplos Práticos](#exemplos-práticos)
8. [Implementação Técnica](#implementação-técnica)
9. [Validação e Qualidade](#validação-e-qualidade)
10. [Tela de Descoberta](#tela-de-descoberta)

---

## 🎯 **VISÃO GERAL DO SISTEMA**

### **Objetivo**
Criar um sistema de categorização em português brasileiro que permita aos usuários descobrir hinos por tema, momento espiritual e intensidade, transformando a experiência de navegação em uma jornada de descoberta espiritual.

### **Filosofia**
- **Teologia como Base**: Categorias fundamentadas em doutrinas cristãs
- **Prática como Aplicação**: Momentos e contextos de uso real
- **Múltiplas Categorias**: Um hino pode pertencer a várias categorias
- **Linguagem Natural**: Nomes em português brasileiro para melhor compreensão
- **Descoberta Inteligente**: Preparado para tela de descoberta e recomendações

### **Estrutura do Sistema**
```kotlin
data class HymnMetadata(
    val categories: List<HymnCategory>,          // Múltiplas categorias (não exclusivas)
    val intensity: SpiritualIntensity,           // Intensidade espiritual
    val moment: SpiritualMoment,                 // Momento ideal de uso
    val confidence: Float = 1.0f,               // Confiança da categorização (0.0-1.0)
    val verified: Boolean = false               // Verificação manual
)

enum class HymnCategory {
    // Categorias principais em português
    SALVACAO,           // Salvação e conversão
    ADORACAO,           // Adoração e louvor
    CONSOLO,            // Consolo e paz
    MISSAO,             // Missão e evangelismo
    PROVACAO,           // Provações e sofrimento
    CURACAO,            // Cura e restauração
    ESPIRITO_SANTO,     // Espírito Santo e unção
    VOLTA_DE_CRISTO,    // Volta de Cristo
    ORACAO,             // Oração e súplica
    COMUNHAO,           // Comunhão e igreja
    GRATIDAO,           // Gratidão e ação de graças
    ESPERANCA,          // Esperança e promessas
    FORCA,              // Força e coragem
    AMOR,               // Amor e relacionamento
    SANTIDADE,          // Santidade e pureza
    SERVICO,            // Serviço e ministério
    TESTEMUNHO,         // Testemunho e experiência
    REFLEXAO,           // Reflexão e meditação
    CELEBRACAO,         // Celebração e festa
    REVERENCIA          // Reverência e solenidade
}
```

---

## 🏗️ **ARQUITETURA DA CATEGORIZAÇÃO**

### **Estrutura de Categorização**

```
CATEGORIAS (múltiplas, não exclusivas)
├── INTENSIDADE (1 obrigatória)
├── MOMENTO (1 obrigatória)
└── CONFIANÇA (automática)
```

### **Fluxo de Categorização**

```
HINO → ANÁLISE IA → MÚLTIPLAS CATEGORIAS → INTENSIDADE → MOMENTO → VALIDAÇÃO → APROVAÇÃO
  ↓         ↓              ↓                    ↓           ↓         ↓          ↓
Texto    Palavras      Teologia            Emocional   Prático   Manual    Produção
```

### **Princípios de Categorização**

1. **Múltiplas Categorias**: Um hino pode ter 1-5 categorias
2. **Não Exclusivas**: Categorias podem se sobrepor
3. **Linguagem Natural**: Nomes em português brasileiro
4. **Base Teológica**: Fundamentadas em doutrinas cristãs
5. **Aplicação Prática**: Úteis para descoberta e uso

---

## 📚 **CATEGORIAS DETALHADAS**

### **1. SALVAÇÃO**
**Definição:** Hinos que falam sobre conversão, redenção, perdão de pecados e a obra salvadora de Cristo.

**Palavras-chave Identificadoras:**
- Conversão, salvação, redenção, perdão
- Pecado, arrependimento, transformação
- Sangue de Cristo, cruz, sacrifício
- Novo nascimento, regeneração
- Justificação, graça salvadora

**Exemplos de Hinos:**
- Hino 15 - "Conversão"
- Hino 6 - "Na Maldição Da Cruz"
- Hinos sobre arrependimento e perdão

**Intensidade Típica:** MODERADO a INTENSO

**Momentos Ideais:** Altar call, apelo, conversão, arrependimento

**Categorias Relacionadas:** TESTEMUNHO, REFLEXÃO, ORACAO

---

### **2. ADORAÇÃO**
**Definição:** Hinos de louvor, adoração, exaltação a Deus, Cristo e Espírito Santo.

**Palavras-chave Identificadoras:**
- Louvor, adoração, glória, honra
- Aleluia, hosana, bendito
- Rei, Senhor, Deus, Cristo
- Trono, céu, santidade
- Magnificar, exaltar, glorificar

**Exemplos de Hinos:**
- Hino 10 - "Eu Te Louvo"
- Hino 3 - "Plena Paz"
- Hinos de glória e louvor

**Intensidade Típica:** MODERADO a EXTÁTICO

**Momentos Ideais:** Culto de adoração, momentos de louvor, celebração

**Categorias Relacionadas:** GRATIDÃO, CELEBRACAO, REVERENCIA

---

### **3. CONSOLO**
**Definição:** Hinos que oferecem consolo, paz, esperança em momentos difíceis.

**Palavras-chave Identificadoras:**
- Consolo, conforto, paz, esperança
- Fortaleza, refúgio, abrigo
- Cuidado, proteção, amparo
- Alívio, tranquilidade, serenidade
- Promessas, segurança, descanso

**Exemplos de Hinos:**
- Hino 4 - "Deus Velará Por Ti"
- Hinos sobre cuidado divino
- Hinos de esperança

**Intensidade Típica:** CONTEMPLATIVO a MODERADO

**Momentos Ideais:** Provações, luto, momentos difíceis, meditação

**Categorias Relacionadas:** ESPERANCA, FORCA, AMOR

---

### **4. MISSÃO**
**Definição:** Hinos sobre evangelismo, missões, serviço cristão e chamado.

**Palavras-chave Identificadoras:**
- Missão, evangelismo, pregação
- Chamado, serviço, trabalho
- Almas, perdidos, conversão
- Boas novas, evangelho
- Discipulado, testemunho

**Exemplos de Hinos:**
- Hino 11 - "Ó Cristão, Eia Avante"
- Hinos sobre missões
- Hinos de evangelismo

**Intensidade Típica:** MODERADO a INTENSO

**Momentos Ideais:** Cultos missionários, conferências, evangelismo

**Categorias Relacionadas:** SERVICO, TESTEMUNHO, FORCA

---

### **5. PROVAÇÃO**
**Definição:** Hinos para momentos de dificuldade, sofrimento, luta e perseverança.

**Palavras-chave Identificadoras:**
- Provação, sofrimento, luta, dor
- Perseverança, resistência, força
- Deserto, vale, escuridão
- Tribulação, angústia, aflição
- Vitória, superação, triunfo

**Exemplos de Hinos:**
- Hinos sobre vales e montes
- Hinos de perseverança
- Hinos de vitória

**Intensidade Típica:** INTENSO a SOLENE

**Momentos Ideais:** Momentos difíceis, luto, crises, perseverança

**Categorias Relacionadas:** FORCA, ESPERANCA, CONSOLO

---

### **6. CURA**
**Definição:** Hinos sobre cura divina, restauração, bênçãos e milagres.

**Palavras-chave Identificadoras:**
- Cura, restauração, bênção
- Milagre, poder, unção
- Saúde, libertação, vitória
- Bálsamo, remédio, alívio
- Ressurreição, renovação

**Exemplos de Hinos:**
- Hino 7 - "Cristo Cura Sim!"
- Hinos sobre cura divina
- Hinos de restauração

**Intensidade Típica:** MODERADO a INTENSO

**Momentos Ideais:** Cultos de cura, oração por enfermos, restauração

**Categorias Relacionadas:** ESPIRITO_SANTO, GRATIDÃO, ESPERANCA

---

### **7. ESPÍRITO SANTO**
**Definição:** Hinos sobre o Espírito Santo, unção, avivamento e dons espirituais.

**Palavras-chave Identificadoras:**
- Espírito Santo, Consolador, Paracleto
- Unção, avivamento, fogo
- Dons, frutos, manifestações
- Poder, graça, presença
- Batismo, enchimento, plenitude

**Exemplos de Hinos:**
- Hino 1 - "Chuvas de Graça"
- Hino 5 - "Ó Desce Fogo Santo"
- Hinos sobre avivamento

**Intensidade Típica:** INTENSO a EXTÁTICO

**Momentos Ideais:** Avivamentos, cultos de unção, busca espiritual

**Categorias Relacionadas:** ADORACAO, PODER, SANTIDADE

---

### **8. VOLTA DE CRISTO**
**Definição:** Hinos sobre a volta de Cristo, escatologia e esperança futura.

**Palavras-chave Identificadoras:**
- Volta, vinda, retorno de Cristo
- Arrebatamento, ressurreição
- Céu, glória, eternidade
- Trombeta, nuvens, anjos
- Juízo, reino, milênio

**Exemplos de Hinos:**
- Hino 2 - "Saudosa Lembrança"
- Hinos sobre a volta de Cristo
- Hinos escatológicos

**Intensidade Típica:** CONTEMPLATIVO a INTENSO

**Momentos Ideais:** Estudos escatológicos, reflexão, esperança

**Categorias Relacionadas:** ESPERANCA, ADORACAO, REFLEXAO

---

### **9. ORAÇÃO**
**Definição:** Hinos que são orações, súplicas, intercessões e comunhão com Deus.

**Palavras-chave Identificadoras:**
- Oração, súplica, petição
- Intercessão, clamor, rogo
- Comunhão, intimidade, presença
- Aba, Pai, Senhor
- Escuta, resposta, diálogo

**Exemplos de Hinos:**
- Hinos de súplica
- Hinos de intercessão
- Hinos de comunhão

**Intensidade Típica:** CONTEMPLATIVO a MODERADO

**Momentos Ideais:** Momentos de oração, intercessão, comunhão

**Categorias Relacionadas:** REFLEXAO, INTIMIDADE, COMUNHAO

---

### **10. COMUNHÃO**
**Definição:** Hinos sobre comunhão cristã, igreja, unidade e relacionamentos.

**Palavras-chave Identificadoras:**
- Comunhão, unidade, igreja
- Irmãos, família, corpo
- Amor, fraternidade, amizade
- Serviço, ministério, dons
- Corpo de Cristo, templo

**Exemplos de Hinos:**
- Hinos sobre comunhão
- Hinos sobre igreja
- Hinos sobre unidade

**Intensidade Típica:** MODERADO

**Momentos Ideais:** Cultos de comunhão, conferências, eventos da igreja

**Categorias Relacionadas:** AMOR, SERVICO, UNIDADE

---

### **11. GRATIDÃO**
**Definição:** Hinos de agradecimento, ação de graças e reconhecimento das bênçãos de Deus.

**Palavras-chave Identificadoras:**
- Gratidão, agradecimento, obrigado
- Bênção, favor, bondade
- Reconhecimento, louvor, honra
- Ação de graças, celebração
- Abundância, provisão, cuidado

**Intensidade Típica:** MODERADO a CELEBRATIVO

**Momentos Ideais:** Ações de graças, celebrações, momentos de bênção

**Categorias Relacionadas:** ADORACAO, CELEBRACAO, ESPERANCA

---

### **12. ESPERANÇA**
**Definição:** Hinos sobre esperança, promessas de Deus e futuro abençoado.

**Palavras-chave Identificadoras:**
- Esperança, promessa, futuro
- Confiança, fé, certeza
- Amanhã, porvir, eternidade
- Glória, reino, vitória
- Transformação, renovação

**Intensidade Típica:** CONTEMPLATIVO a MODERADO

**Momentos Ideais:** Momentos de dúvida, reflexão, encorajamento

**Categorias Relacionadas:** CONSOLO, FORCA, VOLTA_DE_CRISTO

---

### **13. FORÇA**
**Definição:** Hinos sobre força, coragem, determinação e poder para vencer.

**Palavras-chave Identificadoras:**
- Força, poder, coragem
- Determinação, resistência, firmeza
- Vitória, conquista, triunfo
- Fortaleza, escudo, proteção
- Batalha, luta, guerra

**Intensidade Típica:** MODERADO a INTENSO

**Momentos Ideais:** Momentos de fraqueza, batalhas espirituais, encorajamento

**Categorias Relacionadas:** PROVACAO, ESPERANCA, SERVICO

---

### **14. AMOR**
**Definição:** Hinos sobre o amor de Deus, amor ao próximo e relacionamentos.

**Palavras-chave Identificadoras:**
- Amor, caridade, compaixão
- Coração, sentimento, afeto
- Relacionamento, união, vínculo
- Sacrifício, entrega, doação
- Perdão, misericórdia, graça

**Intensidade Típica:** CONTEMPLATIVO a MODERADO

**Momentos Ideais:** Momentos de relacionamento, perdão, compaixão

**Categorias Relacionadas:** COMUNHAO, CONSOLO, GRATIDÃO

---

### **15. SANTIDADE**
**Definição:** Hinos sobre santidade, pureza, separação e vida santa.

**Palavras-chave Identificadoras:**
- Santidade, pureza, limpeza
- Separação, consagração, dedicação
- Justiça, retidão, integridade
- Perfeição, excelência, nobreza
- Templo, altar, sacrifício

**Intensidade Típica:** CONTEMPLATIVO a SOLENE

**Momentos Ideais:** Momentos de consagração, reflexão sobre pureza

**Categorias Relacionadas:** ESPIRITO_SANTO, REFLEXAO, REVERENCIA

---

### **16. SERVIÇO**
**Definição:** Hinos sobre serviço cristão, ministério e trabalho para Deus.

**Palavras-chave Identificadoras:**
- Serviço, ministério, trabalho
- Chamado, vocação, missão
- Dedicação, entrega, sacrifício
- Obreiro, servo, trabalhador
- Colheita, semeadura, fruto

**Intensidade Típica:** MODERADO a INTENSO

**Momentos Ideais:** Cultos de consagração, conferências ministeriais

**Categorias Relacionadas:** MISSAO, TESTEMUNHO, COMUNHAO

---

### **17. TESTEMUNHO**
**Definição:** Hinos de testemunho pessoal, experiência com Deus e transformação.

**Palavras-chave Identificadoras:**
- Testemunho, experiência, história
- Transformação, mudança, conversão
- Vida, caminhada, jornada
- Milagre, bênção, livramento
- Antes, depois, agora

**Intensidade Típica:** MODERADO a INTENSO

**Momentos Ideais:** Momentos de testemunho, celebração de vitórias

**Categorias Relacionadas:** SALVACAO, GRATIDÃO, ESPERANCA

---

### **18. REFLEXÃO**
**Definição:** Hinos para meditação, contemplação e reflexão espiritual.

**Palavras-chave Identificadoras:**
- Reflexão, meditação, contemplação
- Silêncio, quietude, paz
- Profundidade, mistério, sabedoria
- Interior, alma, coração
- Busca, encontro, revelação

**Intensidade Típica:** CONTEMPLATIVO

**Momentos Ideais:** Devoção pessoal, meditação, estudo bíblico

**Categorias Relacionadas:** ORAÇÃO, SANTIDADE, ESPERANCA

---

### **19. CELEBRAÇÃO**
**Definição:** Hinos festivos, alegres e celebrativos para momentos especiais.

**Palavras-chave Identificadoras:**
- Celebração, festa, alegria
- Júbilo, exultação, regozijo
- Vitória, conquista, triunfo
- Festa, banquete, convite
- Dança, música, instrumentos

**Intensidade Típica:** CELEBRATIVO a EXTÁTICO

**Momentos Ideais:** Celebrações, festas, momentos de alegria

**Categorias Relacionadas:** GRATIDÃO, ADORACAO, ESPERANCA

---

### **20. REVERÊNCIA**
**Definição:** Hinos solenes, reverentes e dignos para momentos especiais.

**Palavras-chave Identificadoras:**
- Reverência, respeito, dignidade
- Solenidade, seriedade, gravidade
- Majestade, grandeza, poder
- Trono, reino, autoridade
- Silêncio, quietude, contemplação

**Intensidade Típica:** SOLENE a CONTEMPLATIVO

**Momentos Ideais:** Cerimônias especiais, momentos de reverência

**Categorias Relacionadas:** SANTIDADE, ADORACAO, REFLEXAO

---

## ⚡ **INTENSIDADE ESPIRITUAL**

### **CONTEMPLATIVO**
**Características:** Meditativo, introspectivo, silencioso, profundo
**Palavras-chave:** Meditação, quietude, silêncio, profundidade
**Exemplos:** Hinos de reflexão, meditação, contemplação
**Momentos:** Devoção pessoal, meditação, reflexão

### **MODERADO**
**Características:** Equilibrado, sereno, controlado, estável
**Palavras-chave:** Paz, serenidade, equilíbrio, estabilidade
**Exemplos:** Hinos de louvor cotidiano, comunhão
**Momentos:** Cultos regulares, uso diário, comunhão

### **INTENSO**
**Características:** Forte, apaixonado, fervoroso, determinado
**Palavras-chave:** Paixão, fervor, determinação, força
**Exemplos:** Hinos de avivamento, missão, conversão
**Momentos:** Avivamentos, apelos, momentos decisivos

### **EXTÁTICO**
**Características:** Exaltado, transcendente, jubiloso, exultante
**Palavras-chave:** Êxtase, júbilo, exultação, transcendência
**Exemplos:** Hinos de celebração, vitória, glória
**Momentos:** Celebrações, vitórias, momentos de glória

### **SOLENE**
**Características:** Reverente, respeitoso, digno, cerimonial
**Palavras-chave:** Reverência, dignidade, cerimônia, respeito
**Exemplos:** Hinos de reverência, cerimônias especiais
**Momentos:** Cerimônias, momentos de reverência, funerais

### **CELEBRATIVO**
**Características:** Festivo, alegre, jubiloso, comemorativo
**Palavras-chave:** Festa, alegria, júbilo, comemoração
**Exemplos:** Hinos de celebração, gratidão, festas
**Momentos:** Celebrações, festas, momentos de alegria

### **MEDITATIVO**
**Características:** Reflexivo, pensativo, contemplativo, silencioso
**Palavras-chave:** Reflexão, pensamento, contemplação, silêncio
**Exemplos:** Hinos de reflexão, estudo, contemplação
**Momentos:** Estudo bíblico, reflexão, contemplação

### **PROCLAMATÓRIO**
**Características:** Declarativo, anunciativo, proclamativo, assertivo
**Palavras-chave:** Declaração, anúncio, proclamação, assertividade
**Exemplos:** Hinos de declaração, anúncio, proclamação
**Momentos:** Proclamações, declarações, anúncios

### **INTERCESSÓRIO**
**Características:** Súplice, intercessivo, rogativo, petitorio
**Palavras-chave:** Súplica, intercessão, rogo, petição
**Exemplos:** Hinos de oração, intercessão, súplica
**Momentos:** Oração, intercessão, súplica

### **AÇÃO DE GRAÇAS**
**Características:** Grato, agradecido, reconhecido, bendito
**Palavras-chave:** Gratidão, agradecimento, reconhecimento, bênção
**Exemplos:** Hinos de gratidão, agradecimento, bênção
**Momentos:** Ações de graças, agradecimentos, celebrações

---

## 🕐 **MOMENTOS ESPIRITUAIS**

### **DEVOÇÃO MATINAL**
**Características:** Inspirador, energizante, motivador, renovador
**Palavras-chave:** Manhã, novo dia, inspiração, renovação
**Exemplos:** Hinos de novo dia, inspiração matinal
**Uso:** Devoção pessoal, início do dia, renovação

### **REFLEXÃO NOTURNA**
**Características:** Contemplativo, reflexivo, pacífico, sereno
**Palavras-chave:** Noite, reflexão, contemplação, paz
**Exemplos:** Hinos de reflexão, contemplação noturna
**Uso:** Reflexão do dia, contemplação, paz

### **CULTO DE ADORAÇÃO**
**Características:** Congregacional, comunitário, reverente, celebrativo
**Palavras-chave:** Culto, adoração, congregação, comunidade
**Exemplos:** Hinos de culto, adoração congregacional
**Uso:** Cultos regulares, adoração comunitária

### **ORAÇÃO PESSOAL**
**Características:** Íntimo, pessoal, sincero, profundo
**Palavras-chave:** Oração, intimidade, pessoal, profundo
**Exemplos:** Hinos de oração pessoal, intimidade
**Uso:** Oração pessoal, intimidade com Deus

### **CONSOLO EM PROVAÇÕES**
**Características:** Consolador, encorajador, esperançoso, fortalecedor
**Palavras-chave:** Consolo, encorajamento, esperança, força
**Exemplos:** Hinos de consolo, encorajamento
**Uso:** Momentos difíceis, provações, consolo

### **CELEBRAÇÃO**
**Características:** Festivo, jubiloso, alegre, comemorativo
**Palavras-chave:** Celebração, festa, júbilo, alegria
**Exemplos:** Hinos de celebração, festa, júbilo
**Uso:** Celebrações, festas, momentos alegres

### **CRESCIMENTO ESPIRITUAL**
**Características:** Educativo, inspirador, motivador, transformador
**Palavras-chave:** Crescimento, aprendizado, transformação, inspiração
**Exemplos:** Hinos de crescimento, aprendizado
**Uso:** Estudo, crescimento, transformação

### **MISSÕES**
**Características:** Missionário, evangelístico, chamativo, desafiador
**Palavras-chave:** Missão, evangelismo, chamado, desafio
**Exemplos:** Hinos de missão, evangelismo
**Uso:** Conferências missionárias, evangelismo

### **CERIMÔNIAS FÚNEBRES**
**Características:** Solene, respeitoso, consolador, esperançoso
**Palavras-chave:** Funeral, morte, consolo, esperança
**Exemplos:** Hinos fúnebres, consolo, esperança
**Uso:** Cerimônias fúnebres, momentos de luto

### **CERIMÔNIAS DE CASAMENTO**
**Características:** Romântico, celebrativo, abençoador, comemorativo
**Palavras-chave:** Casamento, amor, bênção, celebração
**Exemplos:** Hinos de casamento, amor, bênção
**Uso:** Cerimônias de casamento, celebrações

---

## 🤖 **PROMPT TÉCNICO PARA IA**

### **Prompt Principal**

```
Você é um especialista em análise de hinos cristãos e teologia. Sua tarefa é categorizar hinos da Harpa Cristã seguindo o sistema de categorização em português brasileiro.

ANÁLISE REQUERIDA:
1. Categorias (múltiplas, não exclusivas)
2. Intensidade Espiritual (1 obrigatória)
3. Momento Espiritual (1 obrigatório)
4. Confiança (0.0-1.0)

DADOS DO HINO:
- Número: [NÚMERO]
- Título: [TÍTULO]
- Versos: [VERSOS]
- Refrão: [REFRÃO]

CATEGORIAS DISPONÍVEIS (múltiplas por hino):
1. SALVACAO - Salvação e conversão
2. ADORACAO - Adoração e louvor
3. CONSOLO - Consolo e paz
4. MISSAO - Missão e evangelismo
5. PROVACAO - Provações e sofrimento
6. CURACAO - Cura e restauração
7. ESPIRITO_SANTO - Espírito Santo e unção
8. VOLTA_DE_CRISTO - Volta de Cristo
9. ORACAO - Oração e súplica
10. COMUNHAO - Comunhão e igreja
11. GRATIDAO - Gratidão e ação de graças
12. ESPERANCA - Esperança e promessas
13. FORCA - Força e coragem
14. AMOR - Amor e relacionamento
15. SANTIDADE - Santidade e pureza
16. SERVICO - Serviço e ministério
17. TESTEMUNHO - Testemunho e experiência
18. REFLEXAO - Reflexão e meditação
19. CELEBRACAO - Celebração e festa
20. REVERENCIA - Reverência e solenidade

INTENSIDADES DISPONÍVEIS:
- CONTEMPLATIVO, MODERADO, INTENSO, EXTÁTICO, SOLENE
- CELEBRATIVO, MEDITATIVO, PROCLAMATÓRIO, INTERCESSÓRIO, AÇÃO_DE_GRAÇAS

MOMENTOS DISPONÍVEIS:
- DEVOÇÃO_MATINAL, REFLEXÃO_NOTURNA, CULTO_DE_ADORAÇÃO, ORAÇÃO_PESSOAL
- CONSOLO_EM_PROVAÇÕES, CELEBRAÇÃO, CRESCIMENTO_ESPIRITUAL, MISSÕES
- CERIMÔNIAS_FÚNEBRES, CERIMÔNIAS_DE_CASAMENTO

FORMATO DE RESPOSTA (JSON):
{
  "categories": ["CATEGORIA1", "CATEGORIA2", "CATEGORIA3"],
  "intensity": "INTENSIDADE",
  "moment": "MOMENTO",
  "confidence": 0.95,
  "reasoning": "Explicação breve da categorização"
}

INSTRUÇÕES:
- Analise o conteúdo completo do hino (título, versos, refrão)
- Identifique palavras-chave, temas e sentimentos
- Considere o contexto teológico e prático
- Seja objetivo e baseado no conteúdo
- Use 1-5 categorias por hino (não exclusivas)
- Justifique sua categorização
- Categorias devem ser em português brasileiro
```

### **Prompt de Validação**

```
Valide a categorização do hino [NÚMERO] - [TÍTULO]:

CATEGORIZAÇÃO PROPOSTA:
- Categorias: [CATEGORIAS]
- Intensidade: [INTENSIDADE]
- Momento: [MOMENTO]
- Confiança: [CONFIANÇA]

ANÁLISE CRÍTICA:
1. As categorias são apropriadas e em português?
2. A intensidade corresponde ao tom do hino?
3. O momento é adequado para o uso?
4. A confiança é realista?
5. Há redundância ou categorias desnecessárias?

RESPOSTA (JSON):
{
  "valid": true/false,
  "issues": ["problema1", "problema2"],
  "suggestions": ["sugestão1", "sugestão2"],
  "confidence": 0.90
}
```

---

## 📝 **EXEMPLOS PRÁTICOS**

### **Exemplo 1: Hino 1 - "Chuvas de Graça"**

**Análise IA:**
```json
{
  "categories": ["ESPIRITO_SANTO", "ADORACAO", "ORACAO"],
  "intensity": "INTENSO",
  "moment": "DEVOÇÃO_MATINAL",
  "confidence": 0.95,
  "reasoning": "Hino focado no Espírito Santo, pedindo chuvas de graça e unção. Tom intenso de súplica por avivamento. Ideal para devoção matinal buscando renovação espiritual."
}
```

### **Exemplo 2: Hino 4 - "Deus Velará Por Ti"**

**Análise IA:**
```json
{
  "categories": ["CONSOLO", "ESPERANCA", "AMOR"],
  "intensity": "CONTEMPLATIVO",
  "moment": "CONSOLO_EM_PROVAÇÕES",
  "confidence": 0.98,
  "reasoning": "Hino de consolo e encorajamento, prometendo cuidado divino. Tom contemplativo e pacífico. Ideal para momentos de provação e necessidade de consolo."
}
```

### **Exemplo 3: Hino 15 - "Conversão"**

**Análise IA:**
```json
{
  "categories": ["SALVACAO", "TESTEMUNHO", "REFLEXAO"],
  "intensity": "INTENSO",
  "moment": "ORAÇÃO_PESSOAL",
  "confidence": 0.99,
  "reasoning": "Hino sobre conversão e transformação pessoal. Tom intenso de testemunho e arrependimento. Ideal para oração pessoal e momentos de conversão."
}
```

---

## 🔧 **IMPLEMENTAÇÃO TÉCNICA**

---

## 📝 **EXEMPLOS PRÁTICOS**

### **Exemplo 1: Hino 1 - "Chuvas de Graça"**

**Análise IA:**
```json
{
  "primaryCategory": "HOLY_SPIRIT",
  "tags": ["avivamento", "unção", "poder", "renovação"],
  "intensity": "INTENSE",
  "moment": "MORNING_DEVOTION",
  "confidence": 0.95,
  "reasoning": "Hino focado no Espírito Santo, pedindo chuvas de graça e unção. Tom intenso de súplica por avivamento. Ideal para devoção matinal buscando renovação espiritual."
}
```

### **Exemplo 2: Hino 4 - "Deus Velará Por Ti"**

**Análise IA:**
```json
{
  "primaryCategory": "COMFORT",
  "tags": ["consolo", "cuidado", "proteção", "paz"],
  "intensity": "CONTEMPLATIVE",
  "moment": "TRIAL_COMFORT",
  "confidence": 0.98,
  "reasoning": "Hino de consolo e encorajamento, prometendo cuidado divino. Tom contemplativo e pacífico. Ideal para momentos de provação e necessidade de consolo."
}
```

### **Exemplo 3: Hino 15 - "Conversão"**

**Análise IA:**
```json
{
  "primaryCategory": "SALVATION",
  "tags": ["conversão", "arrependimento", "transformação", "testemunho"],
  "intensity": "INTENSE",
  "moment": "PERSONAL_PRAYER",
  "confidence": 0.99,
  "reasoning": "Hino sobre conversão e transformação pessoal. Tom intenso de testemunho e arrependimento. Ideal para oração pessoal e momentos de conversão."
}
```

---

## 🔧 **IMPLEMENTAÇÃO TÉCNICA**

### **Modelo de Dados**

```kotlin
// Extensão do modelo Hymn
data class Hymn(
    val id: Int,
    val number: Int,
    val title: String,
    val verses: List<String>,
    val chorus: String? = null,
    val metadata: HymnMetadata? = null
)

// Metadados de categorização
data class HymnMetadata(
    val primaryCategory: PrimaryCategory,
    val tags: List<String>,
    val intensity: SpiritualIntensity,
    val moment: SpiritualMoment,
    val confidence: Float = 1.0f,
    val verified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// Enums para categorização
enum class PrimaryCategory {
    SALVATION, WORSHIP, COMFORT, MISSION, TRIAL,
    HEALING, HOLY_SPIRIT, SECOND_COMING, PRAYER, FELLOWSHIP
}

enum class SpiritualIntensity {
    CONTEMPLATIVE, MODERATE, INTENSE, ECSTATIC, SOLEMN,
    CELEBRATORY, MEDITATIVE, PROCLAMATORY, INTERCESSORY, THANKSGIVING
}

enum class SpiritualMoment {
    MORNING_DEVOTION, EVENING_REFLEXION, WORSHIP_SERVICE, PERSONAL_PRAYER,
    TRIAL_COMFORT, CELEBRATION, SPIRITUAL_GROWTH, MISSIONS, FUNERAL, WEDDING
}
```

### **Serviço de Categorização**

```kotlin
interface HymnCategorizationService {
    // Categorização automática
    suspend fun categorizeHymn(hymn: Hymn): HymnMetadata
    
    // Busca por categoria
    fun getHymnsByCategory(category: PrimaryCategory): List<Hymn>
    
    // Busca por tags
    fun searchHymnsByTags(tags: List<String>): List<Hymn>
    
    // Busca por intensidade
    fun getHymnsByIntensity(intensity: SpiritualIntensity): List<Hymn>
    
    // Busca por momento
    fun getHymnsByMoment(moment: SpiritualMoment): List<Hymn>
    
    // Busca híbrida
    fun searchHymns(
        category: PrimaryCategory? = null,
        tags: List<String> = emptyList(),
        intensity: SpiritualIntensity? = null,
        moment: SpiritualMoment? = null
    ): List<Hymn>
    
    // Validação manual
    fun validateCategorization(hymnId: Int, metadata: HymnMetadata): Boolean
    
    // Estatísticas
    fun getCategorizationStats(): CategorizationStats
}

data class CategorizationStats(
    val totalHymns: Int,
    val categorizedHymns: Int,
    val verifiedHymns: Int,
    val categoryDistribution: Map<PrimaryCategory, Int>,
    val tagFrequency: Map<String, Int>
)
```

### **Repositório de Metadados**

```kotlin
interface HymnMetadataRepository {
    suspend fun saveMetadata(hymnId: Int, metadata: HymnMetadata)
    suspend fun getMetadata(hymnId: Int): HymnMetadata?
    suspend fun getAllMetadata(): Map<Int, HymnMetadata>
    suspend fun updateMetadata(hymnId: Int, metadata: HymnMetadata)
    suspend fun deleteMetadata(hymnId: Int)
    
    // Buscas otimizadas
    suspend fun getHymnsByCategory(category: PrimaryCategory): List<Int>
    suspend fun getHymnsByTags(tags: List<String>): List<Int>
    suspend fun getHymnsByIntensity(intensity: SpiritualIntensity): List<Int>
    suspend fun getHymnsByMoment(moment: SpiritualMoment): List<Int>
}
```

---

## ✅ **VALIDAÇÃO E QUALIDADE**

### **Critérios de Validação**

#### **1. Precisão da Categoria Primária**
- ✅ Categoria corresponde ao tema principal
- ✅ Não há ambiguidade entre categorias
- ✅ Baseada em conteúdo teológico sólido

#### **2. Relevância das Tags**
- ✅ Tags são específicas e úteis
- ✅ Não redundantes com categoria primária
- ✅ Máximo 5 tags por hino
- ✅ Tags em português, minúsculas, com hífens

#### **3. Adequação da Intensidade**
- ✅ Intensidade corresponde ao tom emocional
- ✅ Consistente com o conteúdo
- ✅ Útil para seleção por momento

#### **4. Apropriação do Momento**
- ✅ Momento é prático e aplicável
- ✅ Baseado em uso real
- ✅ Não contradiz a intensidade

#### **5. Realismo da Confiança**
- ✅ Confiança reflete a certeza da categorização
- ✅ Valores entre 0.0 e 1.0
- ✅ Consistente com a qualidade da análise

### **Processo de Validação**

#### **Fase 1: Validação Automática**
```kotlin
fun validateCategorization(metadata: HymnMetadata): ValidationResult {
    val issues = mutableListOf<String>()
    
    // Validar categoria primária
    if (metadata.primaryCategory == null) {
        issues.add("Categoria primária obrigatória")
    }
    
    // Validar tags
    if (metadata.tags.size > 5) {
        issues.add("Máximo 5 tags permitidas")
    }
    
    // Validar intensidade
    if (metadata.intensity == null) {
        issues.add("Intensidade obrigatória")
    }
    
    // Validar momento
    if (metadata.moment == null) {
        issues.add("Momento obrigatório")
    }
    
    // Validar confiança
    if (metadata.confidence < 0.0f || metadata.confidence > 1.0f) {
        issues.add("Confiança deve estar entre 0.0 e 1.0")
    }
    
    return ValidationResult(
        isValid = issues.isEmpty(),
        issues = issues
    )
}
```

#### **Fase 2: Validação Manual**
- Revisão por especialistas em teologia
- Validação por pastores e líderes
- Feedback da comunidade
- Ajustes baseados em uso real

#### **Fase 3: Validação Contínua**
- Monitoramento de uso
- Análise de feedback
- Ajustes baseados em dados
- Melhoria contínua

### **Métricas de Qualidade**

```kotlin
data class QualityMetrics(
    val accuracy: Float,           // Precisão da categorização
    val consistency: Float,        // Consistência entre categorizadores
    val completeness: Float,       // Completude dos metadados
    val usefulness: Float,         // Utilidade para usuários
    val maintainability: Float     // Facilidade de manutenção
)
```

---

## 🚀 **ROADMAP DE IMPLEMENTAÇÃO**

### **Fase 1: Fundação (Mês 1-2)**
- [ ] Implementar modelo de dados
- [ ] Criar serviço de categorização
- [ ] Desenvolver interface de validação
- [ ] Configurar repositório de metadados

### **Fase 2: Categorização Automática (Mês 3-4)**
- [ ] Integrar IA para categorização
- [ ] Implementar validação automática
- [ ] Criar sistema de confiança
- [ ] Desenvolver interface de revisão

### **Fase 3: Validação Manual (Mês 5-6)**
- [ ] Recrutar especialistas para validação
- [ ] Implementar sistema de feedback
- [ ] Criar métricas de qualidade
- [ ] Desenvolver processo de aprovação

### **Fase 4: Integração (Mês 7-8)**
- [ ] Integrar com sistema de busca
- [ ] Implementar navegação por categorias
- [ ] Criar trilhas temáticas
- [ ] Desenvolver recomendações

### **Fase 5: Otimização (Mês 9-12)**
- [ ] Monitorar uso e feedback
- [ ] Otimizar algoritmos de busca
- [ ] Refinar categorização
- [ ] Expandir funcionalidades

---

## 📊 **MÉTRICAS DE SUCESSO**

### **Métricas Técnicas**
- **Precisão da Categorização**: > 90%
- **Consistência Inter-avaliadores**: > 85%
- **Tempo de Categorização**: < 30 segundos por hino
- **Cobertura**: 100% dos hinos categorizados

### **Métricas de Usuário**
- **Descoberta**: +50% de hinos descobertos
- **Engajamento**: +30% de tempo no app
- **Satisfação**: > 4.5/5.0
- **Retenção**: +25% de usuários ativos

### **Métricas de Negócio**
- **Adoção**: 80% dos usuários usam categorias
- **Retenção**: +20% de retenção mensal
- **Recomendações**: +40% de recomendações
- **Crescimento**: +35% de downloads

---

## 🔄 **MANUTENÇÃO E EVOLUÇÃO**

### **Manutenção Contínua**
- Monitoramento de qualidade
- Atualização de categorias
- Refinamento de tags
- Otimização de busca

### **Evolução do Sistema**
- Novas categorias baseadas em uso
- Tags dinâmicas baseadas em tendências
- Intensidades adaptativas
- Momentos contextuais

### **Expansão Futura**
- Categorização multilíngue
- Análise de sentimento avançada
- Recomendações personalizadas
- Integração com outras plataformas

---

## 📋 **Documentos Relacionados**

Para implementação técnica detalhada, consulte:
- **`Proposta_Implementacao_Categorizacao_Hinos.md`**: Proposta de implementação com arquitetura, cronograma e ferramentas de desenvolvimento

---

**Este documento serve como base técnica completa para implementação do sistema de categorização híbrida de hinos no Louve App, fornecendo todas as diretrizes necessárias para categorização automática por IA e validação manual por especialistas.**
