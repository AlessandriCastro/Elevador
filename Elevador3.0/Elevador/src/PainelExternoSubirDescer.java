public class PainelExternoSubirDescer extends PainelExterno {
    private boolean chamadaSubir;
    private boolean chamadaDescer;

    public PainelExternoSubirDescer(Andar andar) {
        super(andar);
        this.chamadaSubir = false;
        this.chamadaDescer = false;
    }

    @Override
    public void chamarElevador() {
        // Este método não é usado neste tipo de painel
    }

    public void chamarSubir() {
        this.chamadaSubir = true;
        this.chamadaAtiva = true;
        System.out.printf("⬆️ Painel Subir/Descer: Chamada para subir ativada no andar %d\n", andar.getNumero());
    }

    public void chamarDescer() {
        this.chamadaDescer = true;
        this.chamadaAtiva = true;
        System.out.printf("⬇️ Painel Subir/Descer: Chamada para descer ativada no andar %d\n", andar.getNumero());
    }

    @Override
    public void cancelarChamada() {
        this.chamadaSubir = false;
        this.chamadaDescer = false;
        this.chamadaAtiva = false;
        System.out.printf("🔕 Painel Subir/Descer: Todas as chamadas canceladas no andar %d\n", andar.getNumero());
    }

    public boolean isChamadaSubir() {
        return chamadaSubir;
    }

    public boolean isChamadaDescer() {
        return chamadaDescer;
    }

    @Override
    public String getTipoPainel() {
        return "Subir/Descer";
    }

    @Override
    public String getStatus() {
        if (!chamadaAtiva) return "🔕";
        if (chamadaSubir && chamadaDescer) return "⬆️⬇️";
        if (chamadaSubir) return "⬆️";
        return "⬇️";
    }
} 