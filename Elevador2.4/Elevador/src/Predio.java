
public class Predio extends EntidadeSimulavel {
    private CentralDeControle central;
    private Lista<Andar> andares;

    public Predio(int quantidadeAndares, int quantidadeElevadores) {

        andares = new Lista<>();

        for (int i = 0; i < quantidadeAndares; i++) {
            andares.inserirFim(new Andar(i));
        }
        int andarMaximo = quantidadeAndares - 1;
        central = new CentralDeControle(quantidadeElevadores, andarMaximo, this);
    }




    private void mostrarEstadoAtual(int minutoSimulado) {
        System.out.println("\n=== MINUTO " + minutoSimulado + " ===");
        
        // Mostra status dos elevadores
        Lista<Elevador> elevadores = central.getElevadores();
        Ponteiro<Elevador> pElev = elevadores.getInicio();
        while (pElev != null) {
            Elevador elevador = pElev.getElemento();
            System.out.println("----------------------------------------");
            System.out.printf("⏰ Minuto: %d | 🛗 Elevador %d\n", minutoSimulado, elevador.getId());
            System.out.printf("📍 Andar Atual: %d\n", elevador.getAndarAtual());
            System.out.printf("👥 Pessoas no elevador: %s\n", 
                elevador.getPessoasDentro().estaVazia() ? "Vazio" : elevador.getPessoasDentro().toString());
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
            
            if (!andar.getPessoasAguardando().estaVazia()) {
                linha.append("👥 Aguardando: ").append(andar.getPessoasAguardando().toString());
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
        central.atualizar(minutoSimulado);
        mostrarEstadoAtual(minutoSimulado); // Chama o método de visualização
    }
}

