package puzzle.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Comprueba una ruta completa antes de aceptarla como solucion. */
public final class PathValidator {

    private PathValidator() {
    }

    public static void validate(Board inicio, List<Board> ruta) {
        if (ruta == null || ruta.isEmpty()) {
            throw new IllegalArgumentException("La ruta esta vacia.");
        }
        if (!ruta.get(0).equals(inicio) || !ruta.get(ruta.size() - 1).isGoal()) {
            throw new IllegalArgumentException("La ruta debe ir del inicio a la meta.");
        }

        Set<Board> visitados = new HashSet<>();
        Board anterior = null;
        for (Board estado : ruta) {
            if (!estado.isSolvable() || !visitados.add(estado)) {
                throw new IllegalArgumentException("La ruta contiene un estado insoluble o repetido.");
            }
            if (anterior != null && !anterior.neighbors().contains(estado)) {
                throw new IllegalArgumentException("La ruta contiene un movimiento ilegal.");
            }
            anterior = estado;
        }
    }
}
