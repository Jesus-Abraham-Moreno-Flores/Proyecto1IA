package uabc.computacion.proyectomedioia;

import puzzle.model.Board;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.ToIntFunction;

/**
 * Greedy Best-First Search
 * La prioridad es solamente h(n)
 */
public final class GreedySearch {

    private static final class Nodo {
        private final Board estado;
        private final Nodo padre;
        private final int heuristica;
        private final long ordenDeLlegada;

        private Nodo(Board estado, Nodo padre, int heuristica, long ordenDeLlegada) {
            this.estado = estado;
            this.padre = padre;
            this.heuristica = heuristica;
            this.ordenDeLlegada = ordenDeLlegada;
        }
    }

    private final ToIntFunction<Board> calcularHeuristica;
    private final PriorityQueue<Nodo> frontera;
    private final Set<Board> descubiertos = new HashSet<>();
    private final Set<Board> explorados = new HashSet<>();

    private Nodo nodoActual;
    private long siguienteOrden;
    private int maximaFrontera = 1;

    public GreedySearch(Board estadoInicial, ToIntFunction<Board> calcularHeuristica) {
        this.calcularHeuristica = calcularHeuristica;

        Comparator<Nodo> prioridad = Comparator
                .comparingInt((Nodo nodo) -> nodo.heuristica)
                .thenComparingLong(nodo -> nodo.ordenDeLlegada);

        frontera = new PriorityQueue<>(prioridad);
        agregarNodo(estadoInicial, null);
    }

    public Board next() {
        if (nodoActual != null) {
            throw new IllegalStateException("Falta incorporar los sucesores del nodo actual.");
        }

        nodoActual = frontera.poll();
        if (nodoActual == null) {
            return null;
        }

        explorados.add(nodoActual.estado);
        return nodoActual.estado;
    }

    public void accept(List<Board> sucesores) {
        if (nodoActual == null) {
            throw new IllegalStateException("No hay un nodo seleccionado.");
        }

        for (Board sucesor : sucesores) {
            if (!descubiertos.contains(sucesor)) {
                agregarNodo(sucesor, nodoActual);
            }
        }

        maximaFrontera = Math.max(maximaFrontera, frontera.size());
        nodoActual = null;
    }

    private void agregarNodo(Board estado, Nodo padre) {
        int valorHeuristico = calcularHeuristica.applyAsInt(estado);
        Nodo nuevoNodo = new Nodo(estado, padre, valorHeuristico, siguienteOrden);

        siguienteOrden++;
        descubiertos.add(estado);
        frontera.add(nuevoNodo);
    }

    public List<Board> solution() {
        if (nodoActual == null || !nodoActual.estado.isGoal()) {
            throw new IllegalStateException("Todavia no se ha encontrado la meta.");
        }

        List<Board> caminoInvertido = new ArrayList<>();
        Nodo nodo = nodoActual;
        while (nodo != null) {
            caminoInvertido.add(nodo.estado);
            nodo = nodo.padre;
        }

        List<Board> camino = new ArrayList<>();
        for (int posicion = caminoInvertido.size() - 1; posicion >= 0; posicion--) {
            camino.add(caminoInvertido.get(posicion));
        }
        return List.copyOf(camino);
    }

    public List<Board> resolver() {
        int iteracion = 1;
        Board estado = next();

        while (estado != null) {
            if (estado.isGoal()) {
                return solution();
            }

            List<Board> hijos = estado.neighbors();
            accept(hijos);
            Board siguienteEstado = next();

            // Impresión en consola exclusivamente para las primeras 3 iteraciones
            if (iteracion <= 3) {
                System.out.println("--- Iteración " + iteracion + " ---");
                System.out.println("Estado analizado: " + estado.key());
                System.out.println("Hijos generados:");
                for (Board hijo : hijos) {
                    System.out.println("   -> " + hijo.key());
                }
                if (siguienteEstado != null) {
                    System.out.println(">> Nodo seleccionado para expandir (menor costo global): " + siguienteEstado.key());
                }
                System.out.println("----------------------\n");
            }

            estado = siguienteEstado;
            iteracion++;
        }
        return List.of();
    }

    public int expanded() {
        return explorados.size();
    }

    public int generated() {
        return descubiertos.size();
    }

    public int frontier() {
        return frontera.size();
    }

    public int maxFrontier() {
        return maximaFrontera;
    }
}