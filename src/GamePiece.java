import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GamePiece {
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    public static final int MAN = 0;
    public static final int KING = 1;
    private static BufferedImage BLACK_MAN_IMG, BLACK_KING_IMG, WHITE_MAN_IMG, WHITE_KING_IMG;

    static {
        try {
            BLACK_MAN_IMG = ImageIO.read(new File("./img/black_man.png" ));
            BLACK_KING_IMG = ImageIO.read(new File("./img/black_king.png" ));
            WHITE_KING_IMG = ImageIO.read(new File("./img/white_king.png" ));
            WHITE_MAN_IMG = ImageIO.read(new File("./img/white_man.png" ));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int posX;
    private int posY;
    private int color;
    private int type;
    private BufferedImage image;
    private ArrayList<Pair<Integer, Integer>> availableMoves;

    public GamePiece(int posX, int posY, int color, int type) {
        this.posX = posX;
        this.posY = posY;
        this.color = color;
        this.type = type;
        this.availableMoves = new ArrayList<>();

        if (this.color == WHITE){
            if(this.type == MAN)
                this.image = WHITE_MAN_IMG;
            else
                this.image = WHITE_KING_IMG;
        } else {
            if (this.type == MAN)
                this.image = BLACK_MAN_IMG;
            else
                this.image = BLACK_KING_IMG;
        }
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void addAvailableMove(Pair<Integer, Integer> move){
        this.availableMoves.add(move);
    }

    public ArrayList<Pair<Integer, Integer>> getAvailableMoves(){
        return this.availableMoves;
    }

    public void resetAvailableMoves(){
        this.availableMoves = new ArrayList<>();
    }

    public boolean move(int x, int y){
        for (int i = 0; i < this.availableMoves.size(); i++){
            if(this.availableMoves.get(i).getKey() == x && this.availableMoves.get(i).getValue() == y) {
                this.posX = x;
                this.posY = y;
                return true;
            }
        }
        return false;
    }

    public void draw(Graphics g, int tileSize, int offsetX, int offsetY) {
        g.drawImage(this.image, this.posX*tileSize+offsetX, this.posY*tileSize+offsetY, null);
    }

    public void drawAvailableMoves(Graphics g, int tileSize, int offsetX, int offsetY){
        int size = 20;
        g.setColor(new Color(105, 130, 51));
        for (int i = 0; i < this.availableMoves.size(); i++) {
            Pair<Integer, Integer> move = availableMoves.get(i);
            g.fillOval(move.getKey()*tileSize+offsetX+(tileSize/2)-(size/2), move.getValue()*tileSize+offsetY+(tileSize/2)-(size/2), size, size);
        }
    }

    public void upgrade(){
        if(this.color == WHITE && this.posY == 0)
            this.type = KING;
        else if(this.color == BLACK && this.posY == 7)
            this.type = KING;
    }
}
