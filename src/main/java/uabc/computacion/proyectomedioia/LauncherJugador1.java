package uabc.computacion.proyectomedioia;

public class LauncherJugador1 {
    public static void main(String[] args) {
        String ipServidor = "192.168.1.50"; // IP de la PC Servidor
        MainApp.setConfiguracionCliente("jugador1", "uabc.computacion.proyectomedioia.AgenteJugadorPiezaFuera", ipServidor);
        MainApp.main(args);
    }
}