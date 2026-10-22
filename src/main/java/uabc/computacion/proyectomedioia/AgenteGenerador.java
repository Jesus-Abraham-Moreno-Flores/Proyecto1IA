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
                    // Nos piden un tablero nuevo porque el usuario presiono el boton
                    if (msg.getPerformative() == ACLMessage.REQUEST && msg.getContent().equals("dame_tablero")) {
                        byte[] nuevoTablero = crearTableroValido();

                        // Empaquetamos el arreglo en un String separado por comas para mandarlo facil
                        StringBuilder textoTablero = new StringBuilder();
                        for (int i = 0; i < nuevoTablero.length; i++) {
                            textoTablero.append(nuevoTablero[i]);
                            if(i < nuevoTablero.length - 1) textoTablero.append(",");
                        }

                        ACLMessage respuesta = msg.createReply();
                        respuesta.setPerformative(ACLMessage.INFORM);
                        respuesta.setContent(textoTablero.toString());
                        send(respuesta);
                    }
                } else {
                    block(); // Le damos un respiro a la CPU si no hay mensajes
                }
            }
        });
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
                // El 0 es el hueco, asi que lo saltamos en el conteo
                if (tablero[i] != 0 && tablero[j] != 0 && tablero[i] > tablero[j]) {
                    inversas++;
                }
            }
        }
        return inversas % 2 == 0;
    }
}