package puzzle.model;

import java.util.*;

/** Verifica un certificado completo: su sufijo garantiza salida hasta la meta. */
public final class PathValidator {
    private PathValidator() { }
    public static void validate(Board start,List<Board> path) {
        if(path==null || path.isEmpty() || !path.get(0).equals(start) || !path.get(path.size()-1).isGoal())
            throw new IllegalArgumentException("La ruta debe ir del inicio a la meta.");
        Set<Board> seen=new HashSet<>(); Board previous=null;
        for(Board b:path) {
            if(!b.isSolvable() || !seen.add(b)) throw new IllegalArgumentException("Ruta insoluble o con estados repetidos.");
            if(previous!=null && !previous.neighbors().contains(b)) throw new IllegalArgumentException("Movimiento ilegal.");
            previous=b;
        }
    }
}
