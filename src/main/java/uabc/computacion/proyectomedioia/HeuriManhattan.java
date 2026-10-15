package uabc.computacion.proyectomedioia;

public class HeuriManhattan implements Heuristica {

    @Override
    public int calcular(int[] estadoActual, int[] estadoObjetivo) {
        int h = 0;
        for (int i = 0; i < 9; i++) {
            int valor = estadoActual[i];

            // Ignorar el espacio en blanco
            if (valor != 0) {
                int posObjetivo = buscarPosicion(estadoObjetivo, valor);

                // Convertir índice 1D a coordenadas 2D (fila, columna)
                int filaActual = i / 3;
                int colActual = i % 3;
                int filaObjetivo = posObjetivo / 3;
                int colObjetivo = posObjetivo % 3;

                h += Math.abs(filaActual - filaObjetivo) + Math.abs(colActual - colObjetivo);
            }
        }
        return h;
    }

    private int buscarPosicion(int[] arreglo, int valor) {
        for (int i = 0; i < arreglo.length; i++) {
            if (arreglo[i] == valor) return i;
        }
        return -1;
    }

    @Override
    public String getNombre() {
        return "Distancia de Manhattan (h2)";
    }
}
