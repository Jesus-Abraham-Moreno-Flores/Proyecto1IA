package puzzle.support;

import puzzle.model.Board;
import puzzle.agents.Protocol.Progress;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.function.Consumer;
import javax.imageio.ImageIO;

/** Vista minima de prueba: cada boton solicita una iteracion real al jugador. */
public final class PlayerTestPanel extends JPanel {
    private final JTextField input=new JTextField("123406758",10);
    private final JButton restart=new JButton("Iniciar");
    private final JLabel message=new JLabel("Iniciando JADE...");
    private final String[] players;
    private final PlayerCard[] cards;

    public PlayerTestPanel(String[] players,Consumer<String> onStart,Consumer<String> onStep){
        this.players=players;cards=new PlayerCard[players.length];
        setLayout(new BorderLayout(12,16));setBackground(Color.WHITE);setBorder(new EmptyBorder(20,20,20,20));
        JPanel top=new JPanel(new BorderLayout(0,12));top.setOpaque(false);
        JLabel title=new JLabel("Proyecto IA",SwingConstants.CENTER);title.setFont(new Font("SansSerif",Font.BOLD,24));top.add(title,BorderLayout.NORTH);
        JPanel controls=new JPanel(new FlowLayout(FlowLayout.CENTER));controls.setOpaque(false);
        controls.add(new JLabel("Tablero:"));controls.add(input);controls.add(restart);top.add(controls);add(top,BorderLayout.NORTH);
        JPanel boards=new JPanel(new GridLayout(1,players.length,20,0));boards.setOpaque(false);
        for(int i=0;i<players.length;i++){cards[i]=new PlayerCard(players[i],onStep);boards.add(cards[i]);}
        add(boards);message.setHorizontalAlignment(SwingConstants.CENTER);add(message,BorderLayout.SOUTH);
        restart.setEnabled(false);restart.addActionListener(e->onStart.accept(input.getText().trim()));
    }
    public void ready(){restart.setEnabled(true);message.setText(" ");}
    public void begin(Board board){input.setText(board.key());for(PlayerCard c:cards)c.reset(board);message.setText(" ");}
    public void message(String text){message.setText(text);}
    public void update(Progress p){for(int i=0;i<players.length;i++)if(players[i].equals(p.player()))cards[i].update(p);}
    /** Solo para la comprobacion de captura: pulsa los mismos botones que el usuario. */
    public void clickNext(){for(PlayerCard c:cards)if(c.next.isEnabled())c.next.doClick();}
    public void capture(String path){try{BufferedImage b=new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);Graphics2D g=b.createGraphics();printAll(g);g.dispose();ImageIO.write(b,"png",new File(path));}catch(IOException e){throw new UncheckedIOException(e);}}

    private static class PlayerCard extends JPanel {
        final JLabel[] tiles=new JLabel[9];
        final JLabel status=new JLabel("Iteracion 0",SwingConstants.CENTER);
        final JButton next=new JButton("Siguiente iteracion");
        PlayerCard(String player,Consumer<String> onStep){
            setOpaque(false);setLayout(new BorderLayout(0,10));
            JLabel title=new JLabel(player,SwingConstants.CENTER);title.setFont(new Font("SansSerif",Font.BOLD,18));add(title,BorderLayout.NORTH);
            JPanel grid=new JPanel(new GridLayout(3,3,3,3));grid.setBackground(Color.WHITE);grid.setPreferredSize(new Dimension(240,240));
            for(int i=0;i<9;i++){tiles[i]=new JLabel("",SwingConstants.CENTER);tiles[i].setOpaque(true);tiles[i].setBackground(new Color(240,240,240));tiles[i].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));tiles[i].setFont(new Font("SansSerif",Font.PLAIN,28));grid.add(tiles[i]);}
            add(grid);JPanel bottom=new JPanel(new GridLayout(2,1,0,6));bottom.setOpaque(false);bottom.add(status);bottom.add(next);add(bottom,BorderLayout.SOUTH);
            next.setEnabled(false);next.addActionListener(e->{next.setEnabled(false);onStep.accept(player);});show(new Board("123406758"));
        }
        void reset(Board b){show(b);status.setText("Iteracion 0");next.setEnabled(false);}
        void show(Board b){for(int i=0;i<9;i++){int t=b.at(i);tiles[i].setText(t==0?"":Integer.toString(t));}}
        void update(Progress p){
            if(p.board()!=null)show(p.board());
            boolean waiting=p.status().equals("LISTO");
            if(p.status().equals("SOLUCION VALIDADA"))status.setText("Iteracion "+p.expanded()+" - Meta alcanzada");
            else if(waiting||p.status().equals("BUSCANDO")||p.status().equals("VALIDANDO RUTA"))status.setText("Iteracion "+p.expanded());
            else status.setText(p.status());
            next.setEnabled(waiting);
        }
    }
}
