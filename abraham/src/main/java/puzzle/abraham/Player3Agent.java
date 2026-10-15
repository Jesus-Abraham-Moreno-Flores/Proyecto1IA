package puzzle.abraham;
import puzzle.agents.Protocol;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.*;
import puzzle.model.Board;

import java.util.*;
import static puzzle.agents.Protocol.*;

/** Avance Abraham: jugador3 utiliza el motor propio y H3. */
public class Player3Agent extends Agent {
    
    private GreedySearch search;
    private String run;
    private Board current;
    private int limit;
    private long started;
    private boolean active,manual,waiting;
    @Override protected void setup() {
        // Unica instancia: jugador3 con H3.
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage m=receive(MessageTemplate.MatchOntology(ONTOLOGY));
                if(m==null) { block(); return; }
                String sender=m.getSender().getLocalName();
                if(!sender.equals("generador")&&!sender.equals("gui"))return;
                try {
                    Object data=m.getContentObject();
                    if(data instanceof Start s && sender.equals("generador") && m.getPerformative()==ACLMessage.INFORM) {
                        run=m.getConversationId(); limit=s.limit(); started=System.nanoTime();
                        search=new GreedySearch(s.board(),LinearConflict::estimate);active=true;manual=s.manual();waiting=manual;current=s.board();
                        if(manual)progress("LISTO",List.of());else advance();
                    } else if(active && Objects.equals(run,m.getConversationId())) {
                        if(data instanceof Step && sender.equals("gui") && m.getPerformative()==ACLMessage.REQUEST){
                            if(manual&&waiting){waiting=false;advance();}
                        }else if(data instanceof Successors s && sender.equals("generador") && m.getPerformative()==ACLMessage.INFORM) {
                            if(!s.source().equals(current)) throw new IllegalStateException("Respuesta para otro nodo.");
                            search.accept(s.boards());if(manual){waiting=true;progress("LISTO",List.of());}else advance();
                        } else if(data instanceof Approved a && sender.equals("generador") && m.getPerformative()==ACLMessage.CONFIRM) {
                            active=false; progress("SOLUCION VALIDADA",a.path());
                        } else if(data instanceof Failure f && sender.equals("generador")) { active=false; progress("ERROR: "+f.reason(),List.of()); }
                    }
                } catch(Exception e) { active=false; if(search!=null) progress("ERROR: "+e.getMessage(),List.of()); }
            }
        });
        Protocol.send(this,"gui",ACLMessage.INFORM,"ready",new Ready(getLocalName()));
    }
    private void advance() {
        // El limite cuenta nodos extraidos de la frontera, incluida la meta.
        if(search.expanded()>=limit) { active=false; progress("LIMITE ALCANZADO",List.of()); return; }
        current=search.next();
        if(current==null) { active=false; progress("FRONTERA AGOTADA",List.of()); return; }
        if(current.isGoal()) {
            progress("VALIDANDO RUTA",List.of());
            Protocol.send(this,"generador",ACLMessage.REQUEST,run,new Candidate(search.solution()));
        } else {
            if(search.expanded()==1 || search.expanded()%50==0) progress("BUSCANDO",List.of());
            Protocol.send(this,"generador",ACLMessage.REQUEST,run,new Expand(current));
        }
    }
    private void progress(String status,List<Board> path) {
        Protocol.send(this,"gui",ACLMessage.INFORM,run,new Progress(getLocalName(),"H3",current,
            search.expanded(),search.generated(),search.frontier(),search.maxFrontier(),
            (System.nanoTime()-started)/1_000_000,status,path));
    }
}
