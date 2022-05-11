import javax.swing.*;
import java.awt.*;

public class GameBoardPreview extends JFrame {
    boolean selected = false;
    int selectedX;
    int selectedY;
    int boardTileSize = 100;
    int offsetY = 45;
    int offsetX = 20;

    GameBoard gameBoard;
    JFrame f;

    public GameBoardPreview(GameBoard gameBoard){
        this.gameBoard = gameBoard;

        this.setTitle("Game board preview");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(840, 870);
        setLocationRelativeTo(null);

        this.setVisible(true);
        this.repaint();
    }

    private void drawBoard(Graphics g){
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
        this.drawBoard(g);
        this.drawPieces(g);
    }
}
