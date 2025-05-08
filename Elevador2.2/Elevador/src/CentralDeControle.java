public class CentralDeControle extends EntidadeSimulavel {
    private Lista<Elevador> elevadores;
    private int andarMaximo;
    private Predio predio;


    public CentralDeControle(int quantidadeElevadores, int andarMaximo, Predio predio) {
        this.predio = predio;
        this.andarMaximo = andarMaximo;
        elevadores = new Lista<>();
        for (int i = 0; i < quantidadeElevadores; i++) {
            elevadores.add(new Elevador(i, 5, andarMaximo,predio), 0); // Corrigido: adiciona novo elevador
        }
    }
//    private void atribuirChamadas() { //pega o andar de origem da Pessoa
//        for (int i = 0; i <= andarMaximo; i++) {
//            Andar andar = predio.getAndar(i);
//            PainelElevador painel = andar.getPainel();
//
//
//            if (painel.isBotaoSubirAtivado() || painel.isBotaoDescerAtivado()) {
//                Fila<Pessoa> PessoasAguardando = andar.getPessoasAguardando();
//
//                if (!PessoasAguardando.estaVazia()) {
//                    Pessoa primeira = PessoasAguardando.primeiro();
//                    int andarOrigem = primeira.getAndarOrigem();
//
//                    Elevador melhorElevador = encontrarElevadorMaisProximo(andarOrigem);
//
//                    if (melhorElevador != null) {
//                        melhorElevador.adicionarDestino(andarOrigem);
//                        painel.resetar(); // Evita múltiplos elevadores indo ao mesmo andar
//                    }
//                }
//            }
//        }
//    }
//    private Elevador encontrarElevadorMaisProximo(int andarSolicitado) {
//
//        Elevador maisProximo = null;
//        int menorDistancia = -1;
//
//        Ponteiro<Elevador> p = elevadores.getInicio();
//        while (p != null) {
//            Elevador elevador = p.getElemento();
//            int distancia = Math.abs(elevador.getAndarAtual() - andarSolicitado); //Math.abs para garantir que o numero seja posistivo
//
//            if (menorDistancia == -1 || distancia < menorDistancia) {
//                menorDistancia = distancia;
//                maisProximo = elevador;
//            }
//
//            p = p.getProximo();
//        }
//
//        return maisProximo;
//    }


    @Override
    public void atualizar(int minutoSimulado) {
//        atribuirChamadas();
        Ponteiro<Elevador> p = elevadores.getInicio();
        while (p != null) {
            Elevador e = p.getElemento();
            e.atualizar(minutoSimulado);
            p = p.getProximo();
        }
    }

    public Lista<Elevador> getElevadores() {
        return elevadores;
    }
}
