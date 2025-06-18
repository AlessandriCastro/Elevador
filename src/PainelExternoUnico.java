public class PainelExternoUnico extends PainelExterno {
    public PainelExternoUnico(Andar andar) {
        super(andar);
    }

    @Override
    public void chamarElevador() {
        this.chamadaAtiva = true;
        System.out.printf("🔔 Painel Único: Chamada ativada no andar %d\n", andar.getNumero());
    }

    @Override
    public void cancelarChamada() {
        this.chamadaAtiva = false;
        System.out.printf("🔕 Painel Único: Chamada cancelada no andar %d\n", andar.getNumero());
    }

    @Override
    public String getTipoPainel() {
        return "Único";
    }

    @Override
    public String getStatus() {
        return chamadaAtiva ? "��" : "🔕";
    }
} 