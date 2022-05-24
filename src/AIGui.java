import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AIGui extends JFrame {
    GameGui gameGui;
    JFrame f;

    public AIGui(GameGui gameGui){
        this.gameGui = gameGui;

        this.setTitle("AI Menu");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setSize(250, 140);
        setLocationRelativeTo(gameGui);

        JPanel panel = new JPanel();

        JButton showButton = new JButton("Show best move");
        showButton.setBounds(30, 10, 200, 20);
        showButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GameBoard gameBoardToShow = Engine.startMinMax(gameGui.gameBoard, 5);
                GameBoardPreview gameBoardPreview = new GameBoardPreview(gameBoardToShow);
            }
        });

        JButton makeMoveButton = new JButton("Make best move");
        makeMoveButton.setBounds(30, 70, 200, 20);
        makeMoveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GameBoard gameBoardToShow = Engine.startMinMax(gameGui.gameBoard, 5);
                gameGui.gameBoard = gameBoardToShow;
                gameGui.repaint();
                System.out.println(gameGui.gameBoard.getCurrentMove());
            }
        });

        JButton showAB = new JButton("Show best Alpha-Beta");
        showAB.setBounds(30, 130, 200, 20);
        showAB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GameBoard gameBoardToShow = Engine.startAlphaBeta(gameGui.gameBoard, 5);
                GameBoardPreview gameBoardPreview = new GameBoardPreview(gameBoardToShow);
            }
        });

        panel.add(showButton);
        panel.add(makeMoveButton);
        panel.add(showAB);
        this.add(panel);
        this.repaint();
        this.setVisible(true);
    }
}
