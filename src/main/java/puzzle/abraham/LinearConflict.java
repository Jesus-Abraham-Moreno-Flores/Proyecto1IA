package puzzle.abraham;

import puzzle.model.Board;

import java.util.ArrayList;
import java.util.List;

/** H3 = Manhattan + dos movimientos por conflicto lineal independiente. */
public final class LinearConflict {

    private record Conflicto(int primeraFicha, int segundaFicha) { }

    private LinearConflict() {
    }

    public static int estimate(Board tablero) {
        return calcularManhattan(tablero) + 2 * contarConflictosIndependientes(tablero);
    }

    public static int contarConflictosIndependientes(Board tablero) {
        List<Conflicto> conflictos = encontrarConflictos(tablero);
        boolean[] fichasUtilizadas = new boolean[9];
        return seleccionarConflictos(conflictos, 0, fichasUtilizadas);
    }

    public static int calcularManhattan(Board tablero) {
        int distancia = 0;

        for (int posicion = 0; posicion < 9; posicion++) {
            int ficha = tablero.at(posicion);
            if (ficha == 0) {
                continue;
            }

            int posicionMeta = ficha - 1;
            int diferenciaFilas = Math.abs(posicion / 3 - posicionMeta / 3);
            int diferenciaColumnas = Math.abs(posicion % 3 - posicionMeta % 3);
            distancia += diferenciaFilas + diferenciaColumnas;
        }
        return distancia;
    }

    private static List<Conflicto> encontrarConflictos(Board tablero) {
        List<Conflicto> conflictos = new ArrayList<>();

        for (int primeraPosicion = 0; primeraPosicion < 9; primeraPosicion++) {
            for (int segundaPosicion = primeraPosicion + 1; segundaPosicion < 9; segundaPosicion++) {
                int primeraFicha = tablero.at(primeraPosicion);
                int segundaFicha = tablero.at(segundaPosicion);
                if (primeraFicha == 0 || segundaFicha == 0) {
                    continue;
                }

                int primeraMeta = primeraFicha - 1;
                int segundaMeta = segundaFicha - 1;

                boolean mismaFila = primeraPosicion / 3 == segundaPosicion / 3;
                boolean ambasEnFilaMeta = primeraMeta / 3 == primeraPosicion / 3
                        && segundaMeta / 3 == primeraPosicion / 3;
                boolean columnasInvertidas = primeraMeta % 3 > segundaMeta % 3;

                boolean mismaColumna = primeraPosicion % 3 == segundaPosicion % 3;
                boolean ambasEnColumnaMeta = primeraMeta % 3 == primeraPosicion % 3
                        && segundaMeta % 3 == primeraPosicion % 3;
                boolean filasInvertidas = primeraMeta / 3 > segundaMeta / 3;

                boolean conflictoEnFila = mismaFila && ambasEnFilaMeta && columnasInvertidas;
                boolean conflictoEnColumna = mismaColumna && ambasEnColumnaMeta && filasInvertidas;

                if (conflictoEnFila || conflictoEnColumna) {
                    conflictos.add(new Conflicto(primeraFicha, segundaFicha));
                }
            }
        }
        return conflictos;
    }

    /** Busca la mayor cantidad de pares que no compartan ninguna ficha. */
    private static int seleccionarConflictos(List<Conflicto> conflictos, int indice,
                                             boolean[] fichasUtilizadas) {
        if (indice == conflictos.size()) {
            return 0;
        }

        int cantidadSinEstePar = seleccionarConflictos(conflictos, indice + 1, fichasUtilizadas);
        Conflicto actual = conflictos.get(indice);

        if (fichasUtilizadas[actual.primeraFicha()] || fichasUtilizadas[actual.segundaFicha()]) {
            return cantidadSinEstePar;
        }

        fichasUtilizadas[actual.primeraFicha()] = true;
        fichasUtilizadas[actual.segundaFicha()] = true;
        int cantidadConEstePar = 1 + seleccionarConflictos(conflictos, indice + 1, fichasUtilizadas);
        fichasUtilizadas[actual.primeraFicha()] = false;
        fichasUtilizadas[actual.segundaFicha()] = false;

        return Math.max(cantidadSinEstePar, cantidadConEstePar);
    }
}
