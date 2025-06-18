public abstract class PainelExterno {
    protected Andar andar;
    protected boolean chamadaAtiva;

    public PainelExterno(Andar andar) {
        this.andar = andar;
        this.chamadaAtiva = false;
    }

    public abstract void chamarElevador();
    public abstract void cancelarChamada();
    public abstract String getTipoPainel();
    public abstract String getStatus();

    public boolean isChamadaAtiva() {
        return chamadaAtiva;
    }

    public Andar getAndar() {
        return andar;
    }
} 