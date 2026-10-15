package puzzle.support;
import puzzle.agents.Protocol;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.*;
import puzzle.model.*;
import java.util.*;
import static puzzle.agents.Protocol.*;

/** SOPORTE DE PRUEBA para jugadores; no es el avance del generador de Emiliano. */
public class GraphFixtureAgent extends Agent {
    private String[] players;
    private String run;
    private Board initial;
    private final Map<Board,List<Board>> graph=new HashMap<>();
    @Override protected void setup() {
        players=(String[])getArguments()[0];
        System.out.println("SOPORTE: GraphFixtureAgent local para probar jugadores.");
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage m=receive(MessageTemplate.MatchOntology(ONTOLOGY));
                if(m==null) { block(); return; }
                try {
                    Object data=m.getContentObject();
                    String sender=m.getSender().getLocalName();
                    if(data instanceof Start start && sender.equals("gui") && m.getPerformative()==ACLMessage.REQUEST) {
                        if(!start.board().isSolvable()) { reply(myAgent,m,ACLMessage.REFUSE,new Failure("Inversiones impares: tablero insoluble.")); return; }
                        if(start.limit()<1 || start.limit()>181440) throw new IllegalArgumentException("Limite fuera de 1..181440.");
                        run=m.getConversationId(); initial=start.board(); graph.clear();
                        reply(myAgent,m,ACLMessage.AGREE,start);
                        for(String player:players) Protocol.send(myAgent,player,ACLMessage.INFORM,run,start);
                        System.out.println("GENERADOR inicio="+initial+" inversiones="+initial.inversions()+" conversacion="+run);
                    } else if(Objects.equals(run,m.getConversationId()) && Arrays.asList(players).contains(sender)
                              && m.getPerformative()==ACLMessage.REQUEST) {
                        if(data instanceof Expand e) {
                            if(!e.board().isSolvable()) throw new IllegalArgumentException("Nodo de otra componente.");
                            List<Board> next=graph.computeIfAbsent(e.board(),b->b.neighbors());
                            reply(myAgent,m,ACLMessage.INFORM,new Successors(e.board(),next));
                        } else if(data instanceof Candidate c) {
                            PathValidator.validate(initial,c.path());
                            reply(myAgent,m,ACLMessage.CONFIRM,new Approved(c.path()));
                            System.out.println("GENERADOR ruta validada: "+sender+" movimientos="+(c.path().size()-1)+" nodos_cache="+graph.size());
                        }
                    }
                } catch(Exception e) { reply(myAgent,m,ACLMessage.FAILURE,new Failure(e.getMessage())); }
            }
        });
        Protocol.send(this,"gui",ACLMessage.INFORM,"ready",new Ready(getLocalName()));
    }
}
