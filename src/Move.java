import javafx.util.Pair;

public class Move {
    private GamePiece gamePiece;
    private Pair<Integer, Integer> endPos;

    public Move(GamePiece gamePiece, Pair<Integer, Integer> endPos) {
        this.gamePiece = gamePiece;
        this.endPos = endPos;
    }

    public GamePiece getGamePiece() {
        return gamePiece;
    }

    public void setGamePiece(GamePiece gamePiece) {
        this.gamePiece = gamePiece;
    }

    public Pair<Integer, Integer> getEndPos() {
        return endPos;
    }

    public void setEndPos(Pair<Integer, Integer> endPos) {
        this.endPos = endPos;
    }
}
