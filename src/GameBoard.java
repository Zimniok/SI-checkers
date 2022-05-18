import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class GameBoard {
    public static int WHITE_WIN = 1;
    public static int BLACK_WIN = -1;
    public int GAME_IN_PROGRESS = 0;
    public static int STALEMATE = 2;

    private ArrayList<GamePiece> gamePieces = new ArrayList<>();
    private int currentMove;
    private int availableCapturesCount;
    private int availableMovesCount;
    private ArrayList<Tree<Pair<Integer, Integer>>> availableCaptures;
    private int kingMoves = 0;
    private double evaluation;

    public GameBoard(){
        this.availableCaptures = new ArrayList<>();
        this.currentMove = 0;

        this.generateWhitePieces();
        //gamePieces.add(new GamePiece(3, 6, GamePiece.WHITE, GamePiece.KING));
        this.generateBlackPieces();

        this.calculateAvailableCaptures(null);
        if (this.availableCapturesCount == 0)
            this.calculateAvailableMoves();
        System.out.println(this.evaluate());

    }

    public GameBoard(GameBoard gameBoard){
        this.availableCaptures = new ArrayList<>();
        this.currentMove = gameBoard.currentMove;
        this.gamePieces = new ArrayList<>();
        for(GamePiece gamePiece: gameBoard.gamePieces){
            this.gamePieces.add(new GamePiece(gamePiece));
        }

        this.calculateAvailableCaptures(null);
        if (this.availableCapturesCount == 0)
            this.calculateAvailableMoves();
    }

    public void change(GameBoard gameBoard){
        this.availableCaptures = gameBoard.availableCaptures;
        this.currentMove = gameBoard.currentMove;
        this.gamePieces = gameBoard.gamePieces;
        this.availableCapturesCount = gameBoard.availableCapturesCount;

        if (this.availableCapturesCount == 0)
            this.calculateAvailableMoves();
    }

    private void generateWhitePieces(){
        for(int i = 0; i < 8; i++){
            gamePieces.add(new GamePiece(((i%4)*2+1) - Math.floorDiv(i, 4), 6 + Math.floorDiv(i, 4), GamePiece.WHITE, GamePiece.MAN));
        }
    }
    private void generateBlackPieces(){
        for(int i = 0; i < 8; i++){
            gamePieces.add(new GamePiece(((i%4)*2+1) - Math.floorDiv(i, 4), Math.floorDiv(i, 4), GamePiece.BLACK, GamePiece.MAN));
        }
    }

    public void draw(Graphics g, int tileSize, int offsetX, int offsetY){
        for (GamePiece gamePiece : gamePieces) {
            gamePiece.draw(g, tileSize, offsetX, offsetY);
        }
    }

    public GamePiece getPiece(int x, int y){
        for (GamePiece gamePiece : gamePieces) {
            if (gamePiece.getPosX() == x && gamePiece.getPosY() == y)
                return gamePiece;
        }
        return null;
    }

    public void afterMoveCalculations(GamePiece piece){

        if (piece != null && availableCaptures.size() > 0){
            for (int i = availableCaptures.size() - 1; i >= 0; i--) {
                if (this.getPiece(availableCaptures.get(i).getNode().data.getKey(), availableCaptures.get(i).getNode().data.getValue()) == piece){
                    for(int j = 0; j < availableCaptures.get(i).getChildren().size(); j ++) {
                        piece.addAvailableMove(availableCaptures.get(i).getChildren().get(j).data);
                        availableCaptures.add(new Tree<>(availableCaptures.get(i).getChildren().get(j)));
                    }
                }

                availableCaptures.remove(i);
            }
            if (this.availableCaptures.size() == 0){
                piece.upgrade();
                this.currentMove++;
                this.calculateAvailableCaptures(null);
                if (this.availableCapturesCount == 0)
                    this.calculateAvailableMoves();
            }
        } else if(piece != null && piece.getType() == GamePiece.MAN){
            piece.upgrade();
            this.currentMove++;
            this.calculateAvailableCaptures(null);
            if (this.availableCapturesCount == 0)
                this.calculateAvailableMoves();
        } else {
            this.currentMove++;
            this.calculateAvailableCaptures(null);
            if (this.availableCapturesCount == 0)
                this.calculateAvailableMoves();
        }

        if(piece !=null && piece.getType() == GamePiece.KING){
            this.kingMoves++;
        }
        else {
            this.kingMoves = 0;
        }
        int gameState = this.checkGameState();
        if(gameState == WHITE_WIN){
            System.out.println("White won");
        } else if(gameState == BLACK_WIN){
            System.out.println("Black won");
        } else if(gameState == STALEMATE) {
            System.out.println("Stalemate");
        }

        System.out.println(this.evaluate());
    }

    private void calculateAvailableCaptures(GamePiece piece) {
        for(int i = 0; i < this.gamePieces.size(); i++){
            this.gamePieces.get(i).resetAvailableMoves();
        }
        this.availableCapturesCount = 0;
        ArrayList<GamePiece> pieces = this.gamePieces;
        if(piece!=null) {
            pieces = new ArrayList<>();
            pieces.add(piece);
        }

        for (GamePiece p : pieces) {
            if (p.getColor() == this.getCurrentColorToMove()) {
                if (availableCaptures.size() == 0) {
                    availableCaptures = new ArrayList<>();
                    availableCaptures.add(calculateCapturesFromPosition(p.getPosX(), p.getPosY(), p.getColor(), new ArrayList<>(), p.getType(), p));
                } else {
                    Tree<Pair<Integer, Integer>> temp = calculateCapturesFromPosition(p.getPosX(), p.getPosY(), p.getColor(), new ArrayList<>(), p.getType(), p);
                    if (temp.calculateDepthFromNode(temp.getNode()) > availableCaptures.get(0).calculateDepthFromNode(availableCaptures.get(0).getNode())) {
                        availableCaptures.clear();
                        availableCaptures.add(temp);
                    } else if (temp.calculateDepthFromNode(temp.getNode()) == availableCaptures.get(0).calculateDepthFromNode(availableCaptures.get(0).getNode())) {
                        availableCaptures.add(temp);
                    }
                }
            }

        }

        for(int i = availableCaptures.size() - 1; i >= 0; i--) {
            ArrayList<Tree.Node<Pair<Integer, Integer>>> children = (ArrayList<Tree.Node<Pair<Integer, Integer>>>) availableCaptures.get(i).getChildren();
            for (Tree.Node<Pair<Integer, Integer>> child : children) {
                this.getPiece(availableCaptures.get(i).getNode().data.getKey(), availableCaptures.get(i).getNode().data.getValue()).addAvailableMove(child.data);
                availableCaptures.add(new Tree<>(child));
            }
            availableCaptures.remove(i);
        }

    }

    private Tree<Pair<Integer, Integer>> calculateCapturesFromPosition(int posX, int posY, int color, ArrayList<GamePiece> gamePiecesTaken, int type, GamePiece king) {
        Tree<Pair<Integer, Integer>> allAvailableCaptures = new Tree<>(new Pair<>(posX, posY));
        if(type == GamePiece.MAN) {
            if (isWithinBoard(posX - 2, posY - 2) && this.getPiece(posX - 1, posY - 1) != null && !gamePiecesTaken.contains((this.getPiece(posX - 1, posY - 1))) && color != this.getPiece(posX - 1, posY - 1).getColor() && this.getPiece(posX - 2, posY - 2) == null) {
                gamePiecesTaken.add(this.getPiece(posX - 1, posY - 1));
                addAvailableCaptures(posX - 2, posY - 2, allAvailableCaptures, color, gamePiecesTaken, type);
                gamePiecesTaken.remove(this.getPiece(posX - 1, posY - 1));
            }
            if (isWithinBoard(posX + 2, posY - 2) && this.getPiece(posX + 1, posY - 1) != null && !gamePiecesTaken.contains(this.getPiece(posX + 1, posY - 1)) && color != this.getPiece(posX + 1, posY - 1).getColor() && this.getPiece(posX + 2, posY - 2) == null) {
                gamePiecesTaken.add(this.getPiece(posX + 1, posY - 1));
                addAvailableCaptures(posX + 2, posY - 2, allAvailableCaptures, color, gamePiecesTaken, type);
                gamePiecesTaken.remove(this.getPiece(posX + 1, posY - 1));
            }
            if (isWithinBoard(posX - 2, posY + 2) && this.getPiece(posX - 1, posY + 1) != null && !gamePiecesTaken.contains(this.getPiece(posX - 1, posY + 1)) && color != this.getPiece(posX - 1, posY + 1).getColor() && this.getPiece(posX - 2, posY + 2) == null) {
                gamePiecesTaken.add(this.getPiece(posX - 1, posY + 1));
                addAvailableCaptures(posX - 2, posY + 2, allAvailableCaptures, color, gamePiecesTaken, type);
                gamePiecesTaken.remove(this.getPiece(posX - 1, posY + 1));
            }
            if (isWithinBoard(posX + 2, posY + 2) && this.getPiece(posX + 1, posY + 1) != null && !gamePiecesTaken.contains(this.getPiece(posX + 1, posY + 1)) && color != this.getPiece(posX + 1, posY + 1).getColor() && this.getPiece(posX + 2, posY + 2) == null) {
                gamePiecesTaken.add(this.getPiece(posX + 1, posY + 1));
                addAvailableCaptures(posX + 2, posY + 2, allAvailableCaptures, color, gamePiecesTaken, type);
                gamePiecesTaken.remove(this.getPiece(posX + 1, posY + 1));
            }
        } else {        // Calculating captures for KING
            int tempX = posX, tempY = posY;
            while (isWithinBoard(++tempX+1, ++tempY+1)){
                if(this.getPiece(tempX, tempY) != null && this.getPiece(tempX+1, tempY+1) == null && this.getPiece(tempX, tempY).getColor() != color && !gamePiecesTaken.contains(this.getPiece(tempX, tempY))) {
                    gamePiecesTaken.add(this.getPiece(tempX, tempY));
                    addAvailableCapturesKing(tempX, tempY, allAvailableCaptures, color, gamePiecesTaken, type, new Pair<>(1, 1), king);
                    gamePiecesTaken.remove(this.getPiece(tempX, tempY));
                    break;
                }
            }
            tempX = posX;
            tempY = posY;
            while (isWithinBoard(--tempX-1, ++tempY+1)){
                if(this.getPiece(tempX, tempY) != null && this.getPiece(tempX-1, tempY+1) == null && this.getPiece(tempX, tempY).getColor() != color && !gamePiecesTaken.contains(this.getPiece(tempX, tempY))) {
                    gamePiecesTaken.add(this.getPiece(tempX, tempY));
                    addAvailableCapturesKing(tempX, tempY, allAvailableCaptures, color, gamePiecesTaken, type, new Pair<>(-1, 1), king);
                    gamePiecesTaken.remove(this.getPiece(tempX, tempY));
                    break;
                }
            }
            tempX = posX;
            tempY = posY;
            while (isWithinBoard(++tempX+1, --tempY-1)){
                if(this.getPiece(tempX, tempY) != null && this.getPiece(tempX+1, tempY-1) == null && this.getPiece(tempX, tempY).getColor() != color && !gamePiecesTaken.contains(this.getPiece(tempX, tempY))) {
                    gamePiecesTaken.add(this.getPiece(tempX, tempY));
                    addAvailableCapturesKing(tempX, tempY, allAvailableCaptures, color, gamePiecesTaken, type, new Pair<>(1, -1), king);
                    gamePiecesTaken.remove(this.getPiece(tempX, tempY));
                    break;
                }
            }
            tempX = posX;
            tempY = posY;
            while (isWithinBoard(--tempX-1, --tempY-1)){
                if(this.getPiece(tempX, tempY) != null && this.getPiece(tempX-1, tempY-1) == null && this.getPiece(tempX, tempY).getColor() != color && !gamePiecesTaken.contains(this.getPiece(tempX, tempY))) {
                    gamePiecesTaken.add(this.getPiece(tempX, tempY));
                    addAvailableCapturesKing(tempX, tempY, allAvailableCaptures, color, gamePiecesTaken, type, new Pair<>(-1, -1), king);
                    gamePiecesTaken.remove(this.getPiece(tempX, tempY));
                    break;
                }
            }
        }

        ArrayList<Integer> depths = new ArrayList<>();
        for(int i = 0; i < allAvailableCaptures.getChildren().size(); i ++){
            depths.add(allAvailableCaptures.calculateDepthFromNode(allAvailableCaptures.getChildren().get(i)));
        }
        if(depths.size() > 0) {
            int maxDepth = Collections.max(depths);
            for (int i = allAvailableCaptures.getChildren().size() - 1; i > -1; i--) {
                if (allAvailableCaptures.calculateDepthFromNode(allAvailableCaptures.getChildren().get(i)) < maxDepth) {
                    allAvailableCaptures.getChildren().remove(i);
                }
            }
        }
        return allAvailableCaptures;
    }

    private void addAvailableCaptures(int posX, int posY, Tree<Pair<Integer, Integer>> allAvailableCaptures, int color, ArrayList<GamePiece> gamePieces, int type) {
        Tree<Pair<Integer, Integer>> child = new Tree<>(new Pair<>(posX, posY));
        allAvailableCaptures.addChild(child.getNode());

        ArrayList<Tree.Node<Pair<Integer, Integer>>> childrenList = new ArrayList<>(calculateCapturesFromPosition(posX, posY, color, gamePieces, type, null).getChildren());

        for (Tree.Node<Pair<Integer, Integer>> pairNode : childrenList) {
            child.addChild(pairNode);
        }
        this.availableCapturesCount++;
    }

    private void addAvailableCapturesKing(int posX, int posY, Tree<Pair<Integer, Integer>> allAvailableCaptures, int color, ArrayList<GamePiece> gamePieces, int type, Pair<Integer, Integer> vector, GamePiece king){
        Tree<Pair<Integer, Integer>> child;
        ArrayList<Tree.Node<Pair<Integer, Integer>>> childrenList;
        posX += vector.getKey();
        posY += vector.getValue();
        while(this.isWithinBoard(posX, posY) && this.getPiece(posX, posY) == null){
            if (posX == king.getPosX()) {
                posX += vector.getKey();
                posY += vector.getValue();
                continue;
            }
            child = new Tree<>(new Pair<>(posX, posY));
            allAvailableCaptures.addChild(child.getNode());

            childrenList = new ArrayList<>(calculateCapturesFromPosition(posX, posY, color, gamePieces, type, king).getChildren());
            for (Tree.Node<Pair<Integer, Integer>> pairNode : childrenList) {
                child.addChild(pairNode);
            }
            this.availableCapturesCount++;

            posX += vector.getKey();
            posY += vector.getValue();
        }
    }

    private void calculateAvailableMoves(){
        this.availableMovesCount = 0;
        for (GamePiece p : this.gamePieces) {
            if (p.getType() == GamePiece.MAN) {
                if (p.getColor() == GamePiece.WHITE && this.getCurrentColorToMove() == GamePiece.WHITE) {
                    if (isWithinBoard(p.getPosX() - 1, p.getPosY() - 1) && this.getPiece(p.getPosX() - 1, p.getPosY() - 1) == null) {
                        p.addAvailableMove(new Pair<>(p.getPosX() - 1, p.getPosY() - 1));
                        this.availableMovesCount++;
                    }
                    if (isWithinBoard(p.getPosX() + 1, p.getPosY() - 1) && this.getPiece(p.getPosX() + 1, p.getPosY() - 1) == null) {
                        p.addAvailableMove(new Pair<>(p.getPosX() + 1, p.getPosY() - 1));
                        this.availableMovesCount++;
                    }
                } else if (p.getColor() == GamePiece.BLACK && this.getCurrentColorToMove() == GamePiece.BLACK) {
                    if (isWithinBoard(p.getPosX() - 1, p.getPosY() + 1) && this.getPiece(p.getPosX() - 1, p.getPosY() + 1) == null) {
                        p.addAvailableMove(new Pair<>(p.getPosX() - 1, p.getPosY() + 1));
                        this.availableMovesCount++;
                    }
                    if (isWithinBoard(p.getPosX() + 1, p.getPosY() + 1) && this.getPiece(p.getPosX() + 1, p.getPosY() + 1) == null) {
                        p.addAvailableMove(new Pair<>(p.getPosX() + 1, p.getPosY() + 1));
                        this.availableMovesCount++;
                    }
                }
            } else {  // Calculation for King
                if (p.getColor() == this.getCurrentColorToMove()) {
                    int tempX = p.getPosX(), tempY = p.getPosY();
                    while (isWithinBoard(++tempX, ++tempY) && this.getPiece(tempX, tempY) == null) {
                        p.addAvailableMove(new Pair<>(tempX, tempY));
                        this.availableMovesCount++;
                    }
                    tempX = p.getPosX();
                    tempY = p.getPosY();
                    while (isWithinBoard(--tempX, ++tempY) && this.getPiece(tempX, tempY) == null) {
                        p.addAvailableMove(new Pair<>(tempX, tempY));
                        this.availableMovesCount++;
                    }
                    tempX = p.getPosX();
                    tempY = p.getPosY();
                    while (isWithinBoard(++tempX, --tempY) && this.getPiece(tempX, tempY) == null) {
                        p.addAvailableMove(new Pair<>(tempX, tempY));
                        this.availableMovesCount++;
                    }
                    tempX = p.getPosX();
                    tempY = p.getPosY();
                    while (isWithinBoard(--tempX, --tempY) && this.getPiece(tempX, tempY) == null) {
                        p.addAvailableMove(new Pair<>(tempX, tempY));
                        this.availableMovesCount++;
                    }
                }
            }
        }
    }

    public GamePiece getPieceBetween(int posX1, int posY1, int posX2, int posY2){
        Pair<Integer, Integer> vector = new Pair<>((posX2-posX1)/Math.abs(posX2-posX1), (posY2-posY1)/Math.abs(posY2-posY1));
        int tempX = posX1 + vector.getKey();
        int tempY = posY1 + vector.getValue();

        while (tempX != posX2 && tempY != posY2){
            if(getPiece(tempX, tempY) != null)
                return getPiece(tempX, tempY);
            tempX = tempX + vector.getKey();
            tempY = tempY + vector.getValue();
        }
        return null;
    }

    public void capture(Pair<Integer, Integer> pos1, Pair<Integer, Integer> pos2, GamePiece piece){
        int posX = pos1.getKey();
        int posY = pos1.getValue();
        int pos2X = pos2.getKey();
        int pos2Y = pos2.getValue();
        if (Math.abs(posX- pos2X) == 1 && piece.getType() == GamePiece.MAN)
            this.afterMoveCalculations(piece);
        else if(piece.getType() == GamePiece.MAN) {    // else: capture
            this.capturePiece(this.getPiece((posX + pos2X)/2, (posY + pos2Y)/2));
            this.afterMoveCalculations(piece);
        } else {
            GamePiece gamePieceCaptured = this.getPieceBetween(pos2X, pos2Y, posX, posY);
            if (gamePieceCaptured!=null) {    //King capture
                this.capturePiece(gamePieceCaptured);
                this.afterMoveCalculations(piece);
            } else {
                this.afterMoveCalculations(piece);
            }
        }
    }

    public void capturePiece(GamePiece piece){
        this.gamePieces.remove(piece);
    }

    public ArrayList<GamePiece> getGamePieces() {
        return gamePieces;
    }

    private boolean isWithinBoard(int x, int y){
        if(x >= 0 && x < 8 && y >= 0 && y<8)
            return true;
        return false;
    }

    public int getCurrentMove() {
        return currentMove;
    }

    public void subCurrentMove() { this.currentMove--; }

    public int getCurrentColorToMove(){
        return currentMove%2;
    }

    public double getEvaluation() {
        return evaluation;
    }

    public int getAvailableCapturesCount() {
        return availableCapturesCount;
    }

    public int checkGameState(){
        int white_men_count = 0;
        int white_king_count = 0;
        int black_men_count = 0;
        int black_king_count = 0;
        int available_moves = 0;
        for (GamePiece piece: this.gamePieces){
            if(piece.getColor() == GamePiece.WHITE) {
                if(piece.getType() == GamePiece.MAN)
                    white_men_count++;
                else
                    white_king_count++;
            }
            else{
                if(piece.getType() == GamePiece.MAN)
                    black_men_count ++;
                else
                    black_king_count ++;
            }
            if(piece.getColor() == this.getCurrentColorToMove()){
                if(piece.getAvailableMoves().size() > 0)
                    available_moves++;
            }
        }

        if(white_men_count == 0 && white_king_count == 0)
            return BLACK_WIN;
        if(black_men_count == 0 && black_king_count == 0)
            return WHITE_WIN;
        if(available_moves == 0 && this.getCurrentColorToMove() == GamePiece.WHITE)
            return BLACK_WIN;
        else if(available_moves == 0)
            return WHITE_WIN;
        if(this.kingMoves == 30)
            return STALEMATE;
        return GAME_IN_PROGRESS;
    }

    public double evaluate(){
        double score = 0.0;

        if(checkGameState() == WHITE_WIN)
            return Integer.MAX_VALUE;
        if(checkGameState() == BLACK_WIN)
            return Integer.MIN_VALUE;

        for (GamePiece piece: this.gamePieces){
            if(piece.getColor() == GamePiece.WHITE) {
                if(piece.getType() == GamePiece.MAN) {
                    score++;
                    score += (7-piece.getPosY())/10.0;
                }
                else {
                    score += 3;
                }


                if (piece.getPosX() > 1 && piece.getPosX() < 6){
                    score += (7-piece.getPosY())/5.0;
                } else if (piece.getPosX() > 0 && piece.getPosX() < 7){
                    score += (7-piece.getPosY())/10.0;
                }
            }
            else {
                if (piece.getType() == GamePiece.MAN) {
                    score--;
                    score -= piece.getPosY()/10.0;
                }
                else {
                    score -= 3;
                }


                if (piece.getPosX() > 1 && piece.getPosX() < 6){
                    score -= piece.getPosY()/5.0;
                } else if (piece.getPosX() > 0 && piece.getPosX() < 7){
                    score -= piece.getPosY()/10.0;
                }
            }
            score = Math.round(score*10)/10.0;
        }

        this.evaluation = score;
        return score;
    }
}
