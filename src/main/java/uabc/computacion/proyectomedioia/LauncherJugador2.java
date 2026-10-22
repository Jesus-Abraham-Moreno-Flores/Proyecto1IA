package uabc.computacion.proyectomedioia;

public class LauncherJugador2 {
    public static void main(String[] args) {
        String ipServidor = "10.21.44.103"; // IP de la PC Servidor
        String miIpLocal = "10.21.44.103";  // IP de esta computadora

        MainApp.setConfiguracionCliente(
                "jugador2", // <-- IMPORTANTE: Cambiar a "jugador2"
                "uabc.computacion.proyectomedioia.AgenteJugadorManhattan",
                ipServidor,
                miIpLocal
        );

        MainApp.main(args);
    }
}