public class PainelExternoNumerico extends PainelExterno {
    private int andarDestino;

    public PainelExternoNumerico(Andar andar) {
        super(andar);
        this.andarDestino = -1;
    }

    @Override
    public void chamarElevador() {
        // Este método não é usado neste tipo de painel
    }

    public void chamarParaAndar(int andarDestino) {
        if (andarDestino >= 0 && andarDestino != andar.getNumero()) {
            this.andarDestino = andarDestino;
            this.chamadaAtiva = true;
            System.out.printf("🔢 Painel Numérico: Chamada para andar %d ativada no andar %d\n", 
                andarDestino, andar.getNumero());
        }
    }

    @Override
    public void cancelarChamada() {
        this.andarDestino = -1;
        this.chamadaAtiva = false;
        System.out.printf("🔕 Painel Numérico: Chamada cancelada no andar %d\n", andar.getNumero());
    }

    public int getAndarDestino() {
        return andarDestino;
    }

    @Override
    public String getTipoPainel() {
        return "Numérico";
    }

    @Override
    public String getStatus() {
        if (!chamadaAtiva) return "🔕";
        return "🔢" + andarDestino;
    }
} 