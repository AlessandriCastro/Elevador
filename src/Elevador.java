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
    private double tempoPorAndarPico = 2.0;      // minutos por andar em pico
    private double tempoPorAndarForaPico = 1.0;  // minutos por andar fora de pico
    private int tempoRestanteViagem = 0;         // minutos simulados restantes para chegar ao próximo andar
    private boolean emViagem = false;
    private boolean horarioPico = false;

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
        if (andar == null) {
            System.err.println("Erro: Andar é null");
            return;
        }
        
        FilaPrior filaDePessoaAguardando = andar.getPessoasAguardando();

        // Se não há ninguém esperando ou elevador está cheio, retorna
        if (filaDePessoaAguardando == null || filaDePessoaAguardando.tamanho() == 0 || !temEspaco()) {
            System.out.printf("DEBUG: Elevador %d não pode embarcar - Fila vazia ou cheio\n", id);
            return;
        }

        System.out.printf("🚪 Elevador %d no andar %d - Tentando embarcar %d pessoas\n", 
            id, andarAtual, filaDePessoaAguardando.tamanho());
        System.out.printf("DEBUG: Elevador %d - Capacidade: %d, Ocupado: %d, Espaço: %d\n", 
            id, capacidadeMaxima, pessoasDentro.tamanho(), capacidadeMaxima - pessoasDentro.tamanho());

        int pessoasEmbarcadas = 0;
        int espacoDisponivel = capacidadeMaxima - pessoasDentro.tamanho();

        // Primeiro embarca cadeirantes (prioridade 2)
        System.out.printf("DEBUG: Verificando cadeirantes (prioridade 2) - Tem elementos: %s\n", 
            filaDePessoaAguardando.temElementosNaPrioridade(2));
        while (temEspaco() && filaDePessoaAguardando.temElementosNaPrioridade(2) && pessoasEmbarcadas < espacoDisponivel) {
            try {
                int idPessoa = filaDePessoaAguardando.dequeue(2);
                System.out.printf("DEBUG: Tentando embarcar cadeirante %d\n", idPessoa);
                Pessoa p = andar.getPessoaPorId(idPessoa);
                if (p != null) {
                    System.out.printf("DEBUG: Elevador %d - Pessoa %d embarcando, destino: %d\n", id, idPessoa, p.getAndarDestino());
                    embarcar(p, minutoAtual);
                    System.out.printf("DEBUG: Elevador %d - Chamando adicionarDestino(%d)\n", id, p.getAndarDestino());
                    adicionarDestino(p.getAndarDestino());
                    System.out.printf("DEBUG: Elevador %d - Após adicionarDestino, lista tem %d destinos: %s\n", 
                        id, destinos.tamanho(), getDestinosString());
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
        System.out.printf("DEBUG: Verificando idosos (prioridade 1) - Tem elementos: %s\n", 
            filaDePessoaAguardando.temElementosNaPrioridade(1));
        while (temEspaco() && filaDePessoaAguardando.temElementosNaPrioridade(1) && pessoasEmbarcadas < espacoDisponivel) {
            try {
                int idPessoa = filaDePessoaAguardando.dequeue(1);
                System.out.printf("DEBUG: Tentando embarcar idoso %d\n", idPessoa);
                Pessoa p = andar.getPessoaPorId(idPessoa);
                if (p != null) {
                    System.out.printf("DEBUG: Elevador %d - Pessoa %d embarcando, destino: %d\n", id, idPessoa, p.getAndarDestino());
                    embarcar(p, minutoAtual);
                    System.out.printf("DEBUG: Elevador %d - Chamando adicionarDestino(%d)\n", id, p.getAndarDestino());
                    adicionarDestino(p.getAndarDestino());
                    System.out.printf("DEBUG: Elevador %d - Após adicionarDestino, lista tem %d destinos: %s\n", 
                        id, destinos.tamanho(), getDestinosString());
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
        System.out.printf("DEBUG: Verificando pessoas normais (prioridade 0) - Tem elementos: %s\n", 
            filaDePessoaAguardando.temElementosNaPrioridade(0));
        while (temEspaco() && filaDePessoaAguardando.temElementosNaPrioridade(0) && pessoasEmbarcadas < espacoDisponivel) {
            try {
                int idPessoa = filaDePessoaAguardando.dequeue(0);
                System.out.printf("DEBUG: Tentando embarcar pessoa normal %d\n", idPessoa);
                Pessoa p = andar.getPessoaPorId(idPessoa);
                if (p != null) {
                    System.out.printf("DEBUG: Elevador %d - Pessoa %d embarcando, destino: %d\n", id, idPessoa, p.getAndarDestino());
                    embarcar(p, minutoAtual);
                    System.out.printf("DEBUG: Elevador %d - Chamando adicionarDestino(%d)\n", id, p.getAndarDestino());
                    adicionarDestino(p.getAndarDestino());
                    System.out.printf("DEBUG: Elevador %d - Após adicionarDestino, lista tem %d destinos: %s\n", 
                        id, destinos.tamanho(), getDestinosString());
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
            System.out.printf("DEBUG: Elevador %d - Fila restante: %d pessoas\n", 
                id, filaDePessoaAguardando.tamanho());
        }
    }

    private void processarEmbarque(int minutoAtual) {
        Andar andar = predio.getAndar(andarAtual);
        if (andar != null) {
            embarcarPessoasNoAndarAtual(andar, minutoAtual);
        }
    }

    private void processarEmbarqueDesembarque(int minutoSimulado) {
        // Primeiro desembarca pessoas
        desembarcarNoAndar(andarAtual, minutoSimulado);
        // Depois tenta embarcar pessoas
        Andar andar = predio.getAndar(andarAtual);
        System.out.printf("DEBUG: Elevador %d tentando embarcar pessoas no andar %d\n", id, andarAtual);
        System.out.printf("DEBUG: Pessoas aguardando: %d\n", andar.getPessoasAguardando().tamanho());
        embarcarPessoasNoAndarAtual(andar, minutoSimulado);
    }

    public void movimentarElevadorInteligente(int minutoSimulado) {
        System.out.printf("DEBUG: Elevador %d - Destinos: %s, Andar atual: %d\n", id, getDestinosString(), andarAtual);
        
        if (destinos.estaVazia()) {
            System.out.printf("DEBUG: Elevador %d - Lista de destinos vazia, procurando chamadas ativas\n", id);
            procurarChamadasAtivas();
            return;
        }
        
        int proximoDestino = destinos.primeiroElemento();
        System.out.printf("DEBUG: Elevador %d - Próximo destino: %d\n", id, proximoDestino);
        
        // Se deve parar no andar atual (para embarque/desembarque)
        if (devePararNoAndarAtual()) {
            System.out.printf("DEBUG: Elevador %d - Deve parar no andar atual %d\n", id, andarAtual);
            processarEmbarqueDesembarque(minutoSimulado);
            
            // Verifica se ainda há pessoas com destino no andar atual após o desembarque
            if (andarAtual == proximoDestino && !temAlguemComDestino(andarAtual)) {
                System.out.printf("DEBUG: Elevador %d - Andar atual %d é o destino e não há mais pessoas com este destino, removendo\n", id, andarAtual);
                destinos.removerValor(andarAtual);
                emViagem = false;
                tempoRestanteViagem = 0;
                return;
            }
        }
        
        // Se chegou no destino sem precisar parar, verifica se há pessoas para desembarcar
        if (andarAtual == proximoDestino) {
            System.out.printf("DEBUG: Elevador %d - Chegou no destino %d, verificando se há pessoas para desembarcar\n", id, andarAtual);
            if (temAlguemComDestino(andarAtual)) {
                System.out.printf("DEBUG: Elevador %d - Há pessoas para desembarcar no destino %d\n", id, andarAtual);
                processarEmbarqueDesembarque(minutoSimulado);
                // Verifica novamente se ainda há pessoas com este destino
                if (!temAlguemComDestino(andarAtual)) {
                    System.out.printf("DEBUG: Elevador %d - Após desembarque, removendo destino %d\n", id, andarAtual);
                    destinos.removerValor(andarAtual);
                    emViagem = false;
                    tempoRestanteViagem = 0;
                    return;
                }
            } else {
                System.out.printf("DEBUG: Elevador %d - Nenhuma pessoa para desembarcar no destino %d, removendo\n", id, andarAtual);
                destinos.removerValor(andarAtual);
                emViagem = false;
                tempoRestanteViagem = 0;
                return;
            }
        }
        
        // Movimento instantâneo para o próximo andar
        System.out.printf("DEBUG: Elevador %d - Movendo de %d para %d\n", id, andarAtual, proximoDestino);
        if (andarAtual < proximoDestino) {
            andarAtual++;
            subindo = true;
        } else if (andarAtual > proximoDestino) {
            andarAtual--;
            subindo = false;
        }
        emViagem = false;
        tempoRestanteViagem = 0;
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
            System.out.printf("DEBUG: Elevador %d tem espaço: %s\n", id, temEspaco());
            
            PainelExterno painel = andar.getPainelExterno();
            System.out.printf("DEBUG: Tipo de painel: %s\n", painel.getClass().getSimpleName());
            
            // Verifica se deve parar baseado no tipo de painel
            if (painel instanceof PainelExternoUnico) {
                boolean chamadaAtiva = painel.isChamadaAtiva();
                System.out.printf("DEBUG: Painel único - Chamada ativa: %s\n", chamadaAtiva);
                if (chamadaAtiva) {
                    System.out.printf("🛑 Elevador %d parando no andar %d para embarque (Painel Único)\n", id, andarAtual);
                    return true;
                }
            } else if (painel instanceof PainelExternoSubirDescer) {
                PainelExternoSubirDescer painelSD = (PainelExternoSubirDescer) painel;
                boolean chamadaSubir = painelSD.isChamadaSubir();
                boolean chamadaDescer = painelSD.isChamadaDescer();
                System.out.printf("DEBUG: Painel Subir/Descer - Subir: %s, Descer: %s\n", chamadaSubir, chamadaDescer);
                // Se há chamada para subir ou descer, para independente da direção atual
                if (chamadaSubir || chamadaDescer) {
                    System.out.printf("🛑 Elevador %d parando no andar %d para embarque (Subir/Descer)\n", id, andarAtual);
                    return true;
                }
            } else if (painel instanceof PainelExternoNumerico) {
                PainelExternoNumerico painelNum = (PainelExternoNumerico) painel;
                boolean chamadaAtiva = painelNum.isChamadaAtiva();
                System.out.printf("DEBUG: Painel numérico - Chamada ativa: %s\n", chamadaAtiva);
                if (chamadaAtiva) {
                    // Para em qualquer chamada numérica, independente da direção
                    System.out.printf("🛑 Elevador %d parando no andar %d para embarque (Numérico)\n", id, andarAtual);
                    return true;
                }
            }
        } else {
            if (andar == null) {
                System.out.printf("DEBUG: Elevador %d - Andar é null\n", id);
            } else if (andar.getPessoasAguardando().tamanho() == 0) {
                System.out.printf("DEBUG: Elevador %d - Nenhuma pessoa aguardando\n", id);
            } else if (!temEspaco()) {
                System.out.printf("DEBUG: Elevador %d - Sem espaço disponível\n", id);
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
                int numeroAndar = andar.getNumero();
                
                boolean outroElevadorJaVai = false;
                Lista<Elevador> todosElevadores = predio.getCentral().getElevadores();
                Ponteiro<Elevador> pElev = todosElevadores.getInicio();
                while (pElev != null) {
                    Elevador outroElevador = pElev.getElemento();
                    if (outroElevador != this) {
                        Ponteiro<Integer> pDest = outroElevador.getDestinos().getInicio();
                        while (pDest != null) {
                            if (pDest.getElemento() == numeroAndar) {
                                outroElevadorJaVai = true;
                                break;
                            }
                            pDest = pDest.getProximo();
                        }
                        if (outroElevadorJaVai) break;
                    }
                    pElev = pElev.getProximo();
                }
                
                if (!outroElevadorJaVai) {
                    int distancia = Math.abs(numeroAndar - andarAtual);
                    if (distancia < menorDistancia) {
                        menorDistancia = distancia;
                        andarMaisProximo = andar;
                    }
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
        System.out.printf("DEBUG: Elevador %d verificando se alguém tem destino %d\n", id, andar);
        System.out.printf("DEBUG: Elevador %d tem %d pessoas dentro\n", id, pessoasDentro.tamanho());
        
        if (pessoasDentro.tamanho() == 0) {
            System.out.printf("DEBUG: Elevador %d - Nenhuma pessoa dentro, não há destino %d ✗\n", id, andar);
            return false;
        }
        
        for (int i = 0; i < pessoasDentro.tamanho(); i++) {
            try {
                Pessoa p = pessoasDentro.dequeue();
                if (p != null) {
                    boolean ehDestino = p.getAndarDestino() == andar;
                    System.out.printf("DEBUG: Elevador %d - Pessoa %d tem destino %d, andar verificado %d, ehDestino: %s\n", 
                        id, p.getId(), p.getAndarDestino(), andar, ehDestino);
                    pessoasDentro.enqueue(p);
                    if (ehDestino) {
                        System.out.printf("DEBUG: Elevador %d - Pessoa %d tem destino %d ✓\n", id, p.getId(), andar);
                        return true;
                    }
                } else {
                    System.out.printf("DEBUG: Elevador %d - Pessoa null encontrada na verificação de destino\n", id);
                    pessoasDentro.enqueue(p);
                }
            } catch (Exception e) {
                System.err.println("Erro ao verificar destino da pessoa: " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.printf("DEBUG: Elevador %d - Ninguém tem destino %d ✗\n", id, andar);
        return false;
    }

    public void desembarcarNoAndar(int andarAtual, int minutoSimulado) {
        try {
            int tamanhoInicial = pessoasDentro.tamanho();
            if (tamanhoInicial == 0) {
                System.out.printf("DEBUG: Elevador %d - Nenhuma pessoa para desembarcar no andar %d\n", id, andarAtual);
                return;
            }

            System.out.printf("DEBUG: Elevador %d tentando desembarcar no andar %d - %d pessoas dentro\n", 
                id, andarAtual, tamanhoInicial);

            Fila<Pessoa> pessoasRestantes = new Fila<>();
            boolean alguemDesembarcou = false;
            int pessoasDesembarcadas = 0;
            int tempoTotalViagem = 0;

            for (int i = 0; i < tamanhoInicial; i++) {
                try {
                    Pessoa p = pessoasDentro.dequeue();
                    if (p != null) {
                        System.out.printf("DEBUG: Elevador %d - Processando pessoa %d (destino: %d, andar atual: %d)\n", 
                            id, p.getId(), p.getAndarDestino(), andarAtual);
                        
                        if (p.getAndarDestino() == andarAtual) {
                            System.out.printf("DEBUG: Elevador %d - Pessoa %d VAI desembarcar no andar %d ✓\n", 
                                id, p.getId(), andarAtual);
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
                            System.out.printf("DEBUG: Elevador %d - Pessoa %d permanece no elevador (destino: %d) ✗\n", 
                                id, p.getId(), p.getAndarDestino());
                        }
                    } else {
                        System.out.printf("DEBUG: Elevador %d - Pessoa null encontrada, pulando...\n", id);
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao processar pessoa no desembarque: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Atualiza a fila de pessoas dentro do elevador
            pessoasDentro = pessoasRestantes;

            System.out.printf("DEBUG: Elevador %d - Após desembarque: %d pessoas restantes\n", 
                id, pessoasDentro.tamanho());

            if (alguemDesembarcou) {
                totalPessoasTransportadas += pessoasDesembarcadas;
                double tempoMedio = pessoasDesembarcadas > 0 ? (double)tempoTotalViagem / pessoasDesembarcadas : 0;
                System.out.printf("\n📊 Elevador %d:\n", id);
                System.out.printf("   👥 Total transportado: %d pessoas\n", totalPessoasTransportadas);
                System.out.printf("   ⏱️ Tempo médio de viagem: %.1f minutos\n", tempoMedio);
                System.out.printf("   ⏰ Horário de pico: %d pessoas\n", pessoasTransportadasHorarioPico);
                System.out.printf("   ⏱️ Horário normal: %d pessoas\n\n", pessoasTransportadasHorarioNormal);
            } else {
                System.out.printf("DEBUG: Elevador %d - Nenhuma pessoa desembarcou no andar %d\n", id, andarAtual);
            }
        } catch (Exception e) {
            System.err.println("Erro crítico no desembarque do elevador " + id + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void adicionarDestino(int andar) {
        System.out.printf("DEBUG: Elevador %d tentando adicionar destino %d (andar máximo: %d)\n", id, andar, andarMaximo);
        
        // Valida o andar
        if (andar < 0 || andar > andarMaximo) {
            System.err.printf("Erro: Andar %d inválido para elevador %d (máximo: %d)\n", andar, id, andarMaximo);
            return;
        }
        
        // Evita destinos repetidos
        Ponteiro<Integer> p = destinos.getInicio();
        while (p != null) {
            if (p.getElemento() == andar) {
                System.out.printf("DEBUG: Andar %d já está na lista de destinos do elevador %d\n", andar, id);
                return;
            }
            p = p.getProximo();
        }
        
        destinos.inserirFim(andar);
        System.out.printf("DEBUG: Andar %d adicionado como destino do elevador %d\n", andar, id);
        System.out.printf("DEBUG: Elevador %d agora tem %d destinos: %s\n", id, destinos.tamanho(), getDestinosString());
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
        try {
            movimentarElevadorInteligente(minutoSimulado);
        } catch (Exception e) {
            System.err.println("Erro crítico na atualização do elevador " + id + ": " + e.getMessage());
            e.printStackTrace();
        }
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

    public void setHorarioPico(boolean horarioPico) {
        this.horarioPico = horarioPico;
    }

    public boolean isHorarioPico() {
        return horarioPico;
    }

    public int getTempoRestanteViagem() {
        return tempoRestanteViagem;
    }

    public int calcularTempoViagem(int destino) {
        int distancia = Math.abs(destino - andarAtual);
        double tempoPorAndar = horarioPico ? tempoPorAndarPico : tempoPorAndarForaPico;
        return (int)Math.ceil(distancia * tempoPorAndar);
    }

    private String getDestinosString() {
        StringBuilder sb = new StringBuilder("[");
        Ponteiro<Integer> p = destinos.getInicio();
        while (p != null) {
            sb.append(p.getElemento());
            if (p.getProximo() != null) {
                sb.append(", ");
            }
            p = p.getProximo();
        }
        sb.append("]");
        return sb.toString();
    }
}