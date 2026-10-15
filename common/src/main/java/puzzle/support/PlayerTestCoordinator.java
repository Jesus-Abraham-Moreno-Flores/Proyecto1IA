package puzzle.support;
import jade.gui.GuiEvent;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import puzzle.shared.*;
import puzzle.model.*;
import puzzle.agents.Protocol;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import static puzzle.agents.Protocol.*;

/** Coordinador de soporte local. El boton manda Step; la busqueda vive en cada jugador. */
public class PlayerTestCoordinator extends jade.gui.GuiAgent {
    private Options options;private PlayerTestPanel panel;private JFrame frame;
    private String[] players;private String member,run;private Board initial;
    private final Set<String> ready=new HashSet<>(),finished=new HashSet<>(),pending=new HashSet<>();
    private boolean busy,failed;
    private final Map<String,Integer> clicks=new HashMap<>();
    private WakerBehaviour watchdog;
    @Override protected void setup(){
        options=(Options)getArguments()[0];players=(String[])getArguments()[1];member=(String)getArguments()[2];
        if(!options.smoke())try{SwingUtilities.invokeAndWait(()->{
            panel=new PlayerTestPanel(players,text->{GuiEvent e=new GuiEvent(this,1);e.addParameter(text);postGuiEvent(e);},player->{GuiEvent e=new GuiEvent(this,3);e.addParameter(player);postGuiEvent(e);});
            frame=new JFrame("Proyecto IA");frame.setContentPane(panel);frame.pack();frame.setLocationRelativeTo(null);frame.setResizable(false);frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){postGuiEvent(new GuiEvent(this,2));}});frame.setVisible(true);
        });}catch(Exception e){throw new IllegalStateException(e);}
        addBehaviour(new CyclicBehaviour(){public void action(){
            ACLMessage m=receive(MessageTemplate.MatchOntology(ONTOLOGY));if(m==null){block();return;}
            try{
                Object data=m.getContentObject();String sender=m.getSender().getLocalName();
                if(data instanceof Ready r&&r.name().equals(sender)&&(sender.equals("generador")||Arrays.asList(players).contains(sender))){
                    if(ready.add(sender)&&ready.size()==players.length+1){ui(()->panel.ready());begin(options.board());}
                }else if(busy&&Objects.equals(run,m.getConversationId())){
                    if(data instanceof Progress p&&sender.equals(p.player())&&Arrays.asList(players).contains(sender)){
                        if(options.capture()!=null&&(p.status().equals("LISTO")||p.status().equals("SOLUCION VALIDADA"))&&p.expanded()!=clicks.getOrDefault(sender,0))throw new IllegalStateException("Una iteracion debe corresponder a un clic: "+sender);
                        ui(()->panel.update(p));
                        if(p.status().equals("LISTO")){
                            pending.remove(sender);if(pending.isEmpty())cancelWatchdog();
                            System.out.println("PASO "+sender+" iteracion="+p.expanded()+" tablero="+p.board());
                        }else if(!p.status().equals("BUSCANDO")&&!p.status().equals("VALIDANDO RUTA")){
                            pending.remove(sender);
                            if(!p.path().isEmpty())PathValidator.validate(initial,p.path());else failed=true;
                            System.out.println("RESULTADO "+sender+" "+p.heuristic()+" estado="+p.status()+" extraidos="+p.expanded()+" movimientos="+(p.path().size()-1));
                            if(!p.path().isEmpty())System.out.println("RUTA "+sender+" "+p.path());
                            finished.add(sender);if(finished.size()==players.length)complete();else if(pending.isEmpty())cancelWatchdog();
                        }
                    }else if(data instanceof Failure f&&sender.equals("generador")){
                        System.out.println("RECHAZADO: "+f.reason());busy=false;cancelWatchdog();ui(()->panel.message(f.reason()));if(options.smoke())shutdown(2);
                    }
                }
            }catch(Exception e){e.printStackTrace();if(options.smoke()||options.capture()!=null)shutdown(2);else ui(()->panel.message(e.getMessage()));}
        }});
        addBehaviour(new WakerBehaviour(this,15000){protected void onWake(){if(ready.size()<players.length+1){System.err.println("Agentes no listos");if(options.smoke())shutdown(2);else ui(()->panel.message("No se pudo iniciar JADE."));}}});
        if(options.capture()!=null)addBehaviour(new TickerBehaviour(this,250){protected void onTick(){ui(()->panel.clickNext());}});
    }
    @Override protected void onGuiEvent(GuiEvent e){
        if(e.getType()==2){shutdown(0);return;}
        if(ready.size()!=players.length+1)return;
        if(e.getType()==1)begin((String)e.getParameter(0));
        else if(e.getType()==3&&busy){String player=(String)e.getParameter(0);if(!finished.contains(player)&&pending.add(player)){clicks.merge(player,1,Integer::sum);armWatchdog();Protocol.send(this,player,ACLMessage.REQUEST,run,new Step());}}
    }
    private void begin(String text){
        try{
            Board board=new Board(text);initial=board;run=UUID.randomUUID().toString();finished.clear();clicks.clear();pending.clear();pending.addAll(Arrays.asList(players));failed=false;busy=true;
            ui(()->panel.begin(board));armWatchdog();
            Protocol.send(this,"generador",ACLMessage.REQUEST,run,new Start(board,options.limit(),!options.smoke()));
        }catch(IllegalArgumentException e){ui(()->panel.message(e.getMessage()));if(options.smoke())shutdown(2);}
    }
    private void complete(){
        busy=false;cancelWatchdog();
        if(options.smoke()){System.out.println(failed?"SMOKE FAIL":"SMOKE PASS "+member);shutdown(failed?2:0);}
        else if(options.capture()!=null)addBehaviour(new WakerBehaviour(this,300){protected void onWake(){try{SwingUtilities.invokeAndWait(()->panel.capture(options.capture()));System.out.println("BOTONES PASS "+member+": una extraccion por clic hasta meta");shutdown(failed?2:0);}catch(Exception e){e.printStackTrace();shutdown(2);}}});
    }
    private void armWatchdog(){cancelWatchdog();watchdog=new WakerBehaviour(this,15000){protected void onWake(){if(busy){busy=false;ui(()->panel.message("Sin respuesta del agente."));if(options.smoke()||options.capture()!=null)shutdown(2);}}};addBehaviour(watchdog);}
    private void cancelWatchdog(){if(watchdog!=null){removeBehaviour(watchdog);watchdog=null;}}
    private void ui(Runnable r){if(panel!=null)SwingUtilities.invokeLater(r);}
    private void shutdown(int code){ui(()->frame.dispose());Platform.stop(code);}
}
