package puzzle.abraham;

import puzzle.model.*;

import java.util.*;

/** Pruebas sin dependencias adicionales. Ejecutar build.ps1 -Mode test. */
public final class Verification {
    private static int checks;
    private static void check(boolean ok,String description) {checks++;if(!ok)throw new AssertionError(description);}
    private static void rejects(Runnable action,String description) {
        try {action.run();} catch(IllegalArgumentException expected) {checks++;return;}
        throw new AssertionError("Debio rechazar: "+description);
    }
    public static void main(String[] args) {
        rejects(()->new Board("112345678"),"ficha repetida");
        rejects(()->new Board("12345678"),"faltan fichas");
        rejects(()->new Board("123456789"),"valor fuera de rango");
        check(!new Board("123456870").isSolvable(),"paridad impar");
        check(Board.GOAL.isSolvable(),"meta soluble");
        for(String h:List.of("H3")) check(LinearConflict.estimate(Board.GOAL)==0,"meta h=0 "+h);
        Board conflict=new Board("213456780");
        check(LinearConflict.estimate(conflict)==4,"H3 conflicto simple");
        check(LinearConflict.estimate(new Board("321456780"))==6,"conflictos solapados sin doble conteo");
        Board col=new Board("723456180");
        check(LinearConflict.estimate(col)>4,"conflicto en columna");
        check(Board.scramble(24,42).equals(Board.scramble(24,42)),"semilla reproducible");
        rejects(()->PathValidator.validate(Board.GOAL,List.of(Board.GOAL,Board.GOAL)),"repeticion");
        rejects(()->PathValidator.validate(new Board("123406758"),List.of(new Board("123406758"),Board.GOAL)),"salto ilegal");
        // BFS de referencia desde meta: distancias optimas de toda la componente.
        Map<Board,Integer> distance=new HashMap<>(); ArrayDeque<Board> queue=new ArrayDeque<>();
        distance.put(Board.GOAL,0);queue.add(Board.GOAL);int diameter=0;
        while(!queue.isEmpty()) {
            Board b=queue.remove();int d=distance.get(b);diameter=Math.max(diameter,d);
            int h1=0,h2=0,h3=LinearConflict.estimate(b);
            check(b.isSolvable(),"paridad conservada");
            check(h1<=h2 && h2<=h3 && h3<=d,"cotas heuristicas en "+b+" distancia="+d+" h3="+h3);
            for(Board next:b.neighbors()) if(!distance.containsKey(next)){distance.put(next,d+1);queue.add(next);}
        }
        check(distance.size()==181440,"componente de 181440 estados");check(diameter==31,"diametro 31");
        System.out.println("PASS: BFS exhaustivo 181440 estados; 0 <= h3 <= distancia optima; diametro=31.");
        for(String key:List.of("123456780","123406758","724506831","867254301")) for(String h:List.of("H3")) {
            Board start=new Board(key); GreedySearch search=new GreedySearch(start,LinearConflict::estimate);
            for(;;) {
                Board b=search.next();check(b!=null,"GBFS encuentra solucion");
                if(b.isGoal())break;
                search.accept(b.neighbors());check(search.expanded()<=181440,"terminacion en espacio finito");
            }
            List<Board> path=search.solution();PathValidator.validate(start,path);
            System.out.printf("PASS GBFS %s %s extraidos=%d movimientos=%d optimo_referencia=%d%n",key,h,search.expanded(),path.size()-1,distance.get(start));
        }
        System.out.println("VERIFICATION PASS Abraham: "+checks+" comprobaciones.");
    }
}
