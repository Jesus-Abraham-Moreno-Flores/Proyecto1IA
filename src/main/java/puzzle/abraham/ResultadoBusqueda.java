package puzzle.abraham;

import puzzle.model.Board;
import java.io.Serializable;
import java.util.List;

/** Datos que el jugador devuelve al agente de consola. No depende de JADE. */
public record ResultadoBusqueda(boolean resuelto, String detalle,
                                List<Board> ruta, int iteraciones) implements Serializable {
}
