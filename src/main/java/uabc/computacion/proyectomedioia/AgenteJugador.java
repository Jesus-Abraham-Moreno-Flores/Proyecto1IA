package uabc.computacion.proyectomedioia;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import puzzle.model.Board;
import java.util.List;

public class AgenteJugador extends Agent {

    @Override
    protected void setup() {
        System.out.println("Jugador listo en plataforma: " + getLocalName());

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();

                if (msg != null) {
                    String contenido = msg.getContent();

                    if (contenido.startsWith("competencia_iniciada|")) {
                        String strTableroGUI = contenido.split("\\|")[1];
                        enviarMensajeGUI("¡Recibí el tablero! Iniciando búsqueda...", msg.getSender());

                        String formatoAbraham = strTableroGUI.replace(",", "");
                        Board estadoInicial = new Board(formatoAbraham);

                        int[] estadoMeta = {1, 2, 3, 4, 5, 6, 7, 8, 0};

                        HeuriPiezaFuera heuristica1 = new HeuriPiezaFuera();
                        HeuriManhattan heuristica2 = new HeuriManhattan();

                        GreedySearch buscador = switch (getLocalName()) {
                            case "jugador1" -> new GreedySearch(estadoInicial, board -> heuristica1.calcular(extraerArreglo(board), estadoMeta));
                            case "jugador2" -> new GreedySearch(estadoInicial, board -> heuristica2.calcular(extraerArreglo(board), estadoMeta));
                            // CORRECCIÓN: Se actualizó el nombre del método a "estimate" según la clase LinearConflict
                            case "jugador3" -> new GreedySearch(estadoInicial, board -> LinearConflict.estimate(board));
                            default -> null;
                        };

                        if (buscador != null) {
                            long tiempoInicio = System.currentTimeMillis();
                            List<Board> solucion = buscador.resolver();
                            long tiempoTotal = System.currentTimeMillis() - tiempoInicio;

                            int nodosExpandidos = buscador.expanded();
                            int longitudCamino = solucion.isEmpty() ? 0 : solucion.size() - 1;

                            String msjResultados = String.format(
                                    "¡Meta encontrada! | Tiempo: %d ms | Expandidos: %d nodos | Pasos: %d",
                                    tiempoTotal, nodosExpandidos, longitudCamino
                            );
                            enviarMensajeGUI(msjResultados, msg.getSender());

                            if (!solucion.isEmpty()) {
                                StringBuilder rutaStr = new StringBuilder("ruta|");
                                for (Board b : solucion) {
                                    rutaStr.append(b.key()).append("-");
                                }
                                enviarMensajeGUI(rutaStr.toString(), msg.getSender());
                            }
                        }
                    }
                } else {
                    block();
                }
            }
        });
    }

    private int[] extraerArreglo(Board board) {
        int[] arreglo = new int[9];
        for (int i = 0; i < 9; i++) {
            arreglo[i] = board.at(i);
        }
        return arreglo;
    }

    private void enviarMensajeGUI(String texto, jade.core.AID receptorGUI) {
        ACLMessage respuesta = new ACLMessage(ACLMessage.INFORM);
        respuesta.addReceiver(receptorGUI);
        respuesta.setContent(texto);
        send(respuesta);
    }
}