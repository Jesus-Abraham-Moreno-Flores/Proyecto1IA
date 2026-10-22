package puzzle.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Tablero inmutable de nueve casillas. El cero representa el hueco. */
public final class Board implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final Board GOAL = new Board("123456780");

    // Se conserva el nombre de este campo para mantener la serializacion compartida.
    private final String key;

    public Board(String casillas) {
        validarCasillas(casillas);
        key = casillas;
    }

    private static void validarCasillas(String casillas) {
        if (casillas == null || casillas.length() != 9) {
            throw new IllegalArgumentException("El tablero debe contener las nueve fichas de 0 a 8.");
        }

        boolean[] encontradas = new boolean[9];
        for (int posicion = 0; posicion < 9; posicion++) {
            int ficha = casillas.charAt(posicion) - '0';
            if (ficha < 0 || ficha > 8 || encontradas[ficha]) {
                throw new IllegalArgumentException("Use las fichas 0 a 8 sin repetir.");
            }
            encontradas[ficha] = true;
        }
    }

    public String key() {
        return key;
    }

    public int at(int posicion) {
        return key.charAt(posicion) - '0';
    }

    public boolean isGoal() {
        return equals(GOAL);
    }

    /** Cuenta parejas de fichas en orden invertido, excluyendo el hueco. */
    public int inversions() {
        int inversiones = 0;
        for (int primera = 0; primera < 9; primera++) {
            for (int segunda = primera + 1; segunda < 9; segunda++) {
                if (at(primera) != 0 && at(segunda) != 0 && at(primera) > at(segunda)) {
                    inversiones++;
                }
            }
        }
        return inversiones;
    }

    public boolean isSolvable() {
        return inversions() % 2 == 0;
    }

    /** Orden fijo del hueco: arriba, abajo, izquierda y derecha. */
    public List<Board> neighbors() {
        int hueco = key.indexOf('0');
        int fila = hueco / 3;
        int columna = hueco % 3;
        List<Board> vecinos = new ArrayList<>(4);

        if (fila > 0) {
            vecinos.add(intercambiar(hueco, hueco - 3));
        }
        if (fila < 2) {
            vecinos.add(intercambiar(hueco, hueco + 3));
        }
        if (columna > 0) {
            vecinos.add(intercambiar(hueco, hueco - 1));
        }
        if (columna < 2) {
            vecinos.add(intercambiar(hueco, hueco + 1));
        }
        return vecinos;
    }

    private Board intercambiar(int hueco, int destino) {
        char[] casillas = key.toCharArray();
        casillas[hueco] = casillas[destino];
        casillas[destino] = '0';
        return new Board(new String(casillas));
    }

    /** Mezcla por movimientos legales; evita deshacer inmediatamente la ultima jugada. */
    public static Board scramble(int movimientos, long semilla) {
        if (movimientos < 0 || movimientos > 10000) {
            throw new IllegalArgumentException("La mezcla debe tener entre 0 y 10000 movimientos.");
        }

        Random aleatorio = new Random(semilla);
        Board actual = GOAL;
        Board anterior = null;
        for (int paso = 0; paso < movimientos; paso++) {
            List<Board> vecinos = actual.neighbors();
            vecinos.remove(anterior);
            anterior = actual;
            actual = vecinos.get(aleatorio.nextInt(vecinos.size()));
        }
        return actual;
    }

    @Override
    public boolean equals(Object objeto) {
        return objeto instanceof Board otroTablero && key.equals(otroTablero.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return key;
    }
}
