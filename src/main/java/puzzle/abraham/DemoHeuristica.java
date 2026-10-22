package puzzle.abraham;

import puzzle.model.Board;

/** Ejecutar esta Main para ver el calculo de H3 sin iniciar agentes. */
public final class DemoHeuristica {

    public static void main(String[] args) {
        String texto = args.length == 0 ? "213456870" : args[0].replace("--board=", "");
        Board tablero = new Board(texto);

        System.out.println("HEURISTICA H3");
        mostrarTablero(tablero);

        int manhattan = LinearConflict.calcularManhattan(tablero);
        int conflictos = LinearConflict.contarConflictosIndependientes(tablero);
        int h3 = LinearConflict.estimate(tablero);

        System.out.println("Manhattan: " + manhattan);
        System.out.println("Conflictos independientes: " + conflictos);
        System.out.println("H3 = " + manhattan + " + 2 * " + conflictos + " = " + h3);
    }

    /** Se reutiliza solamente para presentar tableros en la terminal. */
    public static void mostrarTablero(Board tablero) {
        for (int fila = 0; fila < 3; fila++) {
            for (int columna = 0; columna < 3; columna++) {
                int ficha = tablero.at(fila * 3 + columna);
                System.out.print((ficha == 0 ? "_" : Integer.toString(ficha)) + " ");
            }
            System.out.println();
        }
    }
}
