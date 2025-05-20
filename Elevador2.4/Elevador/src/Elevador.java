

public class Elevador extends EntidadeSimulavel {
    private int id;
    private int andarAtual = 0;
    private int capacidadeMaxima;
    private Fila<Pessoa> pessoasDentro;
    private boolean subindo = true;
    private int andarMaximo;
    private Lista<Integer> destinos = new Lista<>();
    private Predio predio;


    public Elevador(int id, int capacidadeMaxima, int andarMaximo, Predio predio) {
        this.id = id;
        this.capacidadeMaxima = capacidadeMaxima;
        this.andarMaximo = andarMaximo;
        this.pessoasDentro = new Fila<>();
        this.predio = predio;

    }

    public boolean temEspaco() {
        return pessoasDentro.tamanho() < capacidadeMaxima;
    }

    public void embarcar(Pessoa p) {
        if (temEspaco()) {
            pessoasDentro.enqueue(p);
            p.entrarElevador();
            // System.out.println("Pessoa " + p.getId() + " embarcou no elevador " + id);
        } else {
            // System.out.println("Elevador " + id + " está cheio. Pessoa " + p.getId() + " não embarcou.");
        }
    }
    public void embarcarPessoasNoAndarAtual(Andar andar) {
        Fila<Pessoa> filaDePessoaAguardando = andar.getPessoasAguardando();

        // System.out.println("Fila de espera no andar " + andarAtual + ": " + filaDePessoaAguardando.toString());
        // System.out.println("Destinos atuais do elevador: " + destinos.toString());

        int tamanhoFila = filaDePessoaAguardando.tamanho();
        for (int i = 0; i < tamanhoFila && temEspaco(); i++) {
            Pessoa p = filaDePessoaAguardando.dequeue();  // Remove a pessoa da fila do andar
            embarcar(p);                 // coloca a pessoa no elevador
            adicionarDestino(p.getAndarDestino());  // Adiciona o destino da pessoa na lista de destinos
        }
    }
    public void movimentarElevadorInteligente(){
        if (destinos.estaVazia()) return;

        int proximoDestino = destinos.primeiroElemento();

        if (andarAtual < proximoDestino) {
            andarAtual++;
        } else if (andarAtual > proximoDestino) {
                andarAtual--;
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

    public void desembarcarNoAndar(int andarAtual) {

        int tamanhoInicial = pessoasDentro.tamanho();

        for (int i = 0; i < tamanhoInicial; i++) {
            Pessoa p = pessoasDentro.dequeue();
            if (p.getAndarDestino() == andarAtual) {
                // System.out.println("Pessoa " + p.getId() + " desembarcando no andar " + andarAtual);
                p.sairElevador();
            } else {
                // System.out.println("Pessoa " + p.getId() + " ainda não chegou no destino: " + p.getAndarDestino());
                pessoasDentro.enqueue(p);

                }
            }
        if (!temAlguemComDestino(andarAtual)){
            destinos.removerValor(andarAtual);
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
    private void processarEmbarque(){
        Andar andar = predio.getAndar(andarAtual);// aqui tem acesso o predio
        if (andar != null){
            embarcarPessoasNoAndarAtual(andar);
        }
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
        //System.out.println("Elevador " + id + " no andar " + andarAtual + " no minuto " + minutoSimulado);
        desembarcarNoAndar(andarAtual);
        processarEmbarque();
        movimentarElevadorInteligente();







    }


}