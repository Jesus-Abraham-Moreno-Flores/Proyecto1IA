package uabc.computacion.proyectomedioia;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.Random;

public class AgenteGenerador extends Agent {

    private byte[] tableroActual;

    @Override
    protected void setup() {
        System.out.println("Generador en linea: " + getLocalName());

        // Generar un tablero válido inicial al arrancar el servidor
        tableroActual = crearTableroValido();

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null) {
                    String contenido = msg.getContent();

                    if ("pedir_tablero".equals(contenido) || "dame_tablero".equals(contenido)) {
                        // Generar un nuevo tablero resoluble
                        tableroActual = crearTableroValido();

                        // Notificar el mismo tablero a las GUIs de todas las computadoras
                        notificarTableroAGuis(convertirAString(tableroActual));

                    } else if ("iniciar_competencia".equals(contenido)) {
                        // Enviar el tablero actual a todos los agentes jugadores
                        iniciarCompetenciaJugadores(convertirAString(tableroActual));
                    }
                } else {
                    block(); // Liberar CPU mientras no haya mensajes
                }
            }
        });
    }

    private void notificarTableroAGuis(String tableroStr) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID("gui_jugador1", AID.ISLOCALNAME));
        msg.addReceiver(new AID("gui_jugador2", AID.ISLOCALNAME));
        msg.addReceiver(new AID("gui_jugador3", AID.ISLOCALNAME));
        msg.setContent("tablero|" + tableroStr);
        send(msg);
    }

    private void iniciarCompetenciaJugadores(String tableroStr) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID("jugador1", AID.ISLOCALNAME));
        msg.addReceiver(new AID("jugador2", AID.ISLOCALNAME));
        msg.addReceiver(new AID("jugador3", AID.ISLOCALNAME));
        msg.setContent("competencia_iniciada|" + tableroStr);
        send(msg);
    }

    private String convertirAString(byte[] tablero) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tablero.length; i++) {
            sb.append(tablero[i]);
            if (i < tablero.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    private byte[] crearTableroValido() {
        byte[] tablero = new byte[9];
        Random rnd = new Random();
        boolean esValido = false;

        // Ciclamos hasta que salga uno par (resoluble)
        while (!esValido) {
            // Llenamos el arreglo del 0 al 8
            for (byte i = 0; i < 9; i++) {
                tablero[i] = i;
            }

            // Revolvemos las fichas (Fisher-Yates)
            for (int i = tablero.length - 1; i > 0; i--) {
                int index = rnd.nextInt(i + 1);
                byte temp = tablero[index];
                tablero[index] = tablero[i];
                tablero[i] = temp;
            }

            esValido = revisarInversiones(tablero);
        }
        return tablero;
    }

    // Regla de paridad para saber si se puede armar
    private boolean revisarInversiones(byte[] tablero) {
        int inversas = 0;
        for (int i = 0; i < tablero.length - 1; i++) {
            for (int j = i + 1; j < tablero.length; j++) {
                // El 0 es el hueco, así que lo saltamos en el conteo
                if (tablero[i] != 0 && tablero[j] != 0 && tablero[i] > tablero[j]) {
                    inversas++;
                }
            }
        }
        return inversas % 2 == 0;
    }
}