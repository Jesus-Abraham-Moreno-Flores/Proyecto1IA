package uabc.computacion.proyectomedioia;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class ServerLauncher {
    // Cambia esto por la IP local de la PC Servidor
    public static final String IP_SERVIDOR = "192.168.1.50";

    public static void main(String[] args) {
        try {
            Runtime rt = Runtime.instance();
            Profile perfil = new ProfileImpl();

            perfil.setParameter(Profile.MAIN_HOST, IP_SERVIDOR);
            perfil.setParameter(Profile.LOCAL_HOST, IP_SERVIDOR);
            perfil.setParameter(Profile.MAIN_PORT, "1099");
            perfil.setParameter(Profile.GUI, "true"); // Despliega la interfaz de administración de JADE

            AgentContainer mainContainer = rt.createMainContainer(perfil);

            // Iniciar agente generador/coordinador central
            AgentController acGenerador = mainContainer.createNewAgent(
                    "generador",
                    "uabc.computacion.proyectomedioia.AgenteGenerador",
                    null
            );
            acGenerador.start();

            System.out.println(">>> Servidor JADE listo en " + IP_SERVIDOR + ":1099");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}