import java.io.Serializable;

public class PainelElevador implements Serializable {
    private boolean botaoSubirAtivado;
    private boolean botaoDescerAtivado;

    public PainelElevador() {
        this.botaoSubirAtivado = false;
        this.botaoDescerAtivado = false;
    }

    public void pressionarSubir() {
        botaoSubirAtivado = true;
    }

    public void pressionarDescer() {
        botaoDescerAtivado = true;
    }

    public void resetar() {
        botaoSubirAtivado = false;
        botaoDescerAtivado = false;
    }

    public boolean isBotaoSubirAtivado() {
        return botaoSubirAtivado;
    }

    public boolean isBotaoDescerAtivado() {
        return botaoDescerAtivado;
    }

    public void desativarBotoes() {
        botaoSubirAtivado = false;
        botaoDescerAtivado = false;
    }
}