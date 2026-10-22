package uabc.computacion.proyectomedioia;

import puzzle.model.Board;
import puzzle.model.PathValidator;

import java.util.List;

/** Demostracion paso a paso de Greedy BFS usando solo Java estandar. */
public final class DemoGreedy {

    public static void main(String[] args) {
        String texto = args.length == 0 ? "123406758" : args[0].replace("--board=", "");
        ResultadoBusqueda resultado = ejecutar(new Board(texto), 181440);
        System.out.println("Resultado: " + resultado.detalle());
        if (resultado.resuelto()) {
            System.out.println("Movimientos de la solucion: " + (resultado.ruta().size() - 1));
            System.out.println("Ruta: " + resultado.ruta());
        }
    }

    /** El agente utiliza esta misma demostracion para mostrar su razonamiento. */
    public static ResultadoBusqueda ejecutar(Board inicio, int limite) {
        if (!inicio.isSolvable()) {
            return new ResultadoBusqueda(false, "Tablero insoluble: inversiones impares.", List.of(), 0);
        }
        if (limite < 1 || limite > 181440) {
            throw new IllegalArgumentException("El limite debe estar entre 1 y 181440.");
        }

        GreedySearch busqueda = new GreedySearch(inicio, LinearConflict::estimate);
        System.out.println("GREEDY BFS - prioridad h(n), con desempate por orden de llegada");

        while (busqueda.expanded() < limite) {
            Board actual = busqueda.next();
            if (actual == null) {
                return new ResultadoBusqueda(false, "Frontera agotada.", List.of(), busqueda.expanded());
            }

            System.out.println();
            System.out.println("Iteracion " + busqueda.expanded() + " | H3 = " + LinearConflict.estimate(actual));
            DemoHeuristica.mostrarTablero(actual);

            if (actual.isGoal()) {
                List<Board> ruta = busqueda.solution();
                PathValidator.validate(inicio, ruta);
                return new ResultadoBusqueda(true, "Meta alcanzada; ruta validada.", ruta, busqueda.expanded());
            }

            List<Board> vecinos = actual.neighbors();
            System.out.print("Sucesores: ");
            for (Board vecino : vecinos) {
                System.out.print(vecino.key() + "(h=" + LinearConflict.estimate(vecino) + ") ");
            }
            System.out.println();
            busqueda.accept(vecinos);
        }

        return new ResultadoBusqueda(false, "LIMITE ALCANZADO; no implica insolubilidad.",
                List.of(), busqueda.expanded());
    }
}
