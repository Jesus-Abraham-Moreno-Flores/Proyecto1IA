package uabc.computacion.proyectomedioia;

public class LauncherJugador1 {
    public static void main(String[] args) {
        String ipServidor = "10.21.44.103"; // IP de la PC Servidor
        MainApp.setConfiguracionCliente("jugador1", "uabc.computacion.proyectomedioia.AgenteJugadorPiezaFuera", ipServidor);
        MainApp.main(args);
    }
}