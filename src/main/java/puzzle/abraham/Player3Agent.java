package puzzle.abraham;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import puzzle.model.Board;
import java.util.List;

/** Agente real de JADE: recibe REQUEST y devuelve INFORM o FAILURE. */
public final class Player3Agent extends Agent {

    private Board tableroInicial;
    private int limite;
    private ResultadoBusqueda resultado;

    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage solicitud = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
                if (solicitud == null) {
                    block();
                    return;
                }

                try {
                    pensar(solicitud);
                    razonar();
                } catch (Exception error) {
                    resultado = new ResultadoBusqueda(false, error.getMessage(), List.of(), 0);
                }
                actuar(solicitud);
            }
        });
    }

    /** Interpreta los datos de la peticion. */
    private void pensar(ACLMessage solicitud) {
        System.out.println("jugador3 - pensar: interpretar tablero y limite recibidos por REQUEST.");
        tableroInicial = new Board(solicitud.getContent());
        limite = Integer.parseInt(solicitud.getUserDefinedParameter("limite"));
    }

    /** Ejecuta el motor Java; la traza aparece en la consola de IntelliJ. */
    private void razonar() {
        System.out.println("jugador3 - razonar: resolver con Greedy BFS y H3.");
        resultado = DemoGreedy.ejecutar(tableroInicial, limite);
    }

    /** Contesta al agente solicitante usando los performativos de JADE. */
    private void actuar(ACLMessage solicitud) {
        ACLMessage respuesta = solicitud.createReply();
        int tipo = resultado.resuelto() ? ACLMessage.INFORM : ACLMessage.FAILURE;
        respuesta.setPerformative(tipo);

        try {
            respuesta.setContentObject(resultado);
        } catch (java.io.IOException error) {
            throw new IllegalStateException("No se pudo preparar la respuesta.", error);
        }

        System.out.println("jugador3 - actuar: enviar " + ACLMessage.getPerformative(tipo) + ".");
        send(respuesta);
    }
}
