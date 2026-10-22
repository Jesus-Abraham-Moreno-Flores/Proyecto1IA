package uabc.computacion.proyectomedioia;

public class LauncherJugador2 {
    public static void main(String[] args) {
        String ipServidor = "192.168.1.50"; // IP de la PC Servidor
        MainApp.setConfiguracionCliente("jugador2", "uabc.computacion.proyectomedioia.AgenteJugadorManhattan", ipServidor);
        MainApp.main(args);
    }
}