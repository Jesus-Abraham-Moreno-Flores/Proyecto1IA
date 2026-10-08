package uabc.computacion.proyectomedioia;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Random;

public class AgenteJugador extends Agent {
    private static final String[] RESPUESTAS = {
            "no quiero jugar",
            "estoy listo",
            "no sé las reglas del juego"
    };

    private final Random random = new Random();

    @Override
    protected void setup() {
        System.out.println("Agente Jugador " + getLocalName() + " está en línea.");

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null) {
                    if ("competencia iniciada".equalsIgnoreCase(msg.getContent())) {
                        String respuestaElegida = RESPUESTAS[random.nextInt(RESPUESTAS.length)];

                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent(respuestaElegida);
                        send(reply);
                    }
                } else {
                    block();
                }
            }
        });
    }
}