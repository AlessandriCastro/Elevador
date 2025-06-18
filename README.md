# Simulador de Elevador - Interface Gráfica

## Descrição

Este projeto implementa um simulador de elevador com uma interface gráfica moderna que permite visualizar em tempo real toda a lógica do sistema de controle de elevadores.

## Funcionalidades da Interface Gráfica

### 🎮 Controles
- **▶ Iniciar**: Inicia a simulação
- **⏸ Pausar**: Pausa a simulação em andamento
- **⏯ Continuar**: Retoma a simulação pausada
- **⏹ Parar**: Para completamente a simulação

### 🏢 Visualização do Prédio
- Representação visual dos andares
- Posicionamento em tempo real dos elevadores
- Indicadores de status dos painéis externos
- Visualização das pessoas aguardando em cada andar
- Legenda com tipos de pessoas (cadeirantes, idosos, normais)

### 📊 Painéis Informativos
- **Status dos Elevadores**: Mostra posição, ocupação e destinos de cada elevador
- **Estado dos Andares**: Exibe pessoas aguardando e status dos painéis
- **Log de Eventos**: Registra todas as ações em tempo real
- **Estatísticas**: Contadores de pessoas transportadas

### 🎯 Características da Simulação
- **5 andares** e **2 elevadores**
- **Sistema de prioridades**: Cadeirantes (prioridade 2), Idosos (prioridade 1), Pessoas normais (prioridade 0)
- **Diferentes tipos de painéis**: Único, Numérico, Subir/Descer
- **Algoritmo inteligente** de distribuição de chamadas
- **Simulação de 24 horas** (1440 minutos)

## Como Executar

1. Compile todos os arquivos Java:
   ```bash
   javac *.java
   ```

2. Execute o programa:
   ```bash
   java Main
   ```

## Estrutura dos Arquivos

### Arquivos Principais da Interface
- `Main.java` - Ponto de entrada do programa
- `SimuladorGUI.java` - Interface gráfica principal
- `VisualizacaoPredio.java` - Componente visual do prédio

### Arquivos do Sistema
- `Simulador.java` - Controla a simulação
- `Predio.java` - Gerencia andares e elevadores
- `Elevador.java` - Lógica de cada elevador
- `CentralDeControle.java` - Coordena os elevadores
- `Heuristica.java` - Algoritmo de distribuição de chamadas
- `Andar.java` - Representa cada andar
- `Pessoa.java` - Representa as pessoas
- `GeradorPessoas.java` - Gera pessoas para a simulação

## Legenda Visual

- **🛗**: Elevador
- **♿**: Cadeirantes (prioridade alta)
- **👴**: Idosos (prioridade média)
- **👤**: Pessoas normais
- **🔴**: Painel ativo
- **🟢**: Painel inativo
- **🟢**: Elevador em movimento

## Funcionalidades Avançadas

### Sistema de Prioridades
O simulador implementa um sistema de prioridades que garante que:
1. Cadeirantes sejam atendidos primeiro
2. Idosos tenham prioridade sobre pessoas normais
3. A distribuição seja eficiente entre os elevadores

### Algoritmo Inteligente
A heurística considera:
- Distância entre elevador e andar solicitado
- Direção de movimento do elevador
- Capacidade atual do elevador
- Chamadas pendentes
- Proximidade entre elevadores

### Painéis Externos
Diferentes tipos de painéis simulam cenários reais:
- **Painel Único**: Botão simples de chamada
- **Painel Numérico**: Botões para cada andar
- **Painel Subir/Descer**: Botões direcionais

## Observações

- A simulação roda por 24 horas completas (1440 minutos)
- Cada ciclo da simulação representa 1 minuto real
- A velocidade pode ser ajustada no construtor do `SimuladorGUI`
- Todas as estatísticas são atualizadas em tempo real
- O log registra todos os eventos importantes do sistema 