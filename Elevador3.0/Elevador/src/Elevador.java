public class Elevador extends EntidadeSimulavel {
    private int id;
    private int andarAtual = 0;
    private int capacidadeMaxima;
    private Fila<Pessoa> pessoasDentro;
    private boolean subindo = true;
    private int andarMaximo;
    private Lista<Integer> destinos = new Lista<>();
    private Predio predio;
    // Métricas
    private int totalPessoasTransportadas;
    private int pessoasTransportadasHorarioPico;
    private int pessoasTransportadasHorarioNormal;

    public Elevador(int id, int capacidadeMaxima, int andarMaximo, Predio predio) {
        this.id = id;
        this.capacidadeMaxima = capacidadeMaxima;
        this.andarMaximo = andarMaximo;
        this.pessoasDentro = new Fila<>();
        this.predio = predio;
        this.totalPessoasTransportadas = 0;
        this.pessoasTransportadasHorarioPico = 0;
        this.pessoasTransportadasHorarioNormal = 0;
    }

    public boolean temEspaco() {
        return pessoasDentro.tamanho() < capacidadeMaxima;
    }

    public void embarcar(Pessoa p, int minutoAtual) {
        if (temEspaco()) {
            pessoasDentro.enqueue(p);
            p.entrarElevador(minutoAtual);
            System.out.println("DEBUG: Pessoa " + p.getId() + " embarcou no elevador " + id);
        }
    }

    public void embarcarPessoasNoAndarAtual(Andar andar, int minutoAtual) {
        FilaPrior filaDePessoaAguardando = andar.getPessoasAguardando();

        // Se não há ninguém esperando ou elevador está cheio, retorna
        if (filaDePessoaAguardando.tamanho() == 0 || !temEspaco()) {
            System.out.printf("DEBUG: Elevador %d não pode embarcar - Fila vazia ou cheio\n", id);
            return;
        }

        System.out.printf("🚪 Elevador %d no andar %d - Tentando embarcar %d pessoas\n", 
            id, andarAtual, filaDePessoaAguardando.tamanho());

        int pessoasEmbarcadas = 0;
        int espacoDisponivel = capacidadeMaxima - pessoasDentro.tamanho();

        // Primeiro embarca cadeirantes (prioridade 2)
        while (temEspaco() && filaDePessoaAguardando.temElementosNaPrioridade(2) && pessoasEmbarcadas < espacoDisponivel) {
            try {
                int idPessoa = filaDePessoaAguardando.dequeue(2);
                Pessoa p = andar.getPessoaPorId(idPessoa);
                if (p != null) {
                    embarcar(p, minutoAtual);
                    adicionarDestino(p.getAndarDestino());
                    andar.removerPessoa(idPessoa);
                    pessoasEmbarcadas++;
                    System.out.printf("♿ Pessoa %d (cadeirante) embarcou no elevador %d\n", idPessoa, id);
                } else {
                    System.out.printf("⚠️ Pessoa %d não encontrada no andar %d, pulando...\n", idPessoa, andarAtual);
                }
            } catch (Exception e) {
                System.err.println("Erro ao embarcar cadeirante: " + e.getMessage());
                break;
            }
        }
        
        // Depois embarca idosos (prioridade 1)
        while (temEspaco() && filaDePessoaAguardando.temElementosNaPrioridade(1) && pessoasEmbarcadas < espacoDisponivel) {
            try {
                int idPessoa = filaDePessoaAguardando.dequeue(1);
                Pessoa p = andar.getPessoaPorId(idPessoa);
                if (p != null) {
                    embarcar(p, minutoAtual);
                    adicionarDestino(p.getAndarDestino());
                    andar.removerPessoa(idPessoa);
                    pessoasEmbarcadas++;
                    System.out.printf("👴 Pessoa %d (idoso) embarcou no elevador %d\n", idPessoa, id);
                } else {
                    System.out.printf("⚠️ Pessoa %d não encontrada no andar %d, pulando...\n", idPessoa, andarAtual);
                }
            } catch (Exception e) {
                System.err.println("Erro ao embarcar idoso: " + e.getMessage());
                break;
            }
        }
        
        // Por fim, embarca pessoas normais (prioridade 0)
        while (temEspaco() && filaDePessoaAguardando.temElementosNaPrioridade(0) && pessoasEmbarcadas < espacoDisponivel) {
            try {
                int idPessoa = filaDePessoaAguardando.dequeue(0);
                Pessoa p = andar.getPessoaPorId(idPessoa);
                if (p != null) {
                    embarcar(p, minutoAtual);
                    adicionarDestino(p.getAndarDestino());
                    andar.removerPessoa(idPessoa);
                    pessoasEmbarcadas++;
                    System.out.printf("👤 Pessoa %d (normal) embarcou no elevador %d\n", idPessoa, id);
                } else {
                    System.out.printf("⚠️ Pessoa %d não encontrada no andar %d, pulando...\n", idPessoa, andarAtual);
                }
            } catch (Exception e) {
                System.err.println("Erro ao embarcar pessoa normal: " + e.getMessage());
                break;
            }
        }

        if (pessoasEmbarcadas > 0) {
            System.out.printf("✅ Elevador %d: %d pessoas embarcaram no andar %d\n", 
                id, pessoasEmbarcadas, andarAtual);
        } else {
            System.out.printf("⚠️ Elevador %d: Nenhuma pessoa embarcou no andar %d\n", 
                id, andarAtual);
        }
    }

    private void processarEmbarque(int minutoAtual) {
        Andar andar = predio.getAndar(andarAtual);
        if (andar != null) {
            embarcarPessoasNoAndarAtual(andar, minutoAtual);
        }
    }

    public void movimentarElevadorInteligente() {
        if (destinos.estaVazia()) {
            // Se não tem destinos, procura por chamadas ativas
            procurarChamadasAtivas();
            return;
        }

        int proximoDestino = destinos.primeiroElemento();
        
        // Se já está no destino, remove o destino e procura o próximo
        if (andarAtual == proximoDestino) {
            destinos.removerValor(andarAtual);
            return;
        }

        // Verifica se deve parar no andar atual
        if (devePararNoAndarAtual()) {
            // Se parou para embarque/desembarque, tenta processar
            processarEmbarqueDesembarque();
            return;
        }

        // Move o elevador
        if (andarAtual < proximoDestino) {
            andarAtual++;
            subindo = true;
            System.out.printf("⬆️ Elevador %d subindo para andar %d\n", id, andarAtual);
        } else if (andarAtual > proximoDestino) {
            andarAtual--;
            subindo = false;
            System.out.printf("⬇️ Elevador %d descendo para andar %d\n", id, andarAtual);
        }
    }

    private void processarEmbarqueDesembarque() {
        // Primeiro desembarca pessoas
        desembarcarNoAndar(andarAtual, 0); // Usa 0 como referência de tempo
        
        // Depois tenta embarcar pessoas
        Andar andar = predio.getAndar(andarAtual);
        if (andar != null && temEspaco()) {
            System.out.printf("DEBUG: Elevador %d tentando embarcar pessoas no andar %d\n", id, andarAtual);
            System.out.printf("DEBUG: Pessoas aguardando: %d\n", andar.getPessoasAguardando().tamanho());
            embarcarPessoasNoAndarAtual(andar, 0);
        }
    }

    private boolean devePararNoAndarAtual() {
        // Verifica se tem alguém para desembarcar
        if (temAlguemComDestino(andarAtual)) {
            System.out.printf("🛑 Elevador %d parando no andar %d para desembarque\n", id, andarAtual);
            return true;
        }

        // Verifica se tem alguém para embarcar
        Andar andar = predio.getAndar(andarAtual);
        if (andar != null && andar.getPessoasAguardando().tamanho() > 0 && temEspaco()) {
            System.out.printf("DEBUG: Elevador %d verificando se deve parar no andar %d\n", id, andarAtual);
            System.out.printf("DEBUG: Pessoas aguardando: %d\n", andar.getPessoasAguardando().tamanho());
            
            PainelExterno painel = andar.getPainelExterno();
            
            // Verifica se deve parar baseado no tipo de painel
            if (painel instanceof PainelExternoUnico) {
                if (painel.isChamadaAtiva()) {
                    System.out.printf("🛑 Elevador %d parando no andar %d para embarque (Painel Único)\n", id, andarAtual);
                    return true;
                }
            } else if (painel instanceof PainelExternoSubirDescer) {
                PainelExternoSubirDescer painelSD = (PainelExternoSubirDescer) painel;
                if ((subindo && painelSD.isChamadaSubir()) || (!subindo && painelSD.isChamadaDescer())) {
                    System.out.printf("🛑 Elevador %d parando no andar %d para embarque (Subir/Descer)\n", id, andarAtual);
                    return true;
                }
            } else if (painel instanceof PainelExternoNumerico) {
                PainelExternoNumerico painelNum = (PainelExternoNumerico) painel;
                if (painelNum.isChamadaAtiva()) {
                    int destino = painelNum.getAndarDestino();
                    // Verifica se o elevador está indo na direção correta para o destino
                    if ((subindo && destino > andarAtual) || (!subindo && destino < andarAtual)) {
                        System.out.printf("🛑 Elevador %d parando no andar %d para embarque (Numérico - Destino: %d)\n", 
                            id, andarAtual, destino);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void procurarChamadasAtivas() {
        Lista<Andar> andares = predio.getAndares();
        Ponteiro<Andar> pAndar = andares.getInicio();
        int menorDistancia = Integer.MAX_VALUE;
        Andar andarMaisProximo = null;
        
        while (pAndar != null) {
            Andar andar = pAndar.getElemento();
            if (andar.getPessoasAguardando().tamanho() > 0) {
                int distancia = Math.abs(andar.getNumero() - andarAtual);
                if (distancia < menorDistancia) {
                    menorDistancia = distancia;
                    andarMaisProximo = andar;
                }
            }
            pAndar = pAndar.getProximo();
        }

        if (andarMaisProximo != null) {
            adicionarDestino(andarMaisProximo.getNumero());
            System.out.printf("🔍 Elevador %d direcionado para andar %d (distância: %d)\n", 
                id, andarMaisProximo.getNumero(), menorDistancia);
        }
    }

    private boolean temAlguemComDestino(int andar) {
        for (int i = 0; i < pessoasDentro.tamanho(); i++) {
            Pessoa p = pessoasDentro.dequeue();
            boolean ehDestino = p.getAndarDestino() == andar;
            pessoasDentro.enqueue(p);
            if (ehDestino) return true;
        }
        return false;
    }

    public void desembarcarNoAndar(int andarAtual, int minutoSimulado) {
        int tamanhoInicial = pessoasDentro.tamanho();
        if (tamanhoInicial == 0) return;

        Fila<Pessoa> pessoasRestantes = new Fila<>();
        boolean alguemDesembarcou = false;
        int pessoasDesembarcadas = 0;
        int tempoTotalViagem = 0;

        for (int i = 0; i < tamanhoInicial; i++) {
            Pessoa p = pessoasDentro.dequeue();
            if (p.getAndarDestino() == andarAtual) {
                p.sairElevador(minutoSimulado);
                alguemDesembarcou = true;
                pessoasDesembarcadas++;
                tempoTotalViagem += p.getTempoViagem();
                
                // Atualiza contadores por tipo de horário
                if (ehHorarioDePico(minutoSimulado)) {
                    pessoasTransportadasHorarioPico++;
                } else {
                    pessoasTransportadasHorarioNormal++;
                }
                
                System.out.printf("🚶 Pessoa %d desembarcou no andar %d (Tempo de viagem: %d minutos)\n", 
                    p.getId(), andarAtual, p.getTempoViagem());
            } else {
                pessoasRestantes.enqueue(p);
            }
        }

        // Atualiza a fila de pessoas dentro do elevador
        pessoasDentro = pessoasRestantes;

        if (alguemDesembarcou) {
            totalPessoasTransportadas += pessoasDesembarcadas;
            double tempoMedio = pessoasDesembarcadas > 0 ? (double)tempoTotalViagem / pessoasDesembarcadas : 0;
            System.out.printf("\n📊 Elevador %d:\n", id);
            System.out.printf("   👥 Total transportado: %d pessoas\n", totalPessoasTransportadas);
            System.out.printf("   ⏱️ Tempo médio de viagem: %.1f minutos\n", tempoMedio);
            System.out.printf("   ⏰ Horário de pico: %d pessoas\n", pessoasTransportadasHorarioPico);
            System.out.printf("   ⏱️ Horário normal: %d pessoas\n\n", pessoasTransportadasHorarioNormal);
        }
    }

    public void adicionarDestino(int andar) {
        // Evita destinos repetidos
        Ponteiro<Integer> p = destinos.getInicio();
        while (p != null) {
            if (p.getElemento() == andar) return;
            p = p.getProximo();
        }
        destinos.inserirFim(andar);
    }

    public int getAndarAtual() {
        return andarAtual;
    }

    public boolean estaParado(){
        return destinos.estaVazia();
    }

    public boolean temPessoasDentro(){
        return pessoasDentro.tamanho() > 0;
    }

    public Fila<Pessoa> getPessoasDentro(){
        return pessoasDentro;
    }

    public int getId(){
        return id;
    }

    public Lista<Integer> getDestinos() {
        return destinos;
    }

    @Override
    public void atualizar(int minutoSimulado) {
        // Primeiro verifica se deve parar no andar atual
        if (devePararNoAndarAtual()) {
            // Primeiro desembarca TODAS as pessoas que precisam sair neste andar
            desembarcarNoAndar(andarAtual, minutoSimulado);
            
            // Depois tenta embarcar pessoas
            if (temEspaco()) {
                processarEmbarque(minutoSimulado);
            }
        }
        
        // Por fim, movimenta o elevador
        movimentarElevadorInteligente();
    }

    private boolean ehHorarioDePico(int minutoSimulado) {
        int hora = (minutoSimulado / 60) % 24;
        return hora == 8 || hora == 9 || hora == 12 || hora == 13 || hora == 17 || hora == 18;
    }

    // Getters para as métricas
    public int getTotalPessoasTransportadas() {
        return totalPessoasTransportadas;
    }

    public int getPessoasTransportadasHorarioPico() {
        return pessoasTransportadasHorarioPico;
    }

    public int getPessoasTransportadasHorarioNormal() {
        return pessoasTransportadasHorarioNormal;
    }
}