import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GameController {
    private GameBoard gameBoard;
    private AIPlayer whiteAI;
    private AIPlayer blackAI;
    private GameGui gameGui;
    private long sumTime;
    private File outputFile;
    private int evalType;

    public GameController(GameBoard gameBoard, AIPlayer whiteAI, AIPlayer blackAI, GameGui gameGui, int evalType) {
        this.gameBoard = gameBoard;
        this.whiteAI = whiteAI;
        this.blackAI = blackAI;
        this.gameGui = gameGui;
        this.sumTime = 0;
        this.outputFile = new File("./output/checkers_output.csv");
        this.evalType = evalType;
    }

    public void makeMove(){
        if(gameBoard.checkGameState() != gameBoard.GAME_IN_PROGRESS) {
            int gameState = gameBoard.checkGameState();
            if (gameState == GameBoard.STALEMATE || gameState == GameBoard.WHITE_WIN || gameState == GameBoard.BLACK_WIN){
                long avgTime = sumTime/this.gameBoard.getCurrentMove();
                try {
                    FileWriter fw = new FileWriter(outputFile, true);
                    BufferedWriter bw = new BufferedWriter(fw);

                    //game status,white ai type,black ai type,white eval type, black eval type, depth white,depth black,game length,moves
                    bw.append(String.join(";", String.valueOf(gameState), String.valueOf(whiteAI.getType()),
                            String.valueOf(blackAI.getType()), String.valueOf(whiteAI.getEvaluationType()), String.valueOf(blackAI.getEvaluationType()), String.valueOf(whiteAI.getDepth()),
                            String.valueOf(blackAI.getDepth()), String.valueOf(avgTime), String.valueOf(gameBoard.getCurrentMove())));
                    bw.newLine();
                    bw.close();
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return;
        }
        if (gameBoard.getCurrentColorToMove() == GamePiece.WHITE && whiteAI != null){
            long deltaTime = System.currentTimeMillis();
            whiteAI.makeMove(gameBoard);
            sumTime += System.currentTimeMillis() - deltaTime;
            gameGui.repaint();
            //this.makeMove();
        } else if (gameBoard.getCurrentColorToMove() == GamePiece.BLACK && blackAI != null){
            long deltaTime = System.currentTimeMillis();
            blackAI.makeMove(gameBoard);
            sumTime += System.currentTimeMillis() - deltaTime;
            gameGui.repaint();
            System.out.println(gameBoard.checkGameState());
            //TODO: uncomment next line for no graphic output
            //this.makeMove();
        }
    }
}
