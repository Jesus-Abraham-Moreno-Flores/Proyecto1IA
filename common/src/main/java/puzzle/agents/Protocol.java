package puzzle.agents;

import jade.core.*;
import jade.lang.acl.*;
import puzzle.model.Board;
import java.io.*;
import java.util.*;

/** Protocolo propio sobre FIPA-ACL; no implementa el protocolo FIPA Request completo. */
public final class Protocol {
    public static final String ONTOLOGY="8puzzle-v1";
    public static final String[] PLAYERS={"jugador1","jugador2","jugador3"};
    public record Generate(int moves,long seed) implements Serializable { }
    public record Inspect(Board board) implements Serializable { }
    public record Generated(Board board,int inversions,int degree,String origin) implements Serializable { }
    public record Ready(String name) implements Serializable { }
    public record Start(Board board,int limit,boolean manual) implements Serializable {
        public Start(Board board,int limit){this(board,limit,false);}
    }
    public record Step() implements Serializable { }
    public record Expand(Board board) implements Serializable { }
    public record Successors(Board source,List<Board> boards) implements Serializable { }
    public record Candidate(List<Board> path) implements Serializable { }
    public record Approved(List<Board> path) implements Serializable { }
    public record Failure(String reason) implements Serializable { }
    public record Progress(String player,String heuristic,Board board,int expanded,int generated,int frontier,
                           int maxFrontier,long millis,String status,List<Board> path) implements Serializable { }
    private Protocol() { }
    public static void send(Agent agent,String to,int performative,String run,Serializable data) {
        ACLMessage m=new ACLMessage(performative);
        m.addReceiver(new AID(to,AID.ISLOCALNAME)); m.setOntology(ONTOLOGY); m.setConversationId(run);
        try { m.setContentObject(data); } catch(IOException e) { throw new IllegalStateException(e); }
        agent.send(m);
    }
    public static void reply(Agent agent,ACLMessage request,int performative,Serializable data) {
        ACLMessage m=request.createReply(); m.setPerformative(performative);
        try { m.setContentObject(data); } catch(IOException e) { throw new IllegalStateException(e); }
        agent.send(m);
    }
}
