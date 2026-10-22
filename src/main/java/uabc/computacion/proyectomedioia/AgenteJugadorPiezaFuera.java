package uabc.computacion.proyectomedioia;

import puzzle.model.Board;
import java.util.List;

public class AgenteJugadorPiezaFuera extends AgenteJugador {

    @Override
    protected ResultadoBusqueda razonar(Board estadoInicial) {
        HeuriPiezaFuera heuristica = new HeuriPiezaFuera();
        GreedySearch buscador = new GreedySearch(estadoInicial,
                board -> heuristica.calcular(extraerArreglo(board), estadoMeta));

        long tiempoInicio = System.currentTimeMillis();
        List<Board> solucion = buscador.resolver();
        long tiempoTotal = System.currentTimeMillis() - tiempoInicio;

        return new ResultadoBusqueda(solucion, tiempoTotal, buscador.expanded());
    }
}