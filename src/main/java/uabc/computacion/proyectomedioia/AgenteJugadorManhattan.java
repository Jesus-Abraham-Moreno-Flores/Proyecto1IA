package uabc.computacion.proyectomedioia;

import puzzle.model.Board;
import java.util.List;

public class AgenteJugadorManhattan extends AgenteJugador {

    @Override
    protected ResultadoBusqueda razonar(Board estadoInicial) {
        HeuriManhattan heuristica = new HeuriManhattan();
        GreedySearch buscador = new GreedySearch(estadoInicial,
                board -> heuristica.calcular(extraerArreglo(board), estadoMeta));

        long tiempoInicio = System.currentTimeMillis();
        List<Board> solucion = buscador.resolver();
        long tiempoTotal = System.currentTimeMillis() - tiempoInicio;

        boolean resuelto = !solucion.isEmpty();
        String detalle = tiempoTotal + " ms";

        // Instanciación adaptada al Record: (resuelto, detalle, ruta, iteraciones)
        return new ResultadoBusqueda(resuelto, detalle, solucion, buscador.expanded());
    }
}