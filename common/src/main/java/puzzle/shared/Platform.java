package puzzle.shared;
import jade.core.*;
import jade.wrapper.AgentContainer;
/** Arranque comun; cada avance crea solamente sus agentes necesarios. */
public final class Platform {
    public static AgentContainer start(Options options) {
        ProfileImpl p=new ProfileImpl();p.setParameter(Profile.MAIN_HOST,"127.0.0.1");
        p.setParameter(Profile.LOCAL_HOST,"127.0.0.1");p.setParameter(Profile.NO_MTP,"true");
        p.setParameter(Profile.GUI,Boolean.toString(options.rma()));
        AgentContainer c=jade.core.Runtime.instance().createMainContainer(p);
        if(c==null) {System.err.println("Cierre otra instancia JADE que use el puerto 1099.");stop(2);throw new IllegalStateException("No se pudo iniciar JADE");}
        return c;
    }
    public static void stop(int code) {
        jade.core.Runtime.instance().shutDown();
        new Thread(()->{try{Thread.sleep(350);}catch(InterruptedException e){Thread.currentThread().interrupt();}System.exit(code);},"cierre-demo").start();
    }
}
