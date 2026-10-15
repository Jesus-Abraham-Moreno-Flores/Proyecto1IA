package uabc.computacion.proyectomedioia;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {
    private AgenteGUI agenteGUI;
    private TextArea textArea;
    private GridPane gridFichas;

    @Override
    public void start(Stage stage) {
        stage.setTitle("8-Puzzle GUI");

        Button btnArrancar = new Button("Generar Tablero e Iniciar");
        btnArrancar.setMaxWidth(Double.MAX_VALUE);

        textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefHeight(150);

        // Grid para pintar las fichas estilo rompecabezas
        gridFichas = new GridPane();
        gridFichas.setAlignment(Pos.CENTER);
        gridFichas.setHgap(5);
        gridFichas.setVgap(5);

        btnArrancar.setOnAction(e -> {
            if (agenteGUI != null) {
                textArea.appendText("--- Pidiendo estado inicial resoluble ---\n");
                agenteGUI.pedirTablero();
            }
        });

        VBox root = new VBox(15, btnArrancar, gridFichas, textArea);
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 400, 450);
        stage.setScene(scene);
        stage.show();

        prenderJADE();
    }

    public void pintarTablero(byte[] tablero) {
        // Platform.runLater es clave para que no truene la GUI al actualizarla desde JADE
        Platform.runLater(() -> {
            gridFichas.getChildren().clear();
            int pos = 0;

            // Mapeamos el arreglo unidimensional de 9 a una cuadricula 3x3
            for (int f = 0; f < 3; f++) {
                for (int c = 0; c < 3; c++) {
                    String textoFicha = tablero[pos] == 0 ? " " : String.valueOf(tablero[pos]);
                    Label ficha = new Label(textoFicha);

                    // Diseno rapido de los cuadritos
                    ficha.setStyle("-fx-border-color: black; -fx-padding: 10px; -fx-font-size: 24px; -fx-font-weight: bold;");
                    ficha.setMinSize(60, 60);
                    ficha.setAlignment(Pos.CENTER);

                    gridFichas.add(ficha, c, f);
                    pos++;
                }
            }
            textArea.appendText(">> Tablero dibujado. Solvabilidad: Validada.\n");
        });
    }

    public void agregarTextoPantalla(String quien, String queDijo) {
        Platform.runLater(() -> {
            textArea.appendText("Notificacion [" + quien + "]: " + queDijo + "\n");
        });
    }

    private void prenderJADE() {
        new Thread(() -> {
            try {
                Runtime rt = Runtime.instance();
                Profile p = new ProfileImpl();
                p.setParameter(Profile.GUI, "true");
                AgentContainer container = rt.createMainContainer(p);

                // Instanciamos a nuestro nuevo Generador
                AgentController generador = container.createNewAgent(
                        "generador",
                        "uabc.computacion.proyectomedioia.AgenteGenerador",
                        null
                );
                generador.start();

                // Instanciamos los 3 jugadores
                for (int i = 1; i <= 3; i++) {
                    AgentController jugador = container.createNewAgent(
                            "jugador" + i,
                            "uabc.computacion.proyectomedioia.AgenteJugador",
                            null
                    );
                    jugador.start();
                }

                // Inyectamos la interfaz al AgenteGUI
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