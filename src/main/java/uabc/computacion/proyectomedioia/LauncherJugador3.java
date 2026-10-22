package uabc.computacion.proyectomedioia;

public class LauncherJugador3 {
    public static void main(String[] args) {
        String ipServidor = "192.168.1.50"; // IP de la PC Servidor
        MainApp.setConfiguracionCliente("jugador3", "uabc.computacion.proyectomedioia.AgenteJugadorLinearConflict", ipServidor);
        MainApp.main(args);
    }
}