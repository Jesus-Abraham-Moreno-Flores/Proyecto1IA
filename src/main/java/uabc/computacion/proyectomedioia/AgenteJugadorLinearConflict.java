package uabc.computacion.proyectomedioia;

import puzzle.model.Board;
import java.util.List;

public class AgenteJugadorLinearConflict extends AgenteJugador {

    @Override
    protected ResultadoBusqueda razonar(Board estadoInicial) {
        GreedySearch buscador = new GreedySearch(estadoInicial,
                board -> LinearConflict.estimate(board));

        long tiempoInicio = System.currentTimeMillis();
        List<Board> solucion = buscador.resolver();
        long tiempoTotal = System.currentTimeMillis() - tiempoInicio;

        boolean resuelto = !solucion.isEmpty();
        String detalle = tiempoTotal + " ms";

        // Instanciación adecuada para los 4 campos del record: (resuelto, detalle, ruta, iteraciones)
        return new ResultadoBusqueda(resuelto, detalle, solucion, buscador.expanded());
    }
}