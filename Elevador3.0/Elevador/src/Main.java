import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimuladorGUI gui = new SimuladorGUI(12, 3, 1000); // 5 andares, 2 elevadores, 1 segundo por ciclo
            gui.setVisible(true);
        });
    }
}

