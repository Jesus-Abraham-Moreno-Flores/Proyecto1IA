package uabc.computacion.proyectomedioia;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {
    private AgenteGUI agenteGUI;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Agente GUI");

        Button btnIniciar = new Button("Iniciar");
        btnIniciar.setMaxWidth(Double.MAX_VALUE);

        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefHeight(200);

        btnIniciar.setOnAction(e -> {
            if (agenteGUI != null) {
                textArea.appendText("--- Notificación enviada: competencia iniciada ---\n");
                agenteGUI.enviarNotificacion();
            }
        });

        VBox root = new VBox(10, btnIniciar, textArea);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 400, 260);
        stage.setScene(scene);
        stage.show();

        // Arrancar JADE de forma programática
        iniciarContenedorJADE(textArea);
    }

    private void iniciarContenedorJADE(TextArea textArea) {
        new Thread(() -> {
            try {
                Runtime rt = Runtime.instance();
                Profile p = new ProfileImpl();
                p.setParameter(Profile.GUI, "true");
                AgentContainer container = rt.createMainContainer(p);

                // Crear los 3 agentes jugadores
                for (int i = 1; i <= 3; i++) {
                    AgentController jugador = container.createNewAgent(
                            "jugador" + i,
                            "uabc.computacion.proyectomedioia.AgenteJugador",
                            null
                    );
                    jugador.start();
                }

                // Crear el Agente GUI y pasarle el TextArea
                agenteGUI = new AgenteGUI(textArea);
                AgentController guiController = container.acceptNewAgent("guiAgent", agenteGUI);
                guiController.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}