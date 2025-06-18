import java.util.Scanner;

public class SeletorPainelExterno {
    private static final String[] OPCOES = {
        "1 - Painel Único (botão geral)",
        "2 - Painel Subir/Descer",
        "3 - Painel Numérico"
    };

    public static String selecionarPainel() {
        Scanner scanner = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("\n=== SELECIONE O TIPO DE PAINEL EXTERNO ===");
            for (String op : OPCOES) {
                System.out.println(op);
            }
            System.out.print("\nDigite sua escolha (1-3): ");
            
            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                opcao = 0;
            }

            if (opcao < 1 || opcao > 3) {
                System.out.println("❌ Opção inválida! Por favor, escolha entre 1 e 3.");
            }
        } while (opcao < 1 || opcao > 3);

        switch (opcao) {
            case 1:
                return "Unico";
            case 2:
                return "SubirDescer";
            case 3:
                return "Numerico";
            default:
                return "Unico";
        }
    }
} 