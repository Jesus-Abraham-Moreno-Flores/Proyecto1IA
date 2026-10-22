package uabc.computacion.proyectomedioia;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class AgenteGUI extends Agent {
    private MainApp vista;

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

                    // Si responde el generador, desempaquetamos y pintamos la cuadricula
                    if (emisor.equals("generador") && msg.getPerformative() == ACLMessage.INFORM) {
                        String[] pedazos = contenido.split(",");
                        byte[] arregloTablero = new byte[9];

                        for (int i = 0; i < 9; i++) {
                            arregloTablero[i] = Byte.parseByte(pedazos[i]);
                        }
                        vista.pintarTablero(arregloTablero);
                    }
                    // Aqui caerian los mensajes de los jugadores cuando terminen
                    else {
                        vista.agregarTextoPantalla(emisor, contenido);
                    }
                } else {
                    block();
                }
            }
        });
    }

    // Pide un reto nuevo para ver si al usuario le gusta
    public void pedirTableroNuevo() {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setContent("dame_tablero");
        msg.addReceiver(new AID("generador", AID.ISLOCALNAME));
        send(msg);
    }

    // Da el banderazo oficial de salida a los algoritmos de busqueda
    public void iniciarCompetencia() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent("competencia_iniciada");
        msg.addReceiver(new AID("jugador1", AID.ISLOCALNAME));
        msg.addReceiver(new AID("jugador2", AID.ISLOCALNAME));
        msg.addReceiver(new AID("jugador3", AID.ISLOCALNAME));
        send(msg);
    }
}