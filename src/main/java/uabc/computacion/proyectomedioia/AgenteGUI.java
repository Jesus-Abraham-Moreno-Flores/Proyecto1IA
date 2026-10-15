package uabc.computacion.proyectomedioia;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class AgenteGUI extends Agent {
    private final TextArea textArea;

    public AgenteGUI(TextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    protected void setup() {
        System.out.println("Agente GUI iniciado correctamente.");

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null) {
                    String emisor = msg.getSender().getLocalName();
                    String contenido = msg.getContent();

                    Platform.runLater(() -> {
                        if (textArea != null) {
                            textArea.appendText("Respuesta de [" + emisor + "]: " + contenido + "\n");
                        }
                    });
                } else {
                    block();
                }
            }
        });
    }

    // Comentario Prueba

    public void enviarNotificacion() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent("competencia iniciada");

        msg.addReceiver(new AID("jugador1", AID.ISLOCALNAME));
        msg.addReceiver(new AID("jugador2", AID.ISLOCALNAME));
        msg.addReceiver(new AID("jugador3", AID.ISLOCALNAME));

        send(msg);
    }
}