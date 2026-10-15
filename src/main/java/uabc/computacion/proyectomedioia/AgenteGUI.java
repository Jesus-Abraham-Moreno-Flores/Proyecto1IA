package uabc.computacion.proyectomedioia;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class AgenteGUI extends Agent {
    private MainApp vista; // Guardamos la referencia para inyectar datos a JavaFX

    public void setVista(MainApp vista) {
        this.vista = vista;
    }

    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null) {
                    String emisor = msg.getSender().getLocalName();
                    String contenido = msg.getContent();

                    // Si el que contesta es el generador, reconstruimos el byte[]
                    if (emisor.equals("generador") && msg.getPerformative() == ACLMessage.INFORM) {
                        String[] pedazos = contenido.split(",");
                        byte[] arregloTablero = new byte[9];

                        for (int i = 0; i < 9; i++) {
                            arregloTablero[i] = Byte.parseByte(pedazos[i]);
                        }

                        // Mandamos la orden a JavaFX de pintar las fichas
                        vista.pintarTablero(arregloTablero);

                        // Una vez generado, disparamos a los jugadores
                        arrancarCompetencia();
                    } else {
                        // Si es cualquier otro, solo mostramos el texto en consola
                        vista.agregarTextoPantalla(emisor, contenido);
                    }
                } else {
                    block();
                }
            }
        });
    }

    // Metodo que manda a llamar el boton de Iniciar
    public void pedirTablero() {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setContent("dame_tablero");
        msg.addReceiver(new AID("generador", AID.ISLOCALNAME));
        send(msg);
    }

    private void arrancarCompetencia() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent("competencia iniciada");
        msg.addReceiver(new AID("jugador1", AID.ISLOCALNAME));
        msg.addReceiver(new AID("jugador2", AID.ISLOCALNAME));
        msg.addReceiver(new AID("jugador3", AID.ISLOCALNAME));
        send(msg);
    }
}