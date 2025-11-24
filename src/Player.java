import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String name;
    private final String symbol; // Símbolo Unicode
    private final Point startingPosition;
    private Point currentPosition;
    private int wallsRemaining;
    private final int targetRow; // objetivo en Y (para 2 jugadores)


    /*Lista para guardar historial de movimientos*/
    private final List<Point> movementHistory;


    public Player(String name, String symbol, int initialWalls, Point startPos, int targetRow) {
        //Luis estuvo Aqui >:(
        // Validaciones básicas de parámetros
        if (startPos == null){
            throw new IllegalArgumentException("ERROR: La posicion inicial no puede ser null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: El nombre del jugador no puede estar vacío");
        }
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: El símbolo del jugador no puede estar vacío");
        }
        this.name = name;
        this.symbol = symbol;
        this.wallsRemaining = initialWalls;
        this.startingPosition = new Point(startPos.x, startPos.y);
        this.currentPosition = new Point(startPos.x, startPos.y);
        this.targetRow = targetRow;


        /*Inicializar el historial con pocision inicial*/
        this.movementHistory = new ArrayList <> ();
        this.movementHistory.add(new Point(startPos.x, startPos.y));
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

    /* Getter para posicion inicial*/

    public Point getStartingPosition() {
        return new Point(startingPosition.x, startingPosition.y);
    }

    public int getWallsRemaining() {
        return wallsRemaining;
    }

    public int getTargetRow() {
        return targetRow;
    }

    public void setCurrentPosition(Point newPosition) {
        this.currentPosition = new Point(newPosition.x, newPosition.y);

        /*Registrar Automaticamente el movimiento*/

        this.movementHistory.add (new Point(newPosition.x, newPosition.y));
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

    /*Obtener el historial completo de movimientos*/
    public List<Point> getMovementHistory(){
        return new ArrayList<>(movementHistory); //Devuelva una copia y asi no se toca la lista original
    }


}
