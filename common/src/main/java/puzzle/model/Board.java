package puzzle.model;

import java.io.Serializable;
import java.util.*;

/** Estado inmutable; 0 representa el hueco. Meta fija 123456780. */
public final class Board implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final Board GOAL = new Board("123456780");
    private final String key;
    public Board(String key) {
        if (key == null || !key.matches("[0-8]{9}") || key.chars().distinct().count() != 9)
            throw new IllegalArgumentException("Use exactamente las fichas 0 a 8, sin repetir (ej. 123406758).");
        this.key = key;
    }
    public String key() { return key; }
    public int at(int i) { return key.charAt(i) - '0'; }
    public boolean isGoal() { return equals(GOAL); }
    public int inversions() {
        int n = 0;
        for (int i=0;i<9;i++) for (int j=i+1;j<9;j++)
            if (at(i)!=0 && at(j)!=0 && at(i)>at(j)) n++;
        return n;
    }
    public boolean isSolvable() { return inversions()%2 == 0; }
    /** Orden determinista del hueco: arriba, abajo, izquierda, derecha. */
    public List<Board> neighbors() {
        int z=key.indexOf('0'), r=z/3, c=z%3;
        List<Board> result=new ArrayList<>(4);
        int[] targets={r>0?z-3:-1,r<2?z+3:-1,c>0?z-1:-1,c<2?z+1:-1};
        for (int t:targets) if (t>=0) {
            char[] a=key.toCharArray(); a[z]=a[t]; a[t]='0'; result.add(new Board(new String(a)));
        }
        return result;
    }
    public static Board scramble(int moves, long seed) {
        if(moves<0 || moves>10000) throw new IllegalArgumentException("Mezcla entre 0 y 10000.");
        Random random=new Random(seed); Board b=GOAL, previous=null;
        for(int i=0;i<moves;i++) {
            List<Board> next=b.neighbors(); next.remove(previous);
            previous=b; b=next.get(random.nextInt(next.size()));
        }
        return b;
    }
    @Override public boolean equals(Object o) { return o instanceof Board && key.equals(((Board)o).key); }
    @Override public int hashCode() { return key.hashCode(); }
    @Override public String toString() { return key; }
}
