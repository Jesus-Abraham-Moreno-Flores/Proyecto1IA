package uabc.computacion.proyectomedioia;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

public class MainApp extends Application {

    private GridPane gridFichas;
    private Label lblEstado;
    private AgenteGUI agenteGUI;

    private final int TAMANO_CASILLA = 80;
    private final int ESPACIADO = 10;

    public static void main(String[] args) {
        launch(args);
    }

    public void setAgenteGUI(AgenteGUI agenteGUI) {
        this.agenteGUI = agenteGUI;
    }

    @Override
    public void start(Stage escenarioPrincipal) {
        VBox raiz = new VBox(20);
        raiz.setAlignment(Pos.CENTER);
        raiz.setPadding(new Insets(30));
        raiz.setStyle("-fx-background-color: #1a1e24;");

        HBox panelBotones = new HBox(15);
        panelBotones.setAlignment(Pos.CENTER);

        Button btnGenerar = new Button("Generar Nuevo Reto");
        btnGenerar.setStyle("-fx-background-color: #2a2e35; -fx-text-fill: #8a8d91; -fx-padding: 10 20;");

        Button btnIniciar = new Button("¡Aceptar Reto e Iniciar!");
        btnIniciar.setStyle("-fx-background-color: #1e5c3a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        panelBotones.getChildren().addAll(btnGenerar, btnIniciar);

        gridFichas = new GridPane();
        gridFichas.setAlignment(Pos.CENTER);
        gridFichas.setHgap(ESPACIADO);
        gridFichas.setVgap(ESPACIADO);

        lblEstado = new Label("> Esperando conexión de agentes...");
        lblEstado.setStyle("-fx-text-fill: #00ff66; -fx-font-family: 'Consolas'; -fx-font-size: 14px;");

        raiz.getChildren().addAll(panelBotones, gridFichas, lblEstado);

        btnGenerar.setOnAction(e -> {
            if (agenteGUI != null) {
                agenteGUI.pedirTableroNuevo();
            }
        });

        btnIniciar.setOnAction(e -> {
            if (agenteGUI != null) {
                agenteGUI.iniciarCompetencia();
            }
        });

        Scene escena = new Scene(raiz, 800, 600);
        escenarioPrincipal.setTitle("8-Puzzle Dashboard - IA");
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.show();

        iniciarJADE();
    }

    // CORRECCIÓN: Envolvemos el repintado en el hilo de JavaFX
    public void pintarTablero(byte[] estado) {
        Platform.runLater(() -> {
            gridFichas.getChildren().clear();

            for (int i = 0; i < 9; i++) {
                int numero = estado[i];

                Label ficha = new Label();
                ficha.setPrefSize(TAMANO_CASILLA, TAMANO_CASILLA);
                ficha.setAlignment(Pos.CENTER);

                if (numero == 0) {
                    ficha.setText("");
                    ficha.setStyle("-fx-background-color: transparent;");
                } else {
                    ficha.setText(String.valueOf(numero));
                    ficha.setStyle(
                            "-fx-background-color: #1a1e24;" +
                                    "-fx-border-color: #00ff66;" +
                                    "-fx-border-radius: 5;" +
                                    "-fx-border-width: 2;" +
                                    "-fx-text-fill: #00ff66;" +
                                    "-fx-font-size: 24px;" +
                                    "-fx-font-weight: bold;"
                    );
                }

                gridFichas.add(ficha, i % 3, i / 3);
            }
        });
    }

    public void agregarTextoPantalla(String emisor, String mensaje) {
        Platform.runLater(() -> {
            lblEstado.setText("> [" + emisor.toUpperCase() + "] " + mensaje);
        });
    }

    // CORRECCIÓN: Envolvemos TODA la construcción de la animación en el hilo de JavaFX
    public void animarSecuenciaGanadora(List<byte[]> secuencia, String emisor) {
        Platform.runLater(() -> {
            lblEstado.setText("> [" + emisor.toUpperCase() + "] Animando solución paso a paso...");

            // Pintamos el estado inicial directamente sin llamar al método externo para evitar desfases
            gridFichas.getChildren().clear();
            for (int i = 0; i < 9; i++) {
                int numero = secuencia.get(0)[i];
                Label ficha = new Label();
                ficha.setPrefSize(TAMANO_CASILLA, TAMANO_CASILLA);
                ficha.setAlignment(Pos.CENTER);
                if (numero == 0) {
                    ficha.setText("");
                    ficha.setStyle("-fx-background-color: transparent;");
                } else {
                    ficha.setText(String.valueOf(numero));
                    ficha.setStyle("-fx-background-color: #1a1e24; -fx-border-color: #00ff66; -fx-border-radius: 5; -fx-border-width: 2; -fx-text-fill: #00ff66; -fx-font-size: 24px; -fx-font-weight: bold;");
                }
                gridFichas.add(ficha, i % 3, i / 3);
            }

            SequentialTransition animacionGlobal = new SequentialTransition();
            int desplazamientoPixeles = TAMANO_CASILLA + ESPACIADO;

            for (int i = 0; i < secuencia.size() - 1; i++) {
                byte[] estadoActual = secuencia.get(i);
                byte[] estadoSiguiente = secuencia.get(i + 1);

                int posHuecoActual = -1;
                int posFichaMovida = -1;

                for (int j = 0; j < 9; j++) {
                    if (estadoActual[j] == 0) posHuecoActual = j;
                    if (estadoSiguiente[j] == 0) posFichaMovida = j;
                }

                if (posFichaMovida == -1) continue; // Seguro por si el algoritmo devuelve un paso duplicado

                byte numeroFicha = estadoActual[posFichaMovida];
                Node nodoFicha = buscarNodoPorNumero(numeroFicha);

                if (nodoFicha != null) {
                    int colInicial = posFichaMovida % 3;
                    int filaInicial = posFichaMovida / 3;
                    int colFinal = posHuecoActual % 3;
                    int filaFinal = posHuecoActual / 3;

                    int deltaX = (colFinal - colInicial) * desplazamientoPixeles;
                    int deltaY = (filaFinal - filaInicial) * desplazamientoPixeles;

                    TranslateTransition transicion = new TranslateTransition(Duration.millis(250), nodoFicha);
                    transicion.setByX(deltaX);
                    transicion.setByY(deltaY);

                    animacionGlobal.getChildren().add(transicion);
                }
            }

            animacionGlobal.setOnFinished(e -> {
                pintarTablero(secuencia.get(secuencia.size() - 1));
                lblEstado.setText("> [" + emisor.toUpperCase() + "] ¡Rompecabezas resuelto!");
            });

            animacionGlobal.play();
        });
    }

    private Node buscarNodoPorNumero(byte numero) {
        for (Node nodo : gridFichas.getChildren()) {
            if (nodo instanceof Label) {
                String texto = ((Label) nodo).getText();
                if (texto.equals(String.valueOf(numero))) {
                    return nodo;
                }
            }
        }
        return null;
    }

    private void iniciarJADE() {
        new Thread(() -> {
            try {
                Runtime rt = Runtime.instance();
                Profile perfil = new ProfileImpl();
                perfil.setParameter(Profile.MAIN_HOST, "localhost");
                perfil.setParameter(Profile.GUI, "true");

                AgentContainer contenedorPrincipal = rt.createMainContainer(perfil);

                AgentController acGUI = contenedorPrincipal.createNewAgent("gui", "uabc.computacion.proyectomedioia.AgenteGUI", new Object[]{this});
                acGUI.start();

                AgentController acGenerador = contenedorPrincipal.createNewAgent("generador", "uabc.computacion.proyectomedioia.AgenteGenerador", null);
                acGenerador.start();

                AgentController acJugador1 = contenedorPrincipal.createNewAgent("jugador1", "uabc.computacion.proyectomedioia.AgenteJugador", null);
                acJugador1.start();

                AgentController acJugador2 = contenedorPrincipal.createNewAgent("jugador2", "uabc.computacion.proyectomedioia.AgenteJugador", null);
                acJugador2.start();

                AgentController acJugador3 = contenedorPrincipal.createNewAgent("jugador3", "uabc.computacion.proyectomedioia.AgenteJugador", null);
                acJugador3.start();

                agregarTextoPantalla("SISTEMA", "Plataforma JADE lista. Generador y 3 Jugadores en línea.");

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}