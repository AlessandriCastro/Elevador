public class Main {
    public static void main(String[] args) {
        Simulador simulador = new Simulador(5, 1, 1000);

        Predio predio = simulador.getPredio();

        Pessoa p1 = new Pessoa(11, 0, 3);
        Pessoa p2 = new Pessoa(22, 0, 4);
        Pessoa p3 = new Pessoa(33, 2, 1);
        Pessoa p4 = new Pessoa(44, 1, 4);
        Pessoa p5 = new Pessoa(101, 0, 2);  // Vai do 0 ao 3
        Pessoa p6 = new Pessoa(102, 0, 4);  // Vai do 0 ao 5
        Pessoa p7 = new Pessoa(103, 1, 0);  // Vai do 1 ao 0
        Pessoa p8 = new Pessoa(104, 2, 4);  // Vai do 2 ao 4
        Pessoa p9 = new Pessoa(105, 3, 1);  // Vai do 3 ao 2
        Pessoa p10 = new Pessoa(106, 4, 0);  // Vai do 5 ao 1


        predio.getAndar(0).adicionarPessoa(p1);
        predio.getAndar(0).adicionarPessoa(p2);
        predio.getAndar(2).adicionarPessoa(p3);
        predio.getAndar(1).adicionarPessoa(p4);
        predio.getAndar(0).adicionarPessoa(p5);
        predio.getAndar(0).adicionarPessoa(p6);
        predio.getAndar(1).adicionarPessoa(p7);
        predio.getAndar(2).adicionarPessoa(p8);
        predio.getAndar(3).adicionarPessoa(p9);
        predio.getAndar(4).adicionarPessoa(p10);

        simulador.iniciar();

    }
}
