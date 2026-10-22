package puzzle.abraham;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import puzzle.model.PathValidator;
import puzzle.model.Board;
import java.util.UUID;

/** Solicitante minimo para demostrar comunicacion real con jugador3. */
public final class AgenteConsola extends Agent {

    private final String conversacion = UUID.randomUUID().toString();
    private Board inicio;
    private ResultadoBusqueda resultado;

    @Override
    protected void setup() {
        inicio = new Board((String) getArguments()[0]);
        int limite = (Integer) getArguments()[1];
        solicitarResolucion(limite);

        MessageTemplate respuestaEsperada = MessageTemplate.and(
                MessageTemplate.MatchSender(new AID("jugador3", AID.ISLOCALNAME)),
                MessageTemplate.MatchConversationId(conversacion));

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage respuesta = receive(respuestaEsperada);
                if (respuesta == null) {
                    block();
                    return;
                }
                try {
                    pensar(respuesta);
                    razonar();
                    actuar();
                } catch (Exception error) {
                    System.err.println("Error de la demostracion: " + error.getMessage());
                    Main.cerrar(2);
                }
            }
        });

        addBehaviour(new WakerBehaviour(this, 30000) {
            @Override
            protected void onWake() {
                System.err.println("Tiempo de espera agotado para jugador3.");
                Main.cerrar(2);
            }
        });
    }

    private void solicitarResolucion(int limite) {
        ACLMessage solicitud = new ACLMessage(ACLMessage.REQUEST);
        solicitud.addReceiver(new AID("jugador3", AID.ISLOCALNAME));
        solicitud.setConversationId(conversacion);
        solicitud.setContent(inicio.key());
        solicitud.addUserDefinedParameter("limite", Integer.toString(limite));
        System.out.println("consola -> jugador3: REQUEST resolver " + inicio.key());
        send(solicitud);
    }

    private void pensar(ACLMessage respuesta) throws Exception {
        resultado = (ResultadoBusqueda) respuesta.getContentObject();
    }

    private void razonar() {
        if (resultado.resuelto()) {
            PathValidator.validate(inicio, resultado.ruta());
        }
        System.out.println();
        System.out.println("Resultado recibido: " + resultado.detalle());
        System.out.println("Iteraciones: " + resultado.iteraciones());
        if (resultado.resuelto()) {
            System.out.println("Movimientos: " + (resultado.ruta().size() - 1));
            System.out.println("Ruta: " + resultado.ruta());
        }
    }

    private void actuar() {
        System.out.println(resultado.resuelto() ? "AGENTE PASS" : "AGENTE SIN SOLUCION");
        doDelete();
        Main.cerrar(resultado.resuelto() ? 0 : 2);
    }
}
