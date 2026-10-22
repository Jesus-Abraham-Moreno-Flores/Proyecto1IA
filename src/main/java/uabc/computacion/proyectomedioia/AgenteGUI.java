package uabc.computacion.proyectomedioia;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

public class AgenteGUI extends Agent {

    private MainApp vista;

    @Override
    protected void setup() {
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

                    if (contenido == null) {
                        return;
                    }

                    // 1. RECEPCIÓN DE TABLERO DESDE EL GENERADOR CENTRAL
                    if (contenido.startsWith("tablero|")) {
                        String datosTablero = contenido.substring("tablero|".length());
                        byte[] arregloTablero = convertirABytes(datosTablero);

                        if (vista != null) {
                            vista.pintarTablero(arregloTablero);
                            vista.agregarTextoPantalla("SISTEMA", "Nuevo reto sincronizado.");
                        }
                    }
                    // 2. RECEPCIÓN DE MÉTRICAS ESTRUCTURADAS
                    else if (contenido.startsWith("metricas|")) {
                        if (vista != null) {
                            try {
                                String[] partes = contenido.split("\\|");
                                String agente = partes[1];
                                long tiempo = Long.parseLong(partes[2]);
                                int expandidos = Integer.parseInt(partes[3]);
                                int pasos = Integer.parseInt(partes[4]);

                                vista.registrarMetricas(agente, tiempo, expandidos, pasos);
                            } catch (Exception e) {
                                vista.agregarTextoPantalla(emisor, "Error al procesar métricas.");
                            }
                        }
                    }
                    // 3. COMPATIBILIDAD CON TEXTO LIBRE "¡Meta encontrada!"
                    else if (contenido.startsWith("¡Meta encontrada!")) {
                        if (vista != null) {
                            try {
                                String[] partes = contenido.split("\\|");
                                long tiempo = Long.parseLong(partes[1].replaceAll("\\D+", ""));
                                int expandidos = Integer.parseInt(partes[2].replaceAll("\\D+", ""));
                                int pasos = Integer.parseInt(partes[3].replaceAll("\\D+", ""));

                                vista.registrarMetricas(emisor, tiempo, expandidos, pasos);
                            } catch (Exception e) {
                                vista.agregarTextoPantalla(emisor, "Error al procesar métricas.");
                            }
                        }
                    }
                    // 4. RECEPCIÓN DE LA RUTA PARA LA ANIMACIÓN LOCAL
                    else if (contenido.startsWith("ruta|")) {
                        String rutaStr = contenido.substring("ruta|".length());
                        List<byte[]> secuenciaAnimacion = deserializarRuta(rutaStr);

                        if (vista != null && !secuenciaAnimacion.isEmpty()) {
                            // Limpia "gui_" del emisor para pasar solo el nombre del jugador
                            vista.animarSecuenciaGanadora(secuenciaAnimacion, emisor.replace("gui_", ""));
                        }
                    }
                    // 5. MENSAJES INFORMATIVOS DE TEXTO EN CONSOLA
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
        if (vista != null) {
            vista.limpiarDashboard();
        }
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setContent("pedir_tablero");
        msg.addReceiver(new AID("generador", AID.ISLOCALNAME));
        send(msg);
    }

    public void iniciarCompetencia() {
        if (vista != null) {
            vista.limpiarDashboard();
        }
        // Le pedimos al AgenteGenerador (Servidor) que dé la señal de salida a todos los jugadores
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setContent("iniciar_competencia");
        msg.addReceiver(new AID("generador", AID.ISLOCALNAME));
        send(msg);
    }

    private byte[] convertirABytes(String str) {
        byte[] estado = new byte[9];
        if (str.contains(",")) {
            String[] partes = str.split(",");
            for (int i = 0; i < 9 && i < partes.length; i++) {
                estado[i] = Byte.parseByte(partes[i].trim());
            }
        } else {
            String limpia = str.trim();
            for (int i = 0; i < 9 && i < limpia.length(); i++) {
                estado[i] = (byte) (limpia.charAt(i) - '0');
            }
        }
        return estado;
    }

    private List<byte[]> deserializarRuta(String rutaStr) {
        List<byte[]> secuencia = new ArrayList<>();
        String[] pasos = rutaStr.split("-");
        for (String p : pasos) {
            if (!p.trim().isEmpty()) {
                secuencia.add(convertirABytes(p));
            }
        }
        return secuencia;
    }
}