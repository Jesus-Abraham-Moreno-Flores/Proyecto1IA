package uabc.computacion.proyectomedioia;

public class PruebaDiegoHeuris {
    public static void main(String[] args) {
        // Tablero de prueba (Fila por fila)
        // 3 2 1
        // 6 5 7
        // 8 4 0

        /**
         * 3: x=2 y=0
         * 1: x=2 y=0
         * 6: x=2 y=0
         * 7: x=2 y=1
         * 8: x=1 y=0
         * 4: x=1 y=1
         *  Manhattan = 12
         * */
        int[] estadoPrueba  = {3, 2, 1, 6, 5, 7, 8, 4, 0};
        int[] estadoObjetivo = {1, 2, 3, 4, 5, 6, 7, 8, 0};

        Heuristica h1 = new HeuriPiezaFuera();
        Heuristica h2 = new HeuriManhattan();

        System.out.println("=== PRUEBA INDIVIDUAL DE HEURÍSTICAS (Diego) ===");
        System.out.println("Estado Evaluado: [1, 2, 3, 4, 0, 6, 7, 5, 8]");
        System.out.println("Estado Objetivo: [1, 2, 3, 4, 5, 6, 7, 8, 0]\n");

        System.out.println("Jugador 1 - " + h1.getNombre() + ": h(n) = " + h1.calcular(estadoPrueba, estadoObjetivo));
        System.out.println("Jugador 2 - " + h2.getNombre() + ": h(n) = " + h2.calcular(estadoPrueba, estadoObjetivo));

    }
}
