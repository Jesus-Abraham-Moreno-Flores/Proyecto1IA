package puzzle.shared;
import puzzle.model.Board;
public record Options(boolean smoke,String board,int limit,String capture,boolean rma,boolean validate) {
    public static Options parse(String[] args) {
        boolean smoke=false,rma=false,validate=false;String board="123406758",capture=null;int limit=181440;
        for(String a:args) {
            if(a.equals("--smoke"))smoke=true;
            else if(a.equals("--rma"))rma=true;
            else if(a.equals("--validate"))validate=true;
            else if(a.startsWith("--board="))board=a.substring(8);
            else if(a.startsWith("--limit="))limit=Integer.parseInt(a.substring(8));
            else if(a.startsWith("--capture="))capture=a.substring(10);
            else throw new IllegalArgumentException("Argumento desconocido: "+a);
        }
        new Board(board);if(limit<1||limit>181440)throw new IllegalArgumentException("Limite 1..181440");
        return new Options(smoke,board,limit,capture,rma,validate);
    }
}
