public class Main {
    public static void main(String[] args) {
        Simulador simulador = new Simulador(5, 2, 1000);

        Predio predio = simulador.getPredio();


        simulador.agendarPessoa(new Pessoa(101, 0, 3), 0);
        simulador.agendarPessoa(new Pessoa(102, 1, 4), 3500);
        simulador.agendarPessoa(new Pessoa(103, 2, 0), 5000);
        simulador.agendarPessoa(new Pessoa(104, 3, 1), 7000);
        simulador.agendarPessoa(new Pessoa(105, 4, 2), 4000);
        simulador.agendarPessoa(new Pessoa(106, 0, 1), 6000);
        simulador.agendarPessoa(new Pessoa(107, 1, 3), 8000);
        simulador.agendarPessoa(new Pessoa(108, 2, 4), 1000);
        simulador.agendarPessoa(new Pessoa(109, 3, 0), 9000);
        simulador.agendarPessoa(new Pessoa(110, 4, 1), 3000);
        simulador.agendarPessoa(new Pessoa(111, 0, 4), 4500);
        simulador.agendarPessoa(new Pessoa(112, 1, 0), 7500);
        simulador.agendarPessoa(new Pessoa(113, 2, 3), 9500);
        simulador.agendarPessoa(new Pessoa(114, 3, 2), 1500);
        simulador.agendarPessoa(new Pessoa(115, 4, 0), 8500);



        simulador.iniciar();

    }
}
