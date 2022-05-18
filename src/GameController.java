public class GameController {
    private GameBoard gameBoard;
    private AIPlayer whiteAI;
    private AIPlayer blackAI;
    private GameGui gameGui;

    public GameController(GameBoard gameBoard, AIPlayer whiteAI, AIPlayer blackAI, GameGui gameGui) {
        this.gameBoard = gameBoard;
        this.whiteAI = whiteAI;
        this.blackAI = blackAI;
        this.gameGui = gameGui;
    }

    public void makeMove(){
        if (gameBoard.getCurrentColorToMove() == GamePiece.WHITE && whiteAI != null){
            whiteAI.makeMove(gameBoard);
            gameGui.repaint();
            this.makeMove();
        } else if (gameBoard.getCurrentColorToMove() == GamePiece.BLACK && blackAI != null){
            blackAI.makeMove(gameBoard);
            gameGui.repaint();
            this.makeMove();
        }
    }
}
