package uabc.computacion.proyectomedioia;

public class HeuriPiezaFuera implements Heuristica {

    @Override
    public int calcular(int[] estadoActual, int[] estadoObjetivo) {
        int h = 0;
        for (int i = 0; i < estadoActual.length; i++) {
            // No se contabiliza el espacio vacío (0)
            if (estadoActual[i] != 0 && estadoActual[i] != estadoObjetivo[i]) {
                h++;
            }
        }
        return h;
    }
    //nada
    @Override
    public String getNombre() {
        return "Piezas Fuera de Lugar (h1)";
    }
}
