import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

public class GameGui extends JFrame {
    public static int WRONG_EVAL = 0;
    public static int OUTSIDE_BETTER = 1;
    public static int ONLY_COUNT = 2;

    boolean drawGame = false;
    boolean selected = false;
    int selectedX;
    int selectedY;
    int boardTileSize = 100;
    int offsetY = 45;
    int offsetX = 20;
    GameBoard gameBoard;
    GameController gameController;
    AIPlayer whiteAI = new AIPlayer(AIPlayer.MINIMAX, 6, GamePiece.WHITE, WRONG_EVAL);
    AIPlayer blackAI = new AIPlayer(AIPlayer.MINIMAX, 6, GamePiece.BLACK, OUTSIDE_BETTER);
    int evalType;

    JFrame f;
    GameGui(int evalType){
        this.evalType = evalType;

        setTitle("Checkers");
        try {
            setIconImage(ImageIO.read(new File("./img/white_king.png" )));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(840, 870);
        setLocationRelativeTo(null);

        this.printMenu();

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if(drawGame) {
                    int posX = (e.getX() - offsetX) / boardTileSize;
                    int posY = (e.getY() - offsetY) / boardTileSize;
                    if(!selected) {
                        selectedX = posX;
                        selectedY = posY;
                        selected = gameBoard.getPiece(selectedX, selectedY) != null && gameBoard.getCurrentColorToMove() == (gameBoard.getPiece(selectedX, selectedY).getColor());
                    }else {
                        GamePiece piece = gameBoard.getPiece(selectedX, selectedY);
                        boolean moved = piece.move(posX, posY);
                        if (moved){
                            selected = false;
                            if (Math.abs(posX- selectedX) == 1 && piece.getType() == GamePiece.MAN)
                                gameBoard.afterMoveCalculations(piece);
                            else if(piece.getType() == GamePiece.MAN) {    // else: capture
                                gameBoard.capturePiece(gameBoard.getPiece((posX + selectedX)/2, (posY + selectedY)/2));
                                gameBoard.afterMoveCalculations(piece);
                            } else {
                                GamePiece gamePieceCaptured = gameBoard.getPieceBetween(selectedX, selectedY, posX, posY);
                                if (gamePieceCaptured!=null) {    //King capture
                                    gameBoard.capturePiece(gamePieceCaptured);
                                    gameBoard.afterMoveCalculations(piece);
                                } else {
                                    gameBoard.afterMoveCalculations(piece);
                                }
                            }
                            if (gameController != null && gameBoard.checkGameState() != gameBoard.GAME_IN_PROGRESS){
                                gameController.makeMove();
                            }
                        } else {
                            if (gameBoard.getPiece(posX, posY) != null){
                                selectedX = posX;
                                selectedY = posY;
                                selected = gameBoard.getPiece(selectedX, selectedY) != null && gameBoard.getCurrentColorToMove() == (gameBoard.getPiece(selectedX, selectedY).getColor());
                            }else
                                selected = false;
                        }
                        setTitle("Ruch: " + String.valueOf(gameBoard.getCurrentMove()));
                    }
                }
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        setLayout(null);
        setVisible(true);
    }

    private void setGameBoard(GameBoard gameBoard){
        this.gameBoard = gameBoard;
    }

    private void resetWindow(){
        this.getContentPane().removeAll();
        this.repaint();
    }

    private void printMenu() {
        drawGame = false;
        this.setTitle("Checkers Main Menu");
        JPanel jPanel = new JPanel();
        jPanel.setVisible(false);
        this.setVisible(false);

        JButton startButton = new JButton("Start");
        startButton.setBounds(320, 300, 200, 50);
        GameGui temp = this;
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jPanel.setVisible(false);
                setTitle("Checkers game");
                resetWindow();
                gameBoard = new GameBoard();
                drawGame = true;
                repaint();

                AIGui aiGui = new AIGui(temp);
            }
        });
        jPanel.add(startButton);

        JButton whiteAIConfig = new JButton("AI vs AI");
        whiteAIConfig.setBounds(320, 400, 200, 50);
        whiteAIConfig.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jPanel.setVisible(false);
                setTitle("Checkers game");
                resetWindow();
                gameBoard = new GameBoard();
                drawGame = true;
                repaint(1);

                gameController = new GameController(gameBoard, whiteAI, blackAI, temp, evalType);
            }
        });
        jPanel.add(whiteAIConfig);

        JButton ai_vs_player = new JButton("AI vs Player");
        ai_vs_player.setBounds(320, 500, 200, 50);
        ai_vs_player.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jPanel.setVisible(false);
                setTitle("Checkers game");
                resetWindow();
                gameBoard = new GameBoard();
                drawGame = true;
                repaint();

                gameController = new GameController(gameBoard, whiteAI, null, temp, evalType);
            }
        });
        jPanel.add(ai_vs_player);

        JButton player_vs_ai = new JButton("Player vs AI");
        player_vs_ai.setBounds(320, 600, 200, 50);
        player_vs_ai.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jPanel.setVisible(false);
                setTitle("Checkers game");
                resetWindow();
                gameBoard = new GameBoard();
                drawGame = true;
                repaint();

                gameController = new GameController(gameBoard, null, blackAI, temp, evalType);
            }
        });
        jPanel.add(player_vs_ai);
        this.add(jPanel);
        jPanel.setVisible(true);
        this.repaint();
        this.setVisible(true);
        this.revalidate();

    }

    private void drawBoard(Graphics g){
        g.setColor(Color.WHITE);
        g.fillRect(0,0, this.getWidth(), this.getHeight());
        g.setColor(Color.BLACK);
        g.drawRect(offsetX-1, offsetY-1, boardTileSize*8+1, boardTileSize*8+1);
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if ((i + j) % 2 == 0){
                    g.setColor(new Color(255, 255, 255));
                } else {
                    g.setColor(new Color(130, 109, 51));
                }
                g.fillRect(i * boardTileSize + offsetX, j * boardTileSize + offsetY, boardTileSize, boardTileSize);
            }
        }
        if(selected){
            g.setColor(new Color(105, 130, 51));
            g.fillRect(selectedX * boardTileSize + offsetX, selectedY * boardTileSize + offsetY, boardTileSize, boardTileSize);
            gameBoard.getPiece(selectedX, selectedY).drawAvailableMoves(g, boardTileSize, offsetX, offsetY);
        }
    }

    private void drawPieces(Graphics g){
        this.gameBoard.draw(g, boardTileSize, offsetX, offsetY);
    }

    public void paint(Graphics g) {
        if (drawGame) {
            this.drawBoard(g);
            this.drawPieces(g);
            int state = gameBoard.checkGameState();
            if (gameController!=null)
                gameController.makeMove();
            if(state != gameBoard.GAME_IN_PROGRESS) {
                String message = "";
                if(state == GameBoard.WHITE_WIN)
                    message = "White won!";
                else if(state == GameBoard.BLACK_WIN)
                    message = "Black won!";
                else if(state == GameBoard.STALEMATE)
                    message = "Stalemate!";
//                JOptionPane.showMessageDialog(null, message);
                int input = JOptionPane.showConfirmDialog(null, message);
                if(input == JOptionPane.OK_OPTION){
                    printMenu();
                }
            }
        }
    }
}
