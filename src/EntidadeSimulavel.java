import java.io.Serializable;


public abstract class EntidadeSimulavel implements Serializable {
    public abstract void atualizar(int minutoSimulado);
}