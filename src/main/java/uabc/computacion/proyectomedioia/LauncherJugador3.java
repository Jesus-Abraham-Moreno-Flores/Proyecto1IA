package uabc.computacion.proyectomedioia;

public class LauncherJugador3 {
    public static void main(String[] args) {
        String ipServidor = "10.21.44.103"; // IP de la PC Servidor
        String miIpLocal = "10.21.44.103";
        MainApp.setConfiguracionCliente(
                "jugador1",
                "uabc.computacion.proyectomedioia.AgenteJugadorPiezaFuera",
                ipServidor,
                miIpLocal
        );

        MainApp.main(args);
    }
}