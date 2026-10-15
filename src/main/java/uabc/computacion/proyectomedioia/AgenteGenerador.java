package uabc.computacion.proyectomedioia;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.Random;

public class AgenteGenerador extends Agent {

    @Override
    protected void setup() {
        System.out.println("Generador en linea: " + getLocalName());

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null) {
                    // Si la interfaz nos pide el tablero, armamos uno nuevo
                    if (msg.getPerformative() == ACLMessage.REQUEST && msg.getContent().equals("dame_tablero")) {
                        byte[] nuevoTablero = crearTableroValido();

                        // Lo pasamos a texto separado por comas para no batallar con serializacion compleja
                        String textoTablero = "";
                        for (int i = 0; i < nuevoTablero.length; i++) {
                            textoTablero += nuevoTablero[i];
                            if(i < nuevoTablero.length - 1) textoTablero += ",";
                        }

                        ACLMessage respuesta = msg.createReply();
                        respuesta.setPerformative(ACLMessage.INFORM);
                        respuesta.setContent(textoTablero);
                        send(respuesta);
                    }
                } else {
                    block(); // Bloqueamos para que no consuma CPU a lo loco
                }
            }
        });
    }

    private byte[] crearTableroValido() {
        byte[] tablero = new byte[9];
        Random rnd = new Random();
        boolean esValido = false;

        // Iteramos hasta que nos salga un tablero que si se pueda resolver
        while (!esValido) {
            // Llenar del 0 al 8 (el 0 será nuestro hueco vacio)
            for (byte i = 0; i < 9; i++) {
                tablero[i] = i;
            }

            // Revolver las fichas a lo random
            for (int i = tablero.length - 1; i > 0; i--) {
                int index = rnd.nextInt(i + 1);
                byte temp = tablero[index];
                tablero[index] = tablero[i];
                tablero[i] = temp;
            }

            // Checar si la combinacion tiene solucion
            esValido = revisarInversiones(tablero);
        }
        return tablero;
    }

    // Regla matemática del 8-puzzle: solo se resuelve si las inversiones son pares
    private boolean revisarInversiones(byte[] tablero) {
        int inversas = 0;
        for (int i = 0; i < tablero.length - 1; i++) {
            for (int j = i + 1; j < tablero.length; j++) {
                // Saltamos el 0 porque es el espacio en blanco, no una ficha real
                if (tablero[i] != 0 && tablero[j] != 0 && tablero[i] > tablero[j]) {
                    inversas++;
                }
            }
        }
        return inversas % 2 == 0;
    }
}