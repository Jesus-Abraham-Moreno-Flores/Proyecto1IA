package puzzle.abraham;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import puzzle.model.Board;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Ejecutar esta Main para ver a jugador3 trabajar con JADE, sin ventanas. */
public final class Main {

    public static void main(String[] args) throws Exception {
        String tablero = "783012456"; //783012456
        int limite = 181440;
        for (String argumento : args) {
            if (argumento.startsWith("--board=")) {
                tablero = argumento.substring(8);
            } else if (argumento.startsWith("--limit=")) {
                limite = Integer.parseInt(argumento.substring(8));
            } else {
                throw new IllegalArgumentException("Use --board=123406758 o --limit=181440.");
            }
        }
        new Board(tablero);
        if (limite < 1 || limite > 181440) {
            throw new IllegalArgumentException("El limite debe estar entre 1 y 181440.");
        }

        // Se muestran las acciones de la demo; los errores JADE siguen visibles.
        Logger.getLogger("jade").setLevel(Level.WARNING);
        ProfileImpl perfil = new ProfileImpl();
        perfil.setParameter(Profile.MAIN_HOST, "127.0.0.1");
        perfil.setParameter(Profile.LOCAL_HOST, "127.0.0.1");
        perfil.setParameter(Profile.GUI, "false");
        perfil.setParameter(Profile.NO_MTP, "true");

        AgentContainer contenedor = jade.core.Runtime.instance().createMainContainer(perfil);
        if (contenedor == null) {
            System.err.println("No se pudo iniciar JADE. Revise el puerto 1099.");
            cerrar(2);
            return;
        }

        System.out.println("PROYECTO IA - ABRAHAM");
        System.out.println("JADE iniciado: agentes consola y jugador3.");
        contenedor.createNewAgent("jugador3", Player3Agent.class.getName(), null).start();
        contenedor.createNewAgent("consola", AgenteConsola.class.getName(),
                new Object[]{tablero, limite}).start();
    }

    public static void cerrar(int codigo) {
        // El cierre va en otro hilo para no esperar al propio agente que lo solicito.
        Thread cierre = new Thread(() -> {
            jade.core.Runtime.instance().shutDown();
            System.exit(codigo);
        }, "cerrar-jade");
        cierre.start();
    }
}
