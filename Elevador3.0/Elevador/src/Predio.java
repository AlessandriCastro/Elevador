import java.io.Serializable;

public class Predio extends EntidadeSimulavel implements Serializable {
    private static final long serialVersionUID = 1L;
    private CentralDeControle central;
    private Lista<Andar> andares;
    private GeradorPessoas geradorPessoas;

    public Predio(int quantidadeAndares, int quantidadeElevadores) {
        andares = new Lista<>();
        String tipoPainel = SeletorPainelExterno.selecionarPainel();
        System.out.printf("\n✅ Painel externo '%s' selecionado para todos os andares.\n", tipoPainel);

        for (int i = 0; i < quantidadeAndares; i++) {
            andares.inserirFim(new Andar(i, tipoPainel));
        }
        int andarMaximo = quantidadeAndares - 1;
        central = new CentralDeControle(quantidadeElevadores, andarMaximo, this);
        geradorPessoas = new GeradorPessoas(this, andarMaximo);
    }

    private void mostrarEstadoAtual(int minutoSimulado) {
        // Calcula hora e minutos
        int hora = (minutoSimulado / 60) % 24;
        int minutos = minutoSimulado % 60;
        
        // Verifica se é horário de pico
        boolean ehHorarioPico = hora == 8 || hora == 9 || hora == 12 || hora == 13 || hora == 17 || hora == 18;
        String statusPico = ehHorarioPico ? "🚨 HORÁRIO DE PICO" : "⏰ HORÁRIO NORMAL";
        
        System.out.println("\n=== " + statusPico + " | " + String.format("%02d:%02d", hora, minutos) + " ===");
        
        // Mostra status dos elevadores
        Lista<Elevador> elevadores = central.getElevadores();
        Ponteiro<Elevador> pElev = elevadores.getInicio();
        while (pElev != null) {
            Elevador elevador = pElev.getElemento();
            System.out.println("----------------------------------------");
            System.out.printf("⏰ Horário: %02d:%02d | 🛗 Elevador %d\n", hora, minutos, elevador.getId());
            System.out.printf("📍 Andar Atual: %d\n", elevador.getAndarAtual());
            
            // Mostra pessoas dentro do elevador
            Fila<Pessoa> pessoasDentro = elevador.getPessoasDentro();
            if (pessoasDentro.tamanho() > 0) {
                System.out.print("👥 Pessoas no elevador: ");
                int tamanho = pessoasDentro.tamanho();
                for (int i = 0; i < tamanho; i++) {
                    Pessoa p = pessoasDentro.dequeue();
                    String emoji = p.getTipoPrioridade() == 2 ? "♿" : 
                                  p.getTipoPrioridade() == 1 ? "👴" : "👤";
                    System.out.printf("%s(ID:%d) ", emoji, p.getId());
                    pessoasDentro.enqueue(p);
                }
                System.out.println();
            } else {
                System.out.println("👥 Pessoas no elevador: Vazio");
            }
            
            System.out.printf("🎯 Destinos: %s\n", 
                elevador.getDestinos().estaVazia() ? "Nenhum" : elevador.getDestinos().toString());
            System.out.println("----------------------------------------");
            pElev = pElev.getProximo();
        }
        
        // Mostra estado do prédio
        System.out.println("=== ESTADO DO PRÉDIO ===");
        Ponteiro<Andar> pAndar = andares.getInicio();
        while (pAndar != null) {
            Andar andar = pAndar.getElemento();
            StringBuilder linha = new StringBuilder();
            linha.append(String.format("Andar %d: ", andar.getNumero()));
            
            // Mostra status do painel externo
            PainelExterno painel = andar.getPainelExterno();
            linha.append(String.format("[%s: %s] ", painel.getTipoPainel(), painel.getStatus()));
            
            FilaPrior pessoasAguardando = andar.getPessoasAguardando();
            if (pessoasAguardando.tamanho() > 0) {
                linha.append(String.format("👥 Aguardando (%d): ", pessoasAguardando.tamanho()));
                // Mostra cadeirantes (prioridade 2)
                if (pessoasAguardando.temElementosNaPrioridade(2)) {
                    linha.append("♿ ");
                }
                // Mostra idosos (prioridade 1)
                if (pessoasAguardando.temElementosNaPrioridade(1)) {
                    linha.append("👴 ");
                }
                // Mostra pessoas normais (prioridade 0)
                if (pessoasAguardando.temElementosNaPrioridade(0)) {
                    linha.append("👤 ");
                }
            }
            
            // Marca elevadores neste andar
            pElev = elevadores.getInicio();
            while (pElev != null) {
                if (pElev.getElemento().getAndarAtual() == andar.getNumero()) {
                    linha.append(" 🛗");
                }
                pElev = pElev.getProximo();
            }
            
            System.out.println(linha.toString());
            pAndar = pAndar.getProximo();
        }
        System.out.println("=======================");
    }

    public Lista<Andar> getAndares() {
        return andares;
    }
    public Andar getAndar(int numero) {
        Ponteiro<Andar> atual = andares.getInicio();
        while (atual != null) {
            Andar andar = atual.getElemento();
            if (andar.getNumero() == numero) {
                return andar;
            }
            atual = atual.getProximo();
        }
        return null; // Se não encontrar
    }
    
    @Override
    public void atualizar(int minutoSimulado) {
        try {
            central.atualizar(minutoSimulado);
            geradorPessoas.atualizar(minutoSimulado);
            mostrarEstadoAtual(minutoSimulado);
            
            // Mostra estatísticas a cada hora
            if (minutoSimulado % 60 == 0) {
                geradorPessoas.mostrarEstatisticas();
            }
        } catch (Exception e) {
            System.err.println("Erro crítico na atualização do prédio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public CentralDeControle getCentral() {
        return central;
    }
}

