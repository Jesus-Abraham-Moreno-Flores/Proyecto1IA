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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainApp extends Application {

    private GridPane gridFichas;
    private Label lblEstado;
    private AgenteGUI agenteGUI;

    // Paneles del Dashboard
    private VBox panelJ1, panelJ2, panelJ3;
    private Label txtStatsJ1, txtStatsJ2, txtStatsJ3;
    private List<Metricas> resultadosCompetencia = new ArrayList<>();

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
        raiz.setPadding(new Insets(20));
        raiz.setStyle("-fx-background-color: #1a1e24;");

        HBox panelBotones = new HBox(15);
        panelBotones.setAlignment(Pos.CENTER);

        Button btnGenerar = new Button("Generar Nuevo Reto");
        btnGenerar.setStyle("-fx-background-color: #2a2e35; -fx-text-fill: #8a8d91; -fx-padding: 10 20; -fx-font-weight: bold;");

        Button btnIniciar = new Button("¡Aceptar Reto e Iniciar!");
        btnIniciar.setStyle("-fx-background-color: #1e5c3a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        panelBotones.getChildren().addAll(btnGenerar, btnIniciar);

        gridFichas = new GridPane();
        gridFichas.setAlignment(Pos.CENTER);
        gridFichas.setHgap(ESPACIADO);
        gridFichas.setVgap(ESPACIADO);

        // --- CONSTRUCCIÓN DEL DASHBOARD COMPARATIVO ---
        HBox dashboard = new HBox(15);
        dashboard.setAlignment(Pos.CENTER);

        panelJ1 = crearTarjetaJugador("JUGADOR 1\n(Piezas Fuera)");
        txtStatsJ1 = (Label) panelJ1.getChildren().get(1);

        panelJ2 = crearTarjetaJugador("JUGADOR 2\n(Manhattan)");
        txtStatsJ2 = (Label) panelJ2.getChildren().get(1);

        panelJ3 = crearTarjetaJugador("JUGADOR 3\n(Linear Conflict)");
        txtStatsJ3 = (Label) panelJ3.getChildren().get(1);

        dashboard.getChildren().addAll(panelJ1, panelJ2, panelJ3);
        // ----------------------------------------------

        lblEstado = new Label("> Esperando conexión de agentes...");
        lblEstado.setStyle("-fx-text-fill: #00ff66; -fx-font-family: 'Consolas'; -fx-font-size: 14px;");

        raiz.getChildren().addAll(panelBotones, gridFichas, dashboard, lblEstado);

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

        Scene escena = new Scene(raiz, 800, 750);
        escenarioPrincipal.setTitle("8-Puzzle Dashboard - IA");
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.show();

        iniciarJADE();
    }

    private VBox crearTarjetaJugador(String titulo) {
        VBox tarjeta = new VBox(10);
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setPadding(new Insets(10));
        tarjeta.setPrefWidth(200);
        tarjeta.setStyle("-fx-background-color: #2a2e35; -fx-border-color: #4a4e54; -fx-border-radius: 5; -fx-background-radius: 5; -fx-border-width: 2;");

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-text-alignment: center;");

        Label lblStats = new Label("Esperando datos...");
        lblStats.setStyle("-fx-text-fill: #8a8d91; -fx-font-family: 'Consolas'; -fx-font-size: 12px;");

        tarjeta.getChildren().addAll(lblTitulo, lblStats);
        return tarjeta;
    }

    public void limpiarDashboard() {
        Platform.runLater(() -> {
            resultadosCompetencia.clear();

            String estiloBase = "-fx-background-color: #2a2e35; -fx-border-color: #4a4e54; -fx-border-radius: 5; -fx-background-radius: 5; -fx-border-width: 2;";
            panelJ1.setStyle(estiloBase);
            panelJ2.setStyle(estiloBase);
            panelJ3.setStyle(estiloBase);

            txtStatsJ1.setText("Calculando...");
            txtStatsJ2.setText("Calculando...");
            txtStatsJ3.setText("Calculando...");

            txtStatsJ1.setStyle("-fx-text-fill: #8a8d91; -fx-font-family: 'Consolas';");
            txtStatsJ2.setStyle("-fx-text-fill: #8a8d91; -fx-font-family: 'Consolas';");
            txtStatsJ3.setStyle("-fx-text-fill: #8a8d91; -fx-font-family: 'Consolas';");
        });
    }

    public void registrarMetricas(String emisor, long tiempo, int expandidos, int pasos) {
        Platform.runLater(() -> {
            resultadosCompetencia.add(new Metricas(emisor, tiempo, expandidos, pasos));
            String textoStats = String.format("Nodos: %d\nPasos: %d\nTiempo: %d ms", expandidos, pasos, tiempo);

            if (emisor.equals("jugador1")) {
                txtStatsJ1.setText(textoStats);
                txtStatsJ1.setStyle("-fx-text-fill: #00ff66; -fx-font-family: 'Consolas';");
            } else if (emisor.equals("jugador2")) {
                txtStatsJ2.setText(textoStats);
                txtStatsJ2.setStyle("-fx-text-fill: #00ff66; -fx-font-family: 'Consolas';");
            } else if (emisor.equals("jugador3")) {
                txtStatsJ3.setText(textoStats);
                txtStatsJ3.setStyle("-fx-text-fill: #00ff66; -fx-font-family: 'Consolas';");
            }

            // Si ya terminaron los 3, determinamos al ganador
            if (resultadosCompetencia.size() == 3) {
                determinarGanador();
            }
        });
    }

    private void determinarGanador() {
        // Criterio de victoria: 1. Menos Pasos -> 2. Menos Nodos Expandidos -> 3. Menor Tiempo
        resultadosCompetencia.sort(Comparator
                .comparingInt((Metricas m) -> m.pasos)
                .thenComparingInt(m -> m.nodos)
                .thenComparingLong(m -> m.tiempo));

        Metricas ganador = resultadosCompetencia.get(0);
        String estiloGanador = "-fx-background-color: #3b3518; -fx-border-color: #ffd700; -fx-border-radius: 5; -fx-background-radius: 5; -fx-border-width: 3;";

        if (ganador.agente.equals("jugador1")) panelJ1.setStyle(estiloGanador);
        if (ganador.agente.equals("jugador2")) panelJ2.setStyle(estiloGanador);
        if (ganador.agente.equals("jugador3")) panelJ3.setStyle(estiloGanador);

        agregarTextoPantalla("SISTEMA", "¡Competencia finalizada! Ganador: " + ganador.agente.toUpperCase());
    }

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
                            "-fx-background-color: #1a1e24; -fx-border-color: #00ff66; -fx-border-radius: 5; -fx-border-width: 2; -fx-text-fill: #00ff66; -fx-font-size: 24px; -fx-font-weight: bold;"
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

    public void animarSecuenciaGanadora(List<byte[]> secuencia, String emisor) {
        Platform.runLater(() -> {
            lblEstado.setText("> [" + emisor.toUpperCase() + "] Animando solución...");
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

                if (posFichaMovida == -1) continue;

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

                agregarTextoPantalla("SISTEMA", "Plataforma JADE lista. Listos para competir.");

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    // Clase auxiliar para ordenar los resultados
    private static class Metricas {
        String agente;
        long tiempo;
        int nodos;
        int pasos;

        public Metricas(String agente, long tiempo, int nodos, int pasos) {
            this.agente = agente;
            this.tiempo = tiempo;
            this.nodos = nodos;
            this.pasos = pasos;
        }
    }
}