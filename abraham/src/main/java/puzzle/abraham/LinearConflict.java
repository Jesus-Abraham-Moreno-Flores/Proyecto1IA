package puzzle.abraham;
import puzzle.model.Board;
import java.util.*;
/** H3: Manhattan mas 2 por conflicto lineal independiente seleccionado. */
public final class LinearConflict {
    public static int estimate(Board b) {
        int manhattan=0;
        for(int i=0;i<9;i++){int t=b.at(i);if(t!=0)manhattan+=Math.abs(i/3-(t-1)/3)+Math.abs(i%3-(t-1)%3);}
        // Conflictos como aristas entre fichas (indices 0..7).
        // Maximo matching: ninguna ficha recibe penalizacion dos veces.
        List<int[]> conflicts=new ArrayList<>();
        for(int i=0;i<9;i++) for(int j=i+1;j<9;j++) {
            int a=b.at(i), c=b.at(j); if(a==0 || c==0) continue;
            int ga=a-1, gc=c-1;
            boolean row=i/3==j/3 && ga/3==i/3 && gc/3==i/3 && ga%3>gc%3;
            boolean col=i%3==j%3 && ga%3==i%3 && gc%3==i%3 && ga/3>gc/3;
            if(row || col) conflicts.add(new int[]{ga,gc});
        }
        return manhattan+2*matching(conflicts,0,0);
    }
    private static int matching(List<int[]> edges,int i,int used) {
        if(i==edges.size()) return 0;
        int best=matching(edges,i+1,used);
        int mask=(1<<edges.get(i)[0])|(1<<edges.get(i)[1]);
        if((used&mask)==0) best=Math.max(best,1+matching(edges,i+1,used|mask));
        return best;
    }
}
