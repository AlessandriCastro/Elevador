import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Andar extends EntidadeSimulavel implements Serializable {
    private int numero;
    private PainelExterno painelExterno;
    private FilaPrior pessoasAguardando;
    private Lista<Pessoa> pessoas;

    public Andar(int numero, String tipoPainel) {
        this.numero = numero;
        this.pessoasAguardando = new FilaPrior();
        this.pessoas = new Lista<>();
        
        // Cria o painel externo apropriado
        switch (tipoPainel) {
            case "Unico":
                this.painelExterno = new PainelExternoUnico(this);
                break;
            case "SubirDescer":
                this.painelExterno = new PainelExternoSubirDescer(this);
                break;
            case "Numerico":
                this.painelExterno = new PainelExternoNumerico(this);
                break;
            default:
                this.painelExterno = new PainelExternoUnico(this);
        }
    }

    public void adicionarPessoa(int id, int andarOrigem, int andarDestino, int tipoPrioridade) {
        System.out.printf("DEBUG: Adicionando pessoa %d ao andar %d (Origem: %d, Destino: %d, Prioridade: %d)\n",
            id, numero, andarOrigem, andarDestino, tipoPrioridade);
        Pessoa p = new Pessoa(id, andarOrigem, andarDestino, tipoPrioridade);
        pessoas.inserirFim(p);
        pessoasAguardando.enqueue(id, tipoPrioridade);
        
        // Ativa o painel externo baseado no tipo
        PainelExterno painel = getPainelExterno();
        if (painel instanceof PainelExternoUnico) {
            painel.chamarElevador();
        } else if (painel instanceof PainelExternoSubirDescer) {
            PainelExternoSubirDescer painelSD = (PainelExternoSubirDescer) painel;
            if (andarDestino > numero) {
                painelSD.chamarSubir();
            } else {
                painelSD.chamarDescer();
            }
        } else if (painel instanceof PainelExternoNumerico) {
            PainelExternoNumerico painelNum = (PainelExternoNumerico) painel;
            painelNum.chamarParaAndar(andarDestino);
        }
        
        System.out.printf("DEBUG: Pessoa %d adicionada com sucesso ao andar %d\n", id, numero);
    }

    public Pessoa getPessoaPorId(int id) {
        System.out.printf("DEBUG: Buscando pessoa %d no andar %d\n", id, numero);
        Ponteiro<Pessoa> p = pessoas.getInicio();
        while (p != null) {
            if (p.getElemento().getId() == id) {
                System.out.printf("DEBUG: Pessoa %d encontrada no andar %d\n", id, numero);
                return p.getElemento();
            }
            p = p.getProximo();
        }
        System.out.printf("DEBUG: Pessoa %d NÃO encontrada no andar %d\n", id, numero);
        return null;
    }

    public void removerPessoa(int id) {
        System.out.printf("DEBUG: Tentando remover pessoa %d do andar %d\n", id, numero);
        Ponteiro<Pessoa> p = pessoas.getInicio();
        while (p != null) {
            if (p.getElemento().getId() == id) {
                pessoas.removerValor(p.getElemento());
                System.out.printf("DEBUG: Pessoa %d removida com sucesso do andar %d\n", id, numero);
                
                // Se não há mais pessoas aguardando, cancela a chamada do painel
                if (pessoasAguardando.tamanho() == 0) {
                    painelExterno.cancelarChamada();
                }
                
                return;
            }
            p = p.getProximo();
        }
        System.out.printf("DEBUG: Pessoa %d NÃO encontrada para remoção no andar %d\n", id, numero);
    }

    public int getNumero() {
        return numero;
    }

    public PainelExterno getPainelExterno() {
        return painelExterno;
    }

    public FilaPrior getPessoasAguardando() {
        return pessoasAguardando;
    }

    @Override
    public void atualizar(int minutoSimulado) {
        // Atualização do andar, se necessário
    }
}