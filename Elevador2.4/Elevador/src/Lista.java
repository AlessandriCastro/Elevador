

class Ponteiro<T>{
    T elemento;
    Ponteiro<T> prox;
    public Ponteiro(T elemento) {
            this.elemento = elemento;
            this.prox = null;
 }

    public T getElemento() {
        return elemento;
    }

    public Ponteiro<T> getProximo() {
        return prox;
    }

}
public class Lista<T> {
    private int tamanho = 0;
    private Ponteiro<T> inicio;

    public Lista() {
        this.inicio = null;
    } 
    public int tamanho() {
        return tamanho;
    }

    public boolean add(T elemento, int p) {
        Ponteiro<T> novoPonteiro = new Ponteiro<>(elemento);

        if (p < 0) return false;

        if (p == 0) {
            novoPonteiro.prox = inicio;
            inicio = novoPonteiro;
            tamanho++;
            return true;
        }

        Ponteiro<T> atual = inicio;
        int i = 0;

        while (atual != null && i < p - 1) {
            atual = atual.prox;
            i++;
        }

        if (atual == null) return false;

        novoPonteiro.prox = atual.prox;
        atual.prox = novoPonteiro;
        tamanho++;
        return true;
    }

    public boolean remove(int p) {
        if (inicio == null || p < 0) return false;

        if (p == 0) {
            inicio = inicio.prox;
            tamanho--;
            return true;
        }

        Ponteiro<T> atual = inicio;
        int i = 0;

        while (atual.prox != null && i < p - 1) {
            atual = atual.prox;
            i++;
        }

        if (atual.prox == null) return false;

        atual.prox = atual.prox.prox;
        tamanho--;
        return true;
    }

    public boolean estaVazia() {
        return inicio == null;
    }

    public Ponteiro<T> getInicio() {
        return inicio;
    }
    public T primeiroElemento() {
        if (inicio == null) {
            throw new RuntimeException("Lista vazia.");
        }
        return inicio.getElemento();
    }

    public void inserirFim(T elemento) {
        Ponteiro<T> novo = new Ponteiro<>(elemento);
        if (inicio == null) {
            inicio = novo;
        } else {
            Ponteiro<T> atual = inicio;
            while (atual.prox != null) {
                atual = atual.prox;
            }
            atual.prox = novo;
        }
        tamanho++;
    }
    public boolean removerValor(T valor) {
        if (inicio == null) return false;

        if (inicio.elemento.equals(valor)) {
            inicio = inicio.prox;
            tamanho--;
            return true;
        }

        Ponteiro<T> atual = inicio;
        while (atual.prox != null && !atual.prox.elemento.equals(valor)) {
            atual = atual.prox;
        }

        if (atual.prox == null) return false;

        atual.prox = atual.prox.prox;
        tamanho--;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Ponteiro<T> atual = inicio;
        while (atual != null) {
            sb.append(atual.getElemento());
            if (atual.getProximo() != null) {
                sb.append(", ");
            }
            atual = atual.getProximo();
        }
        sb.append("]");
        return sb.toString();
    }

}


