package puzzle.abraham;
import puzzle.shared.SearchSession;
import java.util.function.ToIntFunction;

import puzzle.model.Board;
import java.util.*;

/** Motor incremental: los sucesores se suministran desde AgenteGenerador. */
public final class GreedySearch implements SearchSession {
    private record Node(Board board, Node parent, int h, long order) { }
    private final ToIntFunction<Board> heuristic;
    private final PriorityQueue<Node> open=new PriorityQueue<>(Comparator.comparingInt(Node::h).thenComparingLong(Node::order));
    private final Set<Board> discovered=new HashSet<>();
    private final Set<Board> closed=new HashSet<>();
    private Node current;
    private long sequence=0;
    private int maxOpen=1;
    public GreedySearch(Board start,ToIntFunction<Board> heuristic) {
        this.heuristic=heuristic; discovered.add(start);
        open.add(new Node(start,null,heuristic.applyAsInt(start),sequence++));
    }
    public Board next() {
        if(current!=null) throw new IllegalStateException("Faltan los sucesores del nodo actual.");
        current=open.poll();
        if(current==null) return null;
        closed.add(current.board); return current.board;
    }
    public void accept(List<Board> successors) {
        if(current==null) throw new IllegalStateException("No hay nodo en expansion.");
        for(Board b:successors) if(discovered.add(b))
            open.add(new Node(b,current,heuristic.applyAsInt(b),sequence++));
        maxOpen=Math.max(maxOpen,open.size()); current=null;
    }
    public List<Board> solution() {
        if(current==null || !current.board.isGoal()) throw new IllegalStateException("No se ha encontrado la meta.");
        LinkedList<Board> path=new LinkedList<>();
        for(Node n=current;n!=null;n=n.parent) path.addFirst(n.board);
        return List.copyOf(path);
    }
    public int expanded() { return closed.size(); }
    public int generated() { return discovered.size(); }
    public int frontier() { return open.size(); }
    public int maxFrontier() { return maxOpen; }
}
