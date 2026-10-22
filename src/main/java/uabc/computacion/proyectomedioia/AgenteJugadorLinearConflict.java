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

        return new ResultadoBusqueda(solucion, tiempoTotal, buscador.expanded());
    }
}