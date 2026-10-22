package uabc.computacion.proyectomedioia;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.List;

public class AgenteGUI extends Agent {
    private MainApp vista;
    private String ultimoTableroGenerado = "";

    @Override
    protected void setup() {
        // Enlazar el agente a la ventana gráfica desde sus argumentos iniciales
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            vista = (MainApp) args[0];
            vista.setAgenteGUI(this);
        }

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();

                if (msg != null) {
                    String emisor = msg.getSender().getLocalName();
                    String contenido = msg.getContent();

                    // Escudo: Ignoramos mensajes vacíos del sistema (evita que se caiga el agente)
                    if (contenido == null) {
                        return;
                    }

                    if (emisor.equals("generador") && msg.getPerformative() == ACLMessage.INFORM) {
                        ultimoTableroGenerado = contenido;

                        String[] pedazos = contenido.split(",");
                        byte[] arregloTablero = new byte[9];
                        for (int i = 0; i < 9; i++) {
                            arregloTablero[i] = Byte.parseByte(pedazos[i]);
                        }
                        if (vista != null) {
                            vista.pintarTablero(arregloTablero);
                        }
                    }
                    else if (contenido.startsWith("ruta|") && contenido.contains("-")) {
                        String[] partes = contenido.split("\\|");
                        if(partes.length > 1) {
                            String[] tablerosTexto = partes[1].split("-");
                            List<byte[]> secuenciaAnimacion = new ArrayList<>();

                            for (String t : tablerosTexto) {
                                if (t.isEmpty()) continue;

                                byte[] estadoTablero = new byte[9];
                                for (int i = 0; i < 9; i++) {
                                    estadoTablero[i] = (byte) (t.charAt(i) - '0');
                                }
                                secuenciaAnimacion.add(estadoTablero);
                            }

                            if (vista != null) {
                                vista.animarSecuenciaGanadora(secuenciaAnimacion, emisor);
                            }
                        }
                    }
                    else {
                        if (vista != null) {
                            vista.agregarTextoPantalla(emisor, contenido);
                        }
                    }
                } else {
                    block();
                }
            }
        });
    }

    public void pedirTableroNuevo() {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setContent("dame_tablero");
        msg.addReceiver(new AID("generador", AID.ISLOCALNAME));
        send(msg);
    }

    public void iniciarCompetencia() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent("competencia_iniciada|" + ultimoTableroGenerado);
        msg.addReceiver(new AID("jugador1", AID.ISLOCALNAME));
        msg.addReceiver(new AID("jugador2", AID.ISLOCALNAME));
        msg.addReceiver(new AID("jugador3", AID.ISLOCALNAME));
        send(msg);
    }
}