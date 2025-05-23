import java.util.Random;

public class GeradorPessoas extends EntidadeSimulavel {
    private Predio predio;
    private Random random;
    private int ultimoId;
    private int andarMaximo;
    
    // Configurações de geração
    private static final double PROBABILIDADE_PADRAO = 0.2;  // 20% de chance fora do pico
    private static final double PROBABILIDADE_PICO = 0.8;    // 80% de chance no pico
    private static final int MAX_PESSOAS_PICO = 3;           // Máximo de pessoas geradas por vez no pico
    private static final int MAX_PESSOAS_NORMAL = 1;         // Máximo de pessoas geradas por vez fora do pico
    
    // Probabilidades de tipos de pessoas
    private static final double PROB_CADEIRANTE = 0.05;      // 5% de chance
    private static final double PROB_IDOSO = 0.15;           // 15% de chance
    private static final double PROB_NORMAL = 0.80;          // 80% de chance
    
    // Horários de pico
    private Lista<Integer> horariosPico;
    
    // Métricas
    private int totalPessoasGeradas;
    private int pessoasGeradasHorarioPico;
    private int pessoasGeradasHorarioNormal;
    private int cadeirantesGerados;
    private int idososGerados;
    private int normaisGerados;

    public GeradorPessoas(Predio predio, int andarMaximo) {
        this.predio = predio;
        this.random = new Random();
        this.ultimoId = 0;
        this.andarMaximo = andarMaximo;
        
        // Inicializa métricas
        this.totalPessoasGeradas = 0;
        this.pessoasGeradasHorarioPico = 0;
        this.pessoasGeradasHorarioNormal = 0;
        this.cadeirantesGerados = 0;
        this.idososGerados = 0;
        this.normaisGerados = 0;
        
        // Configura horários de pico
        this.horariosPico = new Lista<>();
        this.horariosPico.inserirFim(8);   // 8h
        this.horariosPico.inserirFim(9);   // 9h
        this.horariosPico.inserirFim(12);  // 12h
        this.horariosPico.inserirFim(13);  // 13h
        this.horariosPico.inserirFim(17);  // 17h
        this.horariosPico.inserirFim(18);  // 18h
    }

    private boolean ehHorarioDePico(int minutoSimulado) {
        int hora = (minutoSimulado / 60) % 24;
        Ponteiro<Integer> p = horariosPico.getInicio();
        while (p != null) {
            if (p.getElemento() == hora) {
                return true;
            }
            p = p.getProximo();
        }
        return false;
    }

    private double getProbabilidadeGeracao(int minutoSimulado) {
        return ehHorarioDePico(minutoSimulado) ? PROBABILIDADE_PICO : PROBABILIDADE_PADRAO;
    }

    private int getMaxPessoasPorVez(int minutoSimulado) {
        return ehHorarioDePico(minutoSimulado) ? MAX_PESSOAS_PICO : MAX_PESSOAS_NORMAL;
    }

    private int gerarTipoPrioridade() {
        double tipo = random.nextDouble();
        if (tipo < PROB_CADEIRANTE) {
            cadeirantesGerados++;
            return 2; // Cadeirante
        } else if (tipo < PROB_CADEIRANTE + PROB_IDOSO) {
            idososGerados++;
            return 1; // Idoso
        } else {
            normaisGerados++;
            return 0; // Normal
        }
    }

    private Pessoa gerarPessoaAleatoria() {
        ultimoId++;
        
        // Gera andar de origem (0 até andarMaximo-1)
        int andarOrigem = random.nextInt(andarMaximo);
        
        // Gera andar de destino (0 até andarMaximo)
        int andarDestino;
        do {
            andarDestino = random.nextInt(andarMaximo + 1);
        } while (andarDestino == andarOrigem);

        // Gera tipo de prioridade
        int tipoPrioridade = gerarTipoPrioridade();

        // Cria a pessoa com todos os atributos
        Pessoa novaPessoa = new Pessoa(ultimoId, andarOrigem, andarDestino, tipoPrioridade);
        
        // Atualiza métricas
        totalPessoasGeradas++;
        if (ehHorarioDePico(0)) { // Usa 0 como referência para o horário atual
            pessoasGeradasHorarioPico++;
        } else {
            pessoasGeradasHorarioNormal++;
        }

        return novaPessoa;
    }

    @Override
    public void atualizar(int minutoSimulado) {
        try {
            double probabilidade = getProbabilidadeGeracao(minutoSimulado);
            if (random.nextDouble() < probabilidade) {
                int maxPessoas = getMaxPessoasPorVez(minutoSimulado);
                int numPessoas = random.nextInt(maxPessoas) + 1; // Gera entre 1 e maxPessoas
                
                for (int i = 0; i < numPessoas; i++) {
                    Pessoa novaPessoa = gerarPessoaAleatoria();
                    Andar andar = predio.getAndar(novaPessoa.getAndarOrigem());
                    
                    if (andar != null) {
                        andar.adicionarPessoa(
                            novaPessoa.getId(),
                            novaPessoa.getAndarOrigem(),
                            novaPessoa.getAndarDestino(),
                            novaPessoa.getTipoPrioridade()
                        );
                        
                        String tipoPessoa = novaPessoa.getTipoPrioridade() == 2 ? "Cadeirante" :
                                          novaPessoa.getTipoPrioridade() == 1 ? "Idoso" : "Normal";
                        
                        int hora = (minutoSimulado / 60) % 24;
                        int minutos = minutoSimulado % 60;
                        boolean ehPico = ehHorarioDePico(minutoSimulado);
                        
                        System.out.printf("👤 Nova pessoa gerada - ID: %d | Tipo: %s | Origem: %d | Destino: %d | Horário: %02d:%02d | %s\n",
                            novaPessoa.getId(),
                            tipoPessoa,
                            novaPessoa.getAndarOrigem(),
                            novaPessoa.getAndarDestino(),
                            hora,
                            minutos,
                            ehPico ? "🚨 HORÁRIO DE PICO" : "⏰ HORÁRIO NORMAL");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao gerar pessoa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Métodos para acessar métricas
    public int getTotalPessoasGeradas() {
        return totalPessoasGeradas;
    }

    public int getPessoasGeradasHorarioPico() {
        return pessoasGeradasHorarioPico;
    }

    public int getPessoasGeradasHorarioNormal() {
        return pessoasGeradasHorarioNormal;
    }

    public int getCadeirantesGerados() {
        return cadeirantesGerados;
    }

    public int getIdososGerados() {
        return idososGerados;
    }

    public int getNormaisGerados() {
        return normaisGerados;
    }

    public void mostrarEstatisticas() {
        System.out.println("\n📊 ESTATÍSTICAS DO GERADOR DE PESSOAS 📊");
        System.out.println("----------------------------------------");
        System.out.printf("Total de pessoas geradas: %d\n", totalPessoasGeradas);
        System.out.printf("  - Horário de pico: %d\n", pessoasGeradasHorarioPico);
        System.out.printf("  - Horário normal: %d\n", pessoasGeradasHorarioNormal);
        System.out.println("\nDistribuição por tipo:");
        System.out.printf("  - Cadeirantes: %d (%.1f%%)\n", 
            cadeirantesGerados, 
            totalPessoasGeradas > 0 ? (double)cadeirantesGerados/totalPessoasGeradas * 100 : 0);
        System.out.printf("  - Idosos: %d (%.1f%%)\n", 
            idososGerados, 
            totalPessoasGeradas > 0 ? (double)idososGerados/totalPessoasGeradas * 100 : 0);
        System.out.printf("  - Normais: %d (%.1f%%)\n", 
            normaisGerados, 
            totalPessoasGeradas > 0 ? (double)normaisGerados/totalPessoasGeradas * 100 : 0);
        System.out.println("----------------------------------------\n");
    }
} 