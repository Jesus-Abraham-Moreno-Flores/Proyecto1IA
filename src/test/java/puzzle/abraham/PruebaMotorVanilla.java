package puzzle.abraham;

import puzzle.model.Board;
import puzzle.model.PathValidator;
import uabc.computacion.proyectomedioia.GreedySearch;
import uabc.computacion.proyectomedioia.LinearConflict;

import java.util.List;

/** Ejecutable con javac y java: no necesita JADE, JavaFX ni Maven. */
public final class PruebaMotorVanilla {

    public static void main(String[] args) {
        List<String> ejemplos = List.of("123456780", "123406758", "724506831", "867254301");

        for (String ejemplo : ejemplos) {
            Board inicio = new Board(ejemplo);
            GreedySearch busqueda = new GreedySearch(inicio, LinearConflict::estimate);
            List<Board> solucion = busqueda.resolver();
            PathValidator.validate(inicio, solucion);

            if (ejemplo.equals("123406758") && solucion.size() != 3) {
                throw new AssertionError("El ejemplo sencillo debe resolverse en dos movimientos.");
            }
            System.out.println("Motor Java: " + ejemplo + " -> " + (solucion.size() - 1) + " movimientos");
        }

        System.out.println("MOTOR VANILLA PASS: cuatro rutas verificadas sin iniciar frameworks.");
    }
}
