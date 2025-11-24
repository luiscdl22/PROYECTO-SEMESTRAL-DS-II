import java.awt.Point;

public class Player {
    private final String name;
    private final String symbol; // Símbolo Unicode
    private final Point startingPosition;
    private Point currentPosition;
    private int wallsRemaining;
    private final int targetRow; // objetivo en Y (para 2 jugadores)


    public Player(String name, String symbol, int initialWalls, Point startPos, int targetRow) {
        this.name = name;
        this.symbol = symbol;
        this.wallsRemaining = initialWalls;
        this.startingPosition = new Point(startPos.x, startPos.y);
        this.currentPosition = new Point(startPos.x, startPos.y);
        this.targetRow = targetRow;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public Point getCurrentPosition() {
        return currentPosition;
    }

    public int getWallsRemaining() {
        return wallsRemaining;
    }

    public int getTargetRow() {
        return targetRow;
    }

    public void setCurrentPosition(Point newPosition) {
        this.currentPosition = new Point(newPosition.x, newPosition.y);
    }

    public boolean useWall() {
        if (wallsRemaining > 0) {
            wallsRemaining--;
            return true;
        }
        return false;
    }

    public void refundWall() {
        wallsRemaining++;
    }


}
