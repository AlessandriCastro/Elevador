import java.io.Serializable;

public class Pessoa implements Serializable {
    private int id;
    private int andarOrigem;
    private int andarDestino;
    private boolean dentroElevador;

    public Pessoa(int id, int origem, int destino) {
        this.id = id;
        this.andarOrigem = origem;
        this.andarDestino = destino;
        this.dentroElevador = false;
    }

    public int getId() {
        return id;
    }

    public int getAndarOrigem() {
        return andarOrigem;
    }

    public int getAndarDestino() {
        return andarDestino;
    }

    public boolean estaDentroDoElevador() {
        return dentroElevador;
    }

    public void entrarElevador() {
        this.dentroElevador = true;
    }

    public void sairElevador() {
        this.dentroElevador = false;
    }
}

