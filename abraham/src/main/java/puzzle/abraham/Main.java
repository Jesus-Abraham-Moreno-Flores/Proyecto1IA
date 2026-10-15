package puzzle.abraham;
import puzzle.shared.*;
import jade.wrapper.AgentContainer;

public final class Main {
    public static void main(String[] args)throws Exception {
        Options o=Options.parse(args);AgentContainer c=Platform.start(o);String[] players={"jugador3"};
        c.createNewAgent("gui","puzzle.support.PlayerTestCoordinator",new Object[]{o,players,"Abraham"}).start();
        c.createNewAgent("generador","puzzle.support.GraphFixtureAgent",new Object[]{players}).start();
        c.createNewAgent("jugador3","puzzle.abraham.Player3Agent",null).start();
        System.out.println("JADE iniciado - avance de Abraham. Ejecute esta Main para la demostracion.");
    }
}
