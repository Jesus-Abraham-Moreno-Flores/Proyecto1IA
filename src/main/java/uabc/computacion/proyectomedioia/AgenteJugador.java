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

            // Apuntamos a la GUI local asociada a este agente (gui_jugador1, gui_jugador2, etc.)
            AID miGuiLocal = new AID("gui_" + getLocalName(), AID.ISLOCALNAME);
            enviarMensajeGUI("¡Recibí el tablero! Iniciando búsqueda...", miGuiLocal);

            String formatoPuzzle = strTableroGUI.replace(",", "").trim();
            Board estadoInicial = new Board(formatoPuzzle);

            // Fase 2: Razonar (retorna el record ResultadoBusqueda)
            ResultadoBusqueda resultado = razonar(estadoInicial);

            // Fase 3: Actuar (envía el resultado a la GUI local)
            if (resultado != null) {
                actuar(miGuiLocal, resultado);
            }
        }
    }

    /**
     * RAZONAR: Método abstracto donde cada agente aplicará su heurística.
     */
    protected abstract ResultadoBusqueda razonar(Board estadoInicial);

    /**
     * ACTUAR: Formatea y envía las métricas y la ruta de animación a la GUI local.
     */
    protected void actuar(AID receptorGUI, ResultadoBusqueda resultado) {
        List<Board> solucion = resultado.ruta();
        int pasos = solucion.isEmpty() ? 0 : solucion.size() - 1;

        // Extraer tiempo en milisegundos desde el detalle ("12 ms" -> 12)
        long tiempoMs = 0;
        try {
            tiempoMs = Long.parseLong(resultado.detalle().replace(" ms", "").trim());
        } catch (Exception ignored) {}

        // 1. Enviar métricas estructuradas para el Dashboard
        String msjMetricas = String.format("metricas|%s|%d|%d|%d",
                getLocalName(), tiempoMs, resultado.iteraciones(), pasos);
        enviarMensajeGUI(msjMetricas, receptorGUI);

        // 2. Enviar texto descriptivo para la barra de estado inferior
        String msjResultados = String.format(
                "¡Meta encontrada! | Tiempo: %s | Expandidos: %d nodos | Pasos: %d",
                resultado.detalle(), resultado.iteraciones(), pasos
        );
        enviarMensajeGUI(msjResultados, receptorGUI);

        // 3. Enviar la ruta completa para animar los movimientos en la pantalla local
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