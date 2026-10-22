package uabc.computacion.proyectomedioia;

public class LauncherJugador3 {
    public static void main(String[] args) {
        String ipServidor = "10.21.44.103"; // IP de la PC Servidor
        String miIpLocal = "10.21.44.103";  // IP de esta computadora

        MainApp.setConfiguracionCliente(
                "jugador3", // <-- IMPORTANTE: Cambiar a "jugador3"
                "uabc.computacion.proyectomedioia.AgenteJugadorLinearConflict",
                ipServidor,
                miIpLocal
        );

        MainApp.main(args);
    }
}