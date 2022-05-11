import javafx.util.Pair;

import java.util.ArrayList;

public class Engine {
    public static GameBoard startMinMax(GameBoard gameBoard, int depth){
        Tree<Pair<GameBoard, Double>> gameTree = new Tree<Pair<GameBoard, Double>>(new Pair<>(gameBoard, 0.0));
        populateTree(gameTree.getNode(), depth, (gameBoard.getCurrentColorToMove() + 1) % 2 );
        minMax(gameTree.getNode());

        ArrayList<Tree.Node<Pair<GameBoard, Double>>> children = (ArrayList<Tree.Node<Pair<GameBoard, Double>>>) gameTree.getChildren();
        for (Tree.Node<Pair<GameBoard, Double>> child: children){
            if(child.data.getValue().equals(gameTree.getNode().data.getValue()))
                return child.data.getKey();
        }

        return children.get(0).data.getKey();
    }

    public static void minMax(Tree.Node<Pair<GameBoard, Double>> node) {
        ArrayList<Tree.Node<Pair<GameBoard, Double>>> children = (ArrayList<Tree.Node<Pair<GameBoard, Double>>>) node.getChildren();

        if (children.size() != 0) {
            for (Tree.Node<Pair<GameBoard, Double>> child : children) {
                minMax(child);
            }
        }
        if(node.getParent() == null)
            return;
        setBestValueForParent(node);
    }

    private static void setBestValueForParent(Tree.Node<Pair<GameBoard, Double>> node) {
        if(node.getParent().data.getKey().getCurrentColorToMove() == GamePiece.WHITE) {
            if (node.getParent().data.getValue() == null || node.data.getKey().getEvaluation() > node.getParent().data.getValue()){
                node.getParent().data = new Pair<>(node.getParent().data.getKey(), node.data.getKey().getEvaluation());
            }
        } else {
            if (node.getParent().data.getValue() == null || node.data.getKey().getEvaluation() < node.getParent().data.getValue()){
                node.getParent().data = new Pair<>(node.getParent().data.getKey(), node.data.getKey().getEvaluation());
            }
        }
    }

    private static void populateTree(Tree.Node<Pair<GameBoard, Double>> node, int depth, int lastMoveColor){
        for (GamePiece gamePiece: node.data.getKey().getGamePieces()){
            if (gamePiece.getColor() != lastMoveColor){
                lastMoveColor = gamePiece.getColor();
                depth --;
            }
            if (gamePiece.getColor() == node.data.getKey().getCurrentColorToMove()) {
                for (Pair<Integer, Integer> move : gamePiece.getAvailableMoves()) {
                    GameBoard nextState = new GameBoard(node.data.getKey());
                    GamePiece nextStatePiece = nextState.getPiece(gamePiece.getPosX(), gamePiece.getPosY());
                    Pair<Integer, Integer> beforeMovePos = new Pair<>(nextStatePiece.getPosX(), nextStatePiece.getPosY());
                    nextStatePiece.move(move.getKey(), move.getValue());
                    if(node.data.getKey().getAvailableCapturesCount() > 0) {
                        nextState.capture(beforeMovePos, move, nextStatePiece);
                    }else {
                        nextState.afterMoveCalculations(nextStatePiece);
                    }
//                    if(node.data.getKey().getAvailableCapturesCount() > 0) {
//                        nextState.addCurrentMove();
//                        nextState.afterMoveCalculations(null);
//                    }
                    Tree.Node<Pair<GameBoard, Double>> child = new Tree.Node<>();
                    child.data = new Pair<GameBoard,Double>(nextState, null);
                    node.addChild(child);
                    int gameState = nextState.checkGameState();
                    if(gameState == GameBoard.WHITE_WIN || gameState == GameBoard.BLACK_WIN || gameState == GameBoard.STALEMATE || depth <= 0) {
                        child.data = new Pair<>(nextState, nextState.evaluate());
                        continue;
                    }
                    populateTree(child, depth, lastMoveColor);
                }
            }
        }
    }
}
