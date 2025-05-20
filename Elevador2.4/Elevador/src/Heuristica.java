public class Heuristica {
    private Predio predio;
    private Lista<Elevador> elevadores;

    public Heuristica(Predio predio, Lista<Elevador> elevadores) {
        this.predio = predio;
        this.elevadores = elevadores;
    }

    public Elevador encontrarElevadorMaisProximo(int andarSolicitado) {
        Elevador maisProximo = null;
        int menorDistancia = 999; // Um número grande como valor inicial

        Ponteiro<Elevador> p = elevadores.getInicio();
        while (p != null) {
            Elevador elevador = p.getElemento();
            // Calcula distância de forma simples
            int distancia;
            if (elevador.getAndarAtual() > andarSolicitado) {
                distancia = elevador.getAndarAtual() - andarSolicitado;
            } else {
                distancia = andarSolicitado - elevador.getAndarAtual();
            }
            
            // Se encontrar uma distância menor E o elevador tiver espaço
            if (distancia < menorDistancia && elevador.temEspaco()) {
                menorDistancia = distancia;
                maisProximo = elevador;
            }
            p = p.getProximo();
        }
        return maisProximo;
    }

    public void distribuirChamadas() {
        // Pega a lista de andares do prédio
        Lista<Andar> andares = predio.getAndares();
        Ponteiro<Andar> atual = andares.getInicio();
        
        // Verifica cada andar
        while (atual != null) {
            Andar andar = atual.getElemento();
            PainelElevador painel = andar.getPainel();

            // Se alguém chamou o elevador neste andar
            if (painel.isBotaoSubirAtivado() || painel.isBotaoDescerAtivado()) {
                atenderChamadaDoAndar(andar);
            }
            atual = atual.getProximo();
        }
    }

    private void atenderChamadaDoAndar(Andar andar) {
        // Verifica se tem alguém esperando
        Fila<Pessoa> pessoasAguardando = andar.getPessoasAguardando();
        if (!pessoasAguardando.estaVazia()) {
            // Pega a primeira pessoa da fila
            Pessoa proxima = pessoasAguardando.primeiro();
            
            // Encontra o elevador mais próximo
            Elevador melhorElevador = encontrarElevadorMaisProximo(andar.getNumero());
            
            // Se encontrou um elevador disponível
            if (melhorElevador != null) {
                melhorElevador.adicionarDestino(andar.getNumero());
                //System.out.println("Elevador " + melhorElevador.getId() + 
                    //" vai atender o andar " + andar.getNumero());
            }
        }
    }

    // Método para distribuir elevadores vazios em diferentes andares
    public void distribuirElevadoresVazios() {
        // Conta quantos elevadores tem
        int totalElevadores = 0;
    Ponteiro<Elevador> p = elevadores.getInicio();
    while (p != null) {
        totalElevadores++;
        p = p.getProximo();
    }

    // Conta quantos andares tem
    int numeroAndares = 0;
    Ponteiro<Andar> a = predio.getAndares().getInicio();
    while (a != null) {
        numeroAndares++;
        a = a.getProximo();
    }

    // Distribui os elevadores vazios
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
}