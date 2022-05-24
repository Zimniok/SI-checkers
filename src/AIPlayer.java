public class AIPlayer {
    public static final int MINIMAX = 0;
    public static final int ALFABETA = 1;

    private int type;
    private int depth;
    private int color;

    public AIPlayer(int type, int depth, int color) {
        this.type = type;
        this.depth = depth;
        this.color = color;
    }

    public void makeMove(GameBoard gameBoard){
        long deltaTime = System.currentTimeMillis();
        if(this.type == MINIMAX){
            GameBoard nextState = Engine.startMinMax(gameBoard, depth);
            gameBoard.change(nextState);
        }
        else if(this.type == ALFABETA){
            GameBoard nextState = Engine.startAlphaBeta(gameBoard, depth);
            gameBoard.change(nextState);
        }
        deltaTime = System.currentTimeMillis() - deltaTime;
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
}
