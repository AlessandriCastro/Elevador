import java.io.Serializable;

public class Heuristica {
    private Predio predio;
    private Lista<Elevador> elevadores;
    private int totalPessoasTransportadas;

    public Heuristica(Predio predio, Lista<Elevador> elevadores) {
        this.predio = predio;
        this.elevadores = elevadores;
        this.totalPessoasTransportadas = 0;
    }

    private class CustoElevador {
        Elevador elevador;
        int custo;
        boolean temDesvio;
        boolean direcaoCompativel;
        int chamadasPendentes;

        CustoElevador(Elevador elevador, int custo, boolean temDesvio, boolean direcaoCompativel, int chamadasPendentes) {
            this.elevador = elevador;
            this.custo = custo;
            this.temDesvio = temDesvio;
            this.direcaoCompativel = direcaoCompativel;
            this.chamadasPendentes = chamadasPendentes;
        }
    }

    public Elevador encontrarElevadorMaisProximo(int andarSolicitado, boolean temPrioridade) {
        CustoElevador melhorOpcao = null;
        int menorCusto = Integer.MAX_VALUE;

        Ponteiro<Elevador> p = elevadores.getInicio();
        while (p != null) {
            Elevador elevador = p.getElemento();
            
            // Verifica se o elevador já tem este andar como destino
            boolean andarJaDestino = false;
            Ponteiro<Integer> pDestino = elevador.getDestinos().getInicio();
            while (pDestino != null) {
                if (pDestino.getElemento() == andarSolicitado) {
                    andarJaDestino = true;
                    break;
                }
                pDestino = pDestino.getProximo();
            }
            
            if (!andarJaDestino) {
                // Calcula o custo para este elevador
                CustoElevador custo = calcularCustoElevador(elevador, andarSolicitado);
                
                // Se tem prioridade, ignora elevadores que já estão indo para outro andar
                if (!temPrioridade || elevador.estaParado() || elevador.getPessoasDentro().tamanho() == 0) {
                    // Aplica penalidades
                    if (custo.temDesvio) {
                        custo.custo += 5; // Aumenta penalidade por desvio
                    }
                    if (!custo.direcaoCompativel) {
                        custo.custo += 10; // Aumenta penalidade por inversão de direção
                    }
                    if (elevador.getPessoasDentro().tamanho() >= 4) {
                        custo.custo += 5; // Aumenta penalidade por elevador quase cheio
                    }
                    
                    // Penalidade por elevador sobrecarregado
                    if (custo.chamadasPendentes > 3) {
                        custo.custo += custo.chamadasPendentes * 2;
                    }

                    // Bônus para elevadores vazios e parados
                    if (elevador.estaParado() && elevador.getPessoasDentro().tamanho() == 0) {
                        custo.custo -= 5;
                    }

                    // Penalidade por elevadores próximos
                    Ponteiro<Elevador> pOutro = elevadores.getInicio();
                    while (pOutro != null) {
                        if (pOutro.getElemento() != elevador) {
                            int distanciaEntreElevadores = Math.abs(elevador.getAndarAtual() - pOutro.getElemento().getAndarAtual());
                            if (distanciaEntreElevadores <= 1) {
                                custo.custo += 15; // Penalidade alta para elevadores muito próximos
                            }
                        }
                        pOutro = pOutro.getProximo();
                    }
                    
                    if (custo.custo < menorCusto) {
                        menorCusto = custo.custo;
                        melhorOpcao = custo;
                    }
                }
            }
            p = p.getProximo();
        }

        if (melhorOpcao != null) {
            System.out.printf("🎯 Elevador %d escolhido para andar %d\n", 
                melhorOpcao.elevador.getId(), andarSolicitado);
            System.out.printf("   Custo: %d | Desvio: %s | Direção Compatível: %s | Chamadas Pendentes: %d\n",
                melhorOpcao.custo,
                melhorOpcao.temDesvio ? "Sim" : "Não",
                melhorOpcao.direcaoCompativel ? "Sim" : "Não",
                melhorOpcao.chamadasPendentes);
            return melhorOpcao.elevador;
        }
        return null;
    }

    private CustoElevador calcularCustoElevador(Elevador elevador, int andarSolicitado) {
        int custo = 0;
        boolean temDesvio = false;
        boolean direcaoCompativel = true;
        int andarAtual = elevador.getAndarAtual();
        Lista<Integer> destinos = elevador.getDestinos();
        int chamadasPendentes = destinos.tamanho();

        // Se o elevador está parado
        if (destinos.estaVazia()) {
            custo = Math.abs(andarAtual - andarSolicitado);
            return new CustoElevador(elevador, custo, false, true, 0);
        }

        // Se o elevador está em movimento
        int primeiroDestino = destinos.primeiroElemento();
        boolean subindo = primeiroDestino > andarAtual;
        boolean chamadaSubindo = andarSolicitado > andarAtual;

        // Verifica compatibilidade de direção
        direcaoCompativel = (subindo && chamadaSubindo) || (!subindo && !chamadaSubindo);

        // Se está no mesmo andar
        if (andarAtual == andarSolicitado) {
            return new CustoElevador(elevador, 0, false, true, chamadasPendentes);
        }

        // Verifica se o andar solicitado está no caminho
        if (subindo && andarSolicitado > andarAtual) {
            // Verifica se está entre o andar atual e o próximo destino
            if (andarSolicitado < primeiroDestino) {
                custo = andarSolicitado - andarAtual;
                temDesvio = false;
            } else {
                // Precisa fazer um desvio
                custo = Math.abs(andarAtual - andarSolicitado) + 10;
                temDesvio = true;
            }
        } else if (!subindo && andarSolicitado < andarAtual) {
            // Verifica se está entre o andar atual e o próximo destino
            if (andarSolicitado > primeiroDestino) {
                custo = andarAtual - andarSolicitado;
                temDesvio = false;
            } else {
                // Precisa fazer um desvio
                custo = Math.abs(andarAtual - andarSolicitado) + 10;
                temDesvio = true;
            }
        } else {
            // Direção oposta, custo maior
            custo = Math.abs(andarAtual - andarSolicitado) + 15;
            temDesvio = true;
        }

        return new CustoElevador(elevador, custo, temDesvio, direcaoCompativel, chamadasPendentes);
    }

    public void atualizarTotalTransportado() {
        totalPessoasTransportadas = 0;
        int tempoTotalViagem = 0;
        int totalViagens = 0;
        
        System.out.println("\n📊 ESTATÍSTICAS DE TRANSPORTE 📊");
        System.out.println("==========================================");
        
        // Estatísticas por elevador
        Ponteiro<Elevador> p = elevadores.getInicio();
        while (p != null) {
            Elevador elevador = p.getElemento();
            int transportadas = elevador.getTotalPessoasTransportadas();
            totalPessoasTransportadas += transportadas;
            
            System.out.printf("🛗 Elevador %d:\n", elevador.getId());
            System.out.printf("   👥 Total transportado: %d pessoas\n", transportadas);
            
            // Calcula tempo médio para este elevador
            Fila<Pessoa> pessoas = elevador.getPessoasDentro();
            int tamanho = pessoas.tamanho();
            int tempoElevador = 0;
            int viagensElevador = 0;
            
            // Primeiro, soma os tempos de viagem
            for (int i = 0; i < tamanho; i++) {
                Pessoa pessoa = pessoas.dequeue();
                if (pessoa.getTempoViagem() > 0) {
                    tempoElevador += pessoa.getTempoViagem();
                    viagensElevador++;
                    System.out.printf("   DEBUG: Pessoa %d - Tempo: %d minutos\n", 
                        pessoa.getId(), pessoa.getTempoViagem());
                }
                pessoas.enqueue(pessoa);
            }
            
            // Depois calcula a média
            if (viagensElevador > 0) {
                double tempoMedioElevador = (double)tempoElevador / viagensElevador;
                System.out.printf("   ⏱️ Tempo médio de viagem: %.1f minutos\n", tempoMedioElevador);
                tempoTotalViagem += tempoElevador;
                totalViagens += viagensElevador;
            }
            
            System.out.println("------------------------------------------");
            p = p.getProximo();
        }
        
        // Estatísticas gerais
        System.out.println("📈 RESUMO GERAL:");
        System.out.printf("   👥 Total de pessoas transportadas: %d\n", totalPessoasTransportadas);
        
        if (totalViagens > 0) {
            double tempoMedio = (double)tempoTotalViagem / totalViagens;
            System.out.printf("   ⏱️ Tempo médio de viagem: %.1f minutos\n", tempoMedio);
            System.out.printf("   DEBUG: Total de viagens: %d, Tempo total: %d\n", totalViagens, tempoTotalViagem);
        }
        
        System.out.println("==========================================\n");
    }

    // Novo método para calcular a pontuação de um elevador para uma chamada
    private int calcularPontuacaoElevador(Elevador elevador, int andarChamada, int direcaoChamada, int prioridadeUsuario) {
        int score = 0;
        int andarAtual = elevador.getAndarAtual();
        boolean parado = elevador.estaParado();
        boolean cheio = elevador.getPessoasDentro().tamanho() >= 5;
        int paradas = elevador.getDestinos().tamanho();
        int distancia = Math.abs(andarAtual - andarChamada);
        boolean mesmaDirecao = false;
        boolean inverterDirecao = false;
        boolean subindo = true;
        if (!elevador.getDestinos().estaVazia()) {
            int destino = elevador.getDestinos().primeiroElemento();
            subindo = destino > andarAtual;
            mesmaDirecao = (subindo && direcaoChamada > 0) || (!subindo && direcaoChamada < 0);
            inverterDirecao = !mesmaDirecao;
        } else {
            mesmaDirecao = true;
        }

        // +5 se já estiver indo na mesma direção
        if (mesmaDirecao) score += 5;
        // +4 se estiver próximo do andar da chamada
        if (distancia <= 2) score += 4;
        // +3 se estiver parado
        if (parado) score += 3;
        // -3 se estiver cheio ou quase cheio
        if (cheio || elevador.getPessoasDentro().tamanho() >= 4) score -= 3;
        // -4 se tiver que inverter a direção
        if (inverterDirecao) score -= 4;
        // +6 se o usuário for cadeirante
        if (prioridadeUsuario == 2) score += 6;
        // +4 se o usuário for idoso
        if (prioridadeUsuario == 1) score += 4;
        // +1 se o elevador tiver menos paradas agendadas
        score += Math.max(0, 3 - paradas); // até +3 se tiver poucas paradas
        return score;
    }

    // Modificar a distribuição de chamadas para usar a nova heurística
    public void distribuirChamadas() {
        try {
            Lista<Andar> andares = predio.getAndares();
            Ponteiro<Andar> atual = andares.getInicio();
            Lista<Integer> andaresAtendidos = new Lista<>();
            
            while (atual != null) {
                try {
                    Andar andar = atual.getElemento();
                    if (andar == null) {
                        atual = atual.getProximo();
                        continue;
                    }
                    
                    FilaPrior pessoasAguardando = andar.getPessoasAguardando();
                    if (pessoasAguardando != null && pessoasAguardando.tamanho() > 0) {
                        int numeroAndar = andar.getNumero();
                        
                        boolean jaAtendido = false;
                        Ponteiro<Integer> pAtendido = andaresAtendidos.getInicio();
                        while (pAtendido != null) {
                            if (pAtendido.getElemento() == numeroAndar) {
                                jaAtendido = true;
                                break;
                            }
                            pAtendido = pAtendido.getProximo();
                        }
                        
                        if (jaAtendido) {
                            atual = atual.getProximo();
                            continue;
                        }
                        
                        boolean algumElevadorJaVai = false;
                        Ponteiro<Elevador> pVerificar = elevadores.getInicio();
                        while (pVerificar != null) {
                            Elevador elevador = pVerificar.getElemento();
                            Ponteiro<Integer> pDest = elevador.getDestinos().getInicio();
                            while (pDest != null) {
                                if (pDest.getElemento() == numeroAndar) {
                                    algumElevadorJaVai = true;
                                    break;
                                }
                                pDest = pDest.getProximo();
                            }
                            if (algumElevadorJaVai) break;
                            pVerificar = pVerificar.getProximo();
                        }
                        
                        if (algumElevadorJaVai) {
                            System.out.printf("🔄 Andar %d já está sendo atendido por outro elevador\n", numeroAndar);
                            atual = atual.getProximo();
                            continue;
                        }
                        
                        int prioridadeUsuario = 0;
                        if (pessoasAguardando.temElementosNaPrioridade(2)) prioridadeUsuario = 2;
                        else if (pessoasAguardando.temElementosNaPrioridade(1)) prioridadeUsuario = 1;
                        
                        int direcaoChamada = 0;
                        if (andar.getNumero() < predio.getAndares().tamanho() - 1) direcaoChamada = 1;
                        else direcaoChamada = -1;

                        Elevador melhorElevador = null;
                        int melhorScore = Integer.MIN_VALUE;
                        int menorDistancia = Integer.MAX_VALUE;
                        
                        Ponteiro<Elevador> pElev = elevadores.getInicio();
                        while (pElev != null) {
                            Elevador elevador = pElev.getElemento();
                            
                            if (!elevador.estaParado() && elevador.getDestinos().tamanho() > 0) {
                                pElev = pElev.getProximo();
                                continue;
                            }
                            
                            boolean jaVaiAtender = false;
                            Ponteiro<Integer> pDest = elevador.getDestinos().getInicio();
                            while (pDest != null) {
                                if (pDest.getElemento() == numeroAndar) {
                                    jaVaiAtender = true;
                                    break;
                                }
                                pDest = pDest.getProximo();
                            }
                            
                            if (jaVaiAtender) {
                                pElev = pElev.getProximo();
                                continue;
                            }
                            
                            int score = calcularPontuacaoElevador(elevador, numeroAndar, direcaoChamada, prioridadeUsuario);
                            int distancia = Math.abs(elevador.getAndarAtual() - numeroAndar);
                            
                            if (score > melhorScore || (score == melhorScore && distancia < menorDistancia)) {
                                melhorScore = score;
                                menorDistancia = distancia;
                                melhorElevador = elevador;
                            }
                            pElev = pElev.getProximo();
                        }
                        
                        if (melhorElevador != null) {
                            melhorElevador.adicionarDestino(numeroAndar);
                            andaresAtendidos.inserirFim(numeroAndar);
                            
                            System.out.printf("📞 Chamada do andar %d atribuída ao elevador %d (score: %d, distância: %d)\n",
                                numeroAndar, melhorElevador.getId(), melhorScore, menorDistancia);
                        } else {
                            System.out.printf("⚠️ Nenhum elevador disponível para atender chamada do andar %d\n", numeroAndar);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao processar andar na distribuição de chamadas: " + e.getMessage());
                }
                atual = atual.getProximo();
            }
            atualizarTotalTransportado();
        } catch (Exception e) {
            System.err.println("Erro crítico na distribuição de chamadas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void distribuirElevadoresVazios() {
        int totalElevadores = 0;
        Ponteiro<Elevador> p = elevadores.getInicio();
        while (p != null) {
            totalElevadores++;
            p = p.getProximo();
        }

        int numeroAndares = 0;
        Ponteiro<Andar> a = predio.getAndares().getInicio();
        while (a != null) {
            numeroAndares++;
            a = a.getProximo();
        }

        p = elevadores.getInicio();
        int numeroElevador = 0;
        while (p != null) {
            Elevador elevador = p.getElemento();
            if (elevador.estaParado() && !elevador.temPessoasDentro()) {
                int andarDesejado = (numeroElevador * numeroAndares) / totalElevadores;
                elevador.adicionarDestino(andarDesejado);
            }
            numeroElevador++;
            p = p.getProximo();
        }
    }

    public int getTotalPessoasTransportadas() {
        return totalPessoasTransportadas;
    }
}