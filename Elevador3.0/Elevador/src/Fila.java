public class Fila<T> {
    private static class No<T> {
        T valor;
        No<T> proximo;

        No(T valor) {
            this.valor = valor;
            this.proximo = null;
        }
    }

    private No<T> head;
    private No<T> tail;
    private int tamanho;

    public Fila() {
        this.head = null;
        this.tail = null;
        this.tamanho = 0;
    }

    public boolean estaVazia() {
        return head == null;
    }

    public void enqueue(T valor) {
        No<T> novo = new No<T>(valor);
        if (estaVazia()) {
            head = novo;
            tail = novo;
        } else {
            tail.proximo = novo;
            tail = novo;
        }
        tamanho++;
    }

    public T dequeue() {
        if (estaVazia()) {
            throw new RuntimeException("A fila está vazia (underflow).");
        }
        T valorRemovido = tail.valor;

        if (head == tail) {
            head = null;
            tail = null;
        } else {
            No<T> atual = head;
            while (atual.proximo != tail) {
                atual = atual.proximo;
            }
            atual.proximo = null;
            tail = atual;
        }
        tamanho--;
        return valorRemovido;
    }

    public T primeiro() {
        if (estaVazia()) {
            throw new RuntimeException("A fila está vazia.");
        }
        return tail.valor;
    }

    public int tamanho() {
        return tamanho;
    }
    @Override
    public String toString() {
        if (estaVazia()) {
            return "Fila vazia";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Fila: [");

        No<T> atual = head;
        while (atual != null) {
            sb.append(atual.valor);
            if (atual.proximo != null) {
                sb.append(", ");
            }
            atual = atual.proximo;
        }

        sb.append("]");
        return sb.toString();
    }

}
