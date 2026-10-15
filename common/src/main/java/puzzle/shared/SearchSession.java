package puzzle.shared;
import puzzle.model.Board;
import java.util.List;
/** Contrato minimo para integrar el motor definitivo sin acoplar heuristicas o GUI. */
public interface SearchSession {
    Board next();
    void accept(List<Board> successors);
    List<Board> solution();
    int expanded(); int generated(); int frontier(); int maxFrontier();
}
