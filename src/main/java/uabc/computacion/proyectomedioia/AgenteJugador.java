package uabc.computacion.proyectomedioia;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import puzzle.model.Board;
import java.util.List;

public abstract class AgenteJugador extends Agent {

    protected final int[] estadoMeta = {1, 2, 3, 4, 5, 6, 7, 8, 0};

    @Override
    protected void setup() {
        System.out.println("Jugador listo en plataforma: " + getLocalName());

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null) {
                    percibir(msg);
                } else {
                    block();
                }
            }
        });
    }

    /**
     * PERCIBIR: Procesa la entrada del mensaje ACL y extrae el estado inicial del tablero.
     */
    protected void percibir(ACLMessage msg) {
        String contenido = msg.getContent();

        if (contenido != null && contenido.startsWith("competencia_iniciada|")) {
            String strTableroGUI = contenido.split("\\|")[1];
            enviarMensajeGUI("¡Recibí el tablero! Iniciando búsqueda...", msg.getSender());

            String formatoAbraham = strTableroGUI.replace(",", "");
            Board estadoInicial = new Board(formatoAbraham);

            // Fase 2: Razonar (retorna el record ResultadoBusqueda)
            ResultadoBusqueda resultado = razonar(estadoInicial);

            // Fase 3: Actuar (envía la respuesta a la GUI)
            if (resultado != null) {
                actuar(msg.getSender(), resultado);
            }
        }
    }

    /**
     * RAZONAR: Método abstracto donde cada agente aplicará su heurística.
     */
    protected abstract ResultadoBusqueda razonar(Board estadoInicial);

    /**
     * ACTUAR: Formatea y envía los mensajes de respuesta hacia la GUI usando el Record.
     */
    protected void actuar(AID receptorGUI, ResultadoBusqueda resultado) {
        List<Board> solucion = resultado.ruta();
        int pasos = solucion.isEmpty() ? 0 : solucion.size() - 1;

        String msjResultados = String.format(
                "¡Meta encontrada! | Detalle: %s | Expandidos: %d nodos | Pasos: %d",
                resultado.detalle(), resultado.iteraciones(), pasos
        );
        enviarMensajeGUI(msjResultados, receptorGUI);

        if (resultado.resuelto() && !solucion.isEmpty()) {
            StringBuilder rutaStr = new StringBuilder("ruta|");
            for (Board b : solucion) {
                rutaStr.append(b.key()).append("-");
            }
            enviarMensajeGUI(rutaStr.toString(), receptorGUI);
        }
    }

    protected int[] extraerArreglo(Board board) {
        int[] arreglo = new int[9];
        for (int i = 0; i < 9; i++) {
            arreglo[i] = board.at(i);
        }
        return arreglo;
    }

    protected void enviarMensajeGUI(String texto, AID receptorGUI) {
        ACLMessage respuesta = new ACLMessage(ACLMessage.INFORM);
        respuesta.addReceiver(receptorGUI);
        respuesta.setContent(texto);
        send(respuesta);
    }
}