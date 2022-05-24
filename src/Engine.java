import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Engine {
    public static GameBoard startAlphaBeta(GameBoard gameBoard, int depth){
        double alpha = Double.MIN_VALUE;
        double beta = Double.MAX_VALUE;
        ArrayList<Pair<GameBoard, Double>> firstKids = new ArrayList<>();
        for (GamePiece gamePiece: gameBoard.getGamePieces()){
            if(gamePiece.getColor() == gameBoard.getCurrentColorToMove()){
                for (Pair<Integer, Integer> move: gamePiece.getAvailableMoves()){
                    GameBoard nextState = new GameBoard(gameBoard);
                    GamePiece nextStatePiece = nextState.getPiece(gamePiece.getPosX(), gamePiece.getPosY());
                    Pair<Integer, Integer> beforeMovePos = new Pair<>(nextStatePiece.getPosX(), nextStatePiece.getPosY());
                    nextStatePiece.move(move.getKey(), move.getValue());
                    if(gameBoard.getAvailableCapturesCount() > 0) {
                        nextState.capture(beforeMovePos, move, nextStatePiece);
                    }else {
                        nextState.afterMoveCalculations(nextStatePiece);
                    }
                    if (gameBoard.getCurrentColorToMove() == GamePiece.WHITE && firstKids.size() > 0 && firstKids.get(firstKids.size()-1).getValue() > alpha)
                        alpha = firstKids.get(firstKids.size()-1).getValue();
                    else if(gameBoard.getCurrentColorToMove() == GamePiece.BLACK && firstKids.size() > 0 && firstKids.get(firstKids.size()-1).getValue() < beta)
                        beta = firstKids.get(firstKids.size()-1).getValue();
                    double bestChildEval = alphaBeta(nextState, depth, gameBoard.getCurrentColorToMove(), alpha, beta);

                    firstKids.add(new Pair<>(nextState, bestChildEval));

                    if(gameBoard.getCurrentColorToMove() == GamePiece.WHITE && bestChildEval > beta){
                        break;
                    } else if(gameBoard.getCurrentColorToMove() == GamePiece.BLACK && bestChildEval < alpha){
                        break;
                    }
                }
            }
        }

        if (firstKids.size() == 0)
            return null;

        int bestValuePos = 0;
        for (int i = 1; i < firstKids.size(); i++){
            if(gameBoard.getCurrentColorToMove() == GamePiece.WHITE && firstKids.get(i).getValue() > firstKids.get(bestValuePos).getValue())
                bestValuePos = i;
            else if(firstKids.get(i).getValue() < firstKids.get(bestValuePos).getValue())
                bestValuePos = i;
        }

        return firstKids.get(bestValuePos).getKey();
    }

    private static double alphaBeta(GameBoard gameBoard, int depth, int lastMoveColor, double alpha, double beta){
        ArrayList<Double> evaluations = new ArrayList<>();
        if(gameBoard.getCurrentColorToMove() != lastMoveColor){
            depth --;
        }
        for(GamePiece gamePiece: gameBoard.getGamePieces()){
            if (gamePiece.getColor() == gameBoard.getCurrentColorToMove()){
                for (Pair<Integer, Integer> move: gamePiece.getAvailableMoves()) {
                    GameBoard nextState = new GameBoard(gameBoard);
                    GamePiece nextStatePiece = nextState.getPiece(gamePiece.getPosX(), gamePiece.getPosY());
                    Pair<Integer, Integer> beforeMovePos = new Pair<>(nextStatePiece.getPosX(), nextStatePiece.getPosY());
                    nextStatePiece.move(move.getKey(), move.getValue());
                    if(gameBoard.getAvailableCapturesCount() > 0){
                        nextState.capture(beforeMovePos, move, nextStatePiece);
                    } else {
                        nextState.afterMoveCalculations(nextStatePiece);
                    }
                    int gameState = nextState.checkGameState();
                    if(gameState == GameBoard.WHITE_WIN || gameState == GameBoard.BLACK_WIN || gameState == GameBoard.STALEMATE || depth <= 0){
                        evaluations.add(nextState.evaluate());
                        continue;
                    }
                    int nextLastMoveColor = lastMoveColor;
                    if(gameBoard.getCurrentColorToMove() != lastMoveColor){
                        nextLastMoveColor = gameBoard.getCurrentColorToMove();
                    }
                    if (lastMoveColor == GamePiece.WHITE && evaluations.size() > 0 && evaluations.get(evaluations.size()-1) > alpha)
                        alpha = evaluations.get(evaluations.size()-1);
                    else if(lastMoveColor == GamePiece.BLACK && evaluations.size() > 0 && evaluations.get(evaluations.size()-1) < beta)
                        beta = evaluations.get(evaluations.size()-1);
                    double bestChildEval = alphaBeta(nextState, depth, nextLastMoveColor, alpha, beta);
                    evaluations.add(bestChildEval);
                    if(lastMoveColor == GamePiece.WHITE && bestChildEval > beta){
                        break;
                    } else if(lastMoveColor == GamePiece.BLACK && bestChildEval < alpha){
                        break;
                    }
                }
            }
        }
        if(evaluations.size() == 0)
            return gameBoard.evaluate();
        if (lastMoveColor == GamePiece.WHITE){
            return Collections.max(evaluations);
        }
        return Collections.min(evaluations);
    }

    public static GameBoard startMinMax(GameBoard gameBoard, int depth){
        ArrayList<Pair<GameBoard, Double>> firstKids = new ArrayList<>();
        for (GamePiece gamePiece: gameBoard.getGamePieces()){
            if(gamePiece.getColor() == gameBoard.getCurrentColorToMove()){
                for (Pair<Integer, Integer> move: gamePiece.getAvailableMoves()){
                    GameBoard nextState = new GameBoard(gameBoard);
                    GamePiece nextStatePiece = nextState.getPiece(gamePiece.getPosX(), gamePiece.getPosY());
                    Pair<Integer, Integer> beforeMovePos = new Pair<>(nextStatePiece.getPosX(), nextStatePiece.getPosY());
                    nextStatePiece.move(move.getKey(), move.getValue());
                    if(gameBoard.getAvailableCapturesCount() > 0) {
                        nextState.capture(beforeMovePos, move, nextStatePiece);
                    }else {
                        nextState.afterMoveCalculations(nextStatePiece);
                    }
                    firstKids.add(new Pair<>(nextState, minmax(nextState, depth, gameBoard.getCurrentColorToMove())));
                }
            }
        }

        if (firstKids.size() == 0)
            return null;

        int bestValuePos = 0;
        for (int i = 1; i < firstKids.size(); i++){
            if(gameBoard.getCurrentColorToMove() == GamePiece.WHITE && firstKids.get(i).getValue() > firstKids.get(bestValuePos).getValue())
                bestValuePos = i;
            else if(firstKids.get(i).getValue() < firstKids.get(bestValuePos).getValue())
                bestValuePos = i;
        }

        return firstKids.get(bestValuePos).getKey();


//        ArrayList<Tree.Node<Pair<Double, Integer>>> children = (ArrayList<Tree.Node<Pair<Double, Integer>>>) gameTree.getChildren();
//        for (Tree.Node<Pair<Double, Integer>> child: children){
//            if(child.data.getValue().equals(gameTree.getNode().data.getValue()))
//                return child.data.getKey();
//        }
//
//        return children.get(0).data.getKey();
    }

    public static double minmax(GameBoard gameBoard, int depth, int lastMoveColor){
        ArrayList<Double> evaluations = new ArrayList<>();
        if(gameBoard.getCurrentColorToMove() != lastMoveColor){
            depth --;
        }
        for(GamePiece gamePiece: gameBoard.getGamePieces()){
            if (gamePiece.getColor() == gameBoard.getCurrentColorToMove()){
                for (Pair<Integer, Integer> move: gamePiece.getAvailableMoves()) {
                    GameBoard nextState = new GameBoard(gameBoard);
                    GamePiece nextStatePiece = nextState.getPiece(gamePiece.getPosX(), gamePiece.getPosY());
                    Pair<Integer, Integer> beforeMovePos = new Pair<>(nextStatePiece.getPosX(), nextStatePiece.getPosY());
                    nextStatePiece.move(move.getKey(), move.getValue());
                    if(gameBoard.getAvailableCapturesCount() > 0){
                        nextState.capture(beforeMovePos, move, nextStatePiece);
                    } else {
                        nextState.afterMoveCalculations(nextStatePiece);
                    }
                    int gameState = nextState.checkGameState();
                    if(gameState == GameBoard.WHITE_WIN || gameState == GameBoard.BLACK_WIN || gameState == GameBoard.STALEMATE || depth <= 0){
                        evaluations.add(nextState.evaluate());
                        continue;
                    }
                    int nextLastMoveColor = lastMoveColor;
                    if(gameBoard.getCurrentColorToMove() != lastMoveColor){
                        nextLastMoveColor = gameBoard.getCurrentColorToMove();
                    }
                    evaluations.add(minmax(nextState, depth, nextLastMoveColor));
                }
            }
        }
        if(evaluations.size() == 0)
            return gameBoard.evaluate();
        if (lastMoveColor == GamePiece.WHITE){
            return Collections.max(evaluations);
        }
        return Collections.min(evaluations);
    }

    public static void minMax(Tree.Node<Pair<Double, Integer>> node) {
        ArrayList<Tree.Node<Pair<Double, Integer>>> children = (ArrayList<Tree.Node<Pair<Double, Integer>>>) node.getChildren();

        if (children.size() != 0) {
            for (Tree.Node<Pair<Double, Integer>> child : children) {
                minMax(child);
            }
        }
        if(node.getParent() == null)
            return;
        setBestValueForParent(node);
    }

    public static GameBoard randomMove(GameBoard gameBoard){
        ArrayList<GameBoard> firstKids = new ArrayList<>();
        for (GamePiece gamePiece: gameBoard.getGamePieces()){
            if(gamePiece.getColor() == gameBoard.getCurrentColorToMove()){
                for (Pair<Integer, Integer> move: gamePiece.getAvailableMoves()){
                    GameBoard nextState = new GameBoard(gameBoard);
                    GamePiece nextStatePiece = nextState.getPiece(gamePiece.getPosX(), gamePiece.getPosY());
                    Pair<Integer, Integer> beforeMovePos = new Pair<>(nextStatePiece.getPosX(), nextStatePiece.getPosY());
                    nextStatePiece.move(move.getKey(), move.getValue());
                    if(gameBoard.getAvailableCapturesCount() > 0) {
                        nextState.capture(beforeMovePos, move, nextStatePiece);
                    }else {
                        nextState.afterMoveCalculations(nextStatePiece);
                    }
                    firstKids.add(nextState);
                }
            }
        }
        Random r = new Random();
        return firstKids.get(r.nextInt(firstKids.size()));
    }

    private static void setBestValueForParent(Tree.Node<Pair<Double, Integer>> node) {
        if (node.getParent().data.getValue() == GamePiece.WHITE){
            if(node.getParent().data.getKey() == null || node.data.getKey() > node.getParent().data.getKey()){
                node.getParent().data = new Pair<>(node.data.getKey(), node.getParent().data.getValue());
            }
        } else {
            if(node.getParent().data.getKey() == null || node.data.getKey() < node.getParent().data.getKey()){
                node.getParent().data = new Pair<>(node.data.getKey(), node.getParent().data.getValue());
            }
        }
//        if(node.getParent().data.getKey().getCurrentColorToMove() == GamePiece.WHITE) {
//            if (node.getParent().data.getValue() == null || node.data.getKey().getEvaluation() > node.getParent().data.getValue()){
//                node.getParent().data = new Pair<>(node.getParent().data.getKey(), node.data.getKey().getEvaluation());
//            }
//        } else {
//            if (node.getParent().data.getValue() == null || node.data.getKey().getEvaluation() < node.getParent().data.getValue()){
//                node.getParent().data = new Pair<>(node.getParent().data.getKey(), node.data.getKey().getEvaluation());
//            }
//        }
    }

    private static void populateTree(Tree.Node<Pair<Double, Integer>> node, int depth, int lastMoveColor, GameBoard gameBoard){
        if(gameBoard.getCurrentColorToMove() != lastMoveColor){
            lastMoveColor = gameBoard.getCurrentColorToMove();
            depth --;
        }
        for(GamePiece gamePiece: gameBoard.getGamePieces()){
            if (gamePiece.getColor() == gameBoard.getCurrentColorToMove()){
                for (Pair<Integer, Integer> move: gamePiece.getAvailableMoves()) {
                    GameBoard nextState = new GameBoard(gameBoard);
                    GamePiece nextStatePiece = nextState.getPiece(gamePiece.getPosX(), gamePiece.getPosY());
                    Pair<Integer, Integer> beforeMovePos = new Pair<>(nextStatePiece.getPosX(), nextStatePiece.getPosY());
                    nextStatePiece.move(move.getKey(), move.getValue());
                    if(gameBoard.getAvailableCapturesCount() > 0){
                        nextState.capture(beforeMovePos, move, nextStatePiece);
                    } else {
                        nextState.afterMoveCalculations(nextStatePiece);
                    }
                    Tree.Node<Pair<Double, Integer>> child = new Tree.Node<>();
                    child.data = new Pair<>(null, nextState.getCurrentColorToMove());
                    node.addChild(child);
                    int gameState = nextState.checkGameState();
                    if(gameState == GameBoard.WHITE_WIN || gameState == GameBoard.BLACK_WIN || gameState == GameBoard.STALEMATE || depth <= 0){
                        child.data = new Pair<>(nextState.evaluate(), nextState.getCurrentColorToMove());
                        continue;
                    }
                    populateTree(child, depth, lastMoveColor, nextState);
                }
            }
        }

//        for (GamePiece gamePiece: node.data.getKey().getGamePieces()){
//            if (gamePiece.getColor() != lastMoveColor){
//                lastMoveColor = gamePiece.getColor();
//                depth --;
//            }
//            if (gamePiece.getColor() == node.data.getKey().getCurrentColorToMove()) {
//                for (Pair<Integer, Integer> move : gamePiece.getAvailableMoves()) {
//                    GameBoard nextState = new GameBoard(node.data.getKey());
//                    GamePiece nextStatePiece = nextState.getPiece(gamePiece.getPosX(), gamePiece.getPosY());
//                    Pair<Integer, Integer> beforeMovePos = new Pair<>(nextStatePiece.getPosX(), nextStatePiece.getPosY());
//                    nextStatePiece.move(move.getKey(), move.getValue());
//                    if(node.data.getKey().getAvailableCapturesCount() > 0) {
//                        nextState.capture(beforeMovePos, move, nextStatePiece);
//                    }else {
//                        nextState.afterMoveCalculations(nextStatePiece);
//                    }
////                    if(node.data.getKey().getAvailableCapturesCount() > 0) {
////                        nextState.addCurrentMove();
////                        nextState.afterMoveCalculations(null);
////                    }
//                    Tree.Node<Pair<GameBoard, Double>> child = new Tree.Node<>();
//                    child.data = new Pair<GameBoard,Double>(nextState, null);
//                    node.addChild(child);
//                    int gameState = nextState.checkGameState();
//                    if(gameState == GameBoard.WHITE_WIN || gameState == GameBoard.BLACK_WIN || gameState == GameBoard.STALEMATE || depth <= 0) {
//                        child.data = new Pair<>(nextState, nextState.evaluate());
//                        continue;
//                    }
//                    populateTree(child, depth, lastMoveColor);
//                }
//            }
//        }
    }
}
