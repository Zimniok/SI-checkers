
public class AIPlayer {
    public static final int MINIMAX = 0;
    public static final int ALFABETA = 1;

    private int type;
    private int depth;
    private int color;
    private int evaluationType;

    public AIPlayer(int type, int depth, int color, int evaluationType) {
        this.type = type;
        this.depth = depth;
        this.color = color;
        this.evaluationType = evaluationType;
    }

    public void makeMove(GameBoard gameBoard){
        if(gameBoard.getCurrentMove() == 0)
            gameBoard.change(Engine.randomMove(gameBoard));
        if(this.type == MINIMAX){
            GameBoard nextState = Engine.startMinMax(gameBoard, depth, evaluationType);
            gameBoard.change(nextState);
        }
        else if(this.type == ALFABETA){
            GameBoard nextState = Engine.startAlphaBeta(gameBoard, depth, evaluationType);
            gameBoard.change(nextState);
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getEvaluationType() {
        return evaluationType;
    }
}
