package puzzle.shared;
import puzzle.model.*;
import java.util.*;
public class CommonVerification {
    public static void main(String[] args) {
        int checks=0;
        for(int seed=0;seed<1000;seed++) {
            Board b=Board.scramble(80,seed);
            if(!b.isSolvable())throw new AssertionError("Mezcla insoluble");checks++;
            for(Board n:b.neighbors()){if(!n.isSolvable()||!n.neighbors().contains(b))throw new AssertionError("Arista invalida");checks++;}
        }
        if(new Board("123456870").isSolvable())throw new AssertionError("Paridad");
        try{new Board("112345678");throw new AssertionError("Duplicados");}catch(IllegalArgumentException expected){}
        try{PathValidator.validate(Board.GOAL,List.of(Board.GOAL,Board.GOAL));throw new AssertionError("Repeticion");}catch(IllegalArgumentException expected){}
        System.out.println("COMMON PASS: "+checks+" comprobaciones de mezcla soluble, vecinos y validacion.");
    }
}
