package uabc.computacion.proyectomedioia;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
    private AgenteGUI agenteGUI;
    private Label statusLabel; // Aqui esta nuestra nueva barra minimalista
    private GridPane gridFichas;
    private Button btnArrancar;

    @Override
    public void start(Stage stage) {
        stage.setTitle("8-Puzzle Dashboard - IA");

        Button btnGenerar = new Button("Generar Nuevo Reto");
        btnGenerar.setStyle("-fx-background-color: #2d3446; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px;");

        btnArrancar = new Button("¡Aceptar Reto e Iniciar!");
        btnArrancar.setStyle("-fx-background-color: #00ff66; -fx-text-fill: #151922; -fx-font-weight: bold; -fx-padding: 10px;");
        btnArrancar.setDisable(true);

        HBox panelBotones = new HBox(15, btnGenerar, btnArrancar);
        panelBotones.setAlignment(Pos.CENTER);

        // Diseno de la barra de estado
        statusLabel = new Label("> Esperando para generar tablero...");
        statusLabel.setStyle("-fx-text-fill: #00ff66; -fx-font-family: 'Consolas'; -fx-font-size: 14px;");
        statusLabel.setAlignment(Pos.CENTER);
        statusLabel.setMaxWidth(Double.MAX_VALUE);

        gridFichas = new GridPane();
        gridFichas.setAlignment(Pos.CENTER);
        gridFichas.setHgap(8);
        gridFichas.setVgap(8);

        btnGenerar.setOnAction(e -> {
            if (agenteGUI != null) {
                statusLabel.setText("> Solicitando tablero resoluble al generador...");
                agenteGUI.pedirTableroNuevo();
                btnArrancar.setDisable(false);
            }
        });

        btnArrancar.setOnAction(e -> {
            if (agenteGUI != null) {
                statusLabel.setText("> ¡COMPETENCIA INICIADA! Agentes buscando ruta...");
                btnArrancar.setDisable(true);
                btnGenerar.setDisable(true);
                agenteGUI.iniciarCompetencia();
            }
        });

        VBox root = new VBox(25, panelBotones, gridFichas, statusLabel);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #151922;");

        Scene scene = new Scene(root, 450, 520);
        stage.setScene(scene);
        stage.show();

        prenderJADE();
    }

    public void pintarTablero(byte[] tablero) {
        Platform.runLater(() -> {
            gridFichas.getChildren().clear();
            int pos = 0;

            for (int f = 0; f < 3; f++) {
                for (int c = 0; c < 3; c++) {
                    String textoFicha = tablero[pos] == 0 ? "" : String.valueOf(tablero[pos]);
                    Label ficha = new Label(textoFicha);

                    if (tablero[pos] == 0) {
                        ficha.setStyle("-fx-background-color: #1a1f2b; -fx-background-radius: 8px;");
                    } else {
                        ficha.setStyle(
                                "-fx-background-color: #2d3446; " +
                                        "-fx-text-fill: #00ff66; " +
                                        "-fx-border-color: #00ff66; " +
                                        "-fx-border-width: 2px; " +
                                        "-fx-border-radius: 8px; " +
                                        "-fx-background-radius: 8px; " +
                                        "-fx-font-size: 32px; " +
                                        "-fx-font-weight: bold;"
                        );
                    }

                    ficha.setMinSize(80, 80);
                    ficha.setAlignment(Pos.CENTER);
                    gridFichas.add(ficha, c, f);

                    ScaleTransition st = new ScaleTransition(Duration.millis(250), ficha);
                    st.setFromX(0.5); st.setFromY(0.5);
                    st.setToX(1.0); st.setToY(1.0);

                    FadeTransition ft = new FadeTransition(Duration.millis(250), ficha);
                    ft.setFromValue(0);
                    ft.setToValue(1.0);

                    st.play();
                    ft.play();

                    pos++;
                }
            }
            statusLabel.setText("> Tablero renderizado. Validación Matemática: OK.");
        });
    }

    public void animarSecuenciaGanadora(List<byte[]> listaMovimientos, String ganador) {
        Platform.runLater(() -> {
            statusLabel.setText(">> Reproduciendo ruta ganadora de: " + ganador);
        });

        new Thread(() -> {
            for (int i = 0; i < listaMovimientos.size(); i++) {
                byte[] estadoActual = listaMovimientos.get(i);
                pintarTablero(estadoActual);

                try {
                    Thread.sleep(600);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            Platform.runLater(() -> statusLabel.setText(">> Animación finalizada."));
        }).start();
    }

    public void agregarTextoPantalla(String quien, String queDijo) {
        Platform.runLater(() -> {
            // Solo reemplaza el texto con el mensaje mas reciente
            statusLabel.setText("[" + quien + "]: " + queDijo);
        });
    }

    private void prenderJADE() {
        new Thread(() -> {
            try {
                Runtime rt = Runtime.instance();
                Profile p = new ProfileImpl();
                p.setParameter(Profile.GUI, "true");
                AgentContainer container = rt.createMainContainer(p);

                AgentController generador = container.createNewAgent(
                        "generador",
                        "uabc.computacion.proyectomedioia.AgenteGenerador",
                        null
                );
                generador.start();

                for (int i = 1; i <= 3; i++) {
                    AgentController jugador = container.createNewAgent(
                            "jugador" + i,
                            "uabc.computacion.proyectomedioia.AgenteJugador",
                            null
                    );
                    jugador.start();
                }

                agenteGUI = new AgenteGUI();
                agenteGUI.setVista(this);
                AgentController guiCtrl = container.acceptNewAgent("guiAgent", agenteGUI);
                guiCtrl.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}