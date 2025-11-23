import java.awt.Point;
import java.util.List;

public class GameBoard {
    private final int gridSize; // 17
    private char[][] grid;


    // Símbolos y constantes
    public static final char NODE_EMPTY = ' ';
    public static final char H_WALL = '─';
    public static final char CORNER = '+';
    public static final char V_PATH = ' ';
    public static final char WALL_SOLID = '█';

    public GameBoard(int gridSize) {
        this.gridSize = gridSize;
        this.grid = new char[gridSize][gridSize];
        initializeGrid();
    }

    private void initializeGrid() {
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                if (y % 2 == 0 && x % 2 == 0) {
                    grid[y][x] = NODE_EMPTY; // nodo (casilla)
                } else if (y % 2 != 0 && x % 2 != 0) {
                    grid[y][x] = CORNER; // esquina
                } else if (y % 2 == 0) {
                    grid[y][x] = H_WALL; // slot horizontal (entre nodos en x impar)
                } else {
                    grid[y][x] = V_PATH; // slot vertical (entre nodos en y impar)
                }
            }
        }
    }

    public char[][] getGrid() {
        return grid;
    }

    /**
     * Coloca los jugadores en los nodos (índices pares). Limpia previamente los nodos.
     */
    public void updatePlayers(List<Player> players) {
        // limpiar nodos
        for (int y = 0; y < gridSize; y += 2) {
            for (int x = 0; x < gridSize; x += 2) {
                grid[y][x] = NODE_EMPTY;
            }
        }
        for (Player p : players) {
            Point pt = p.getCurrentPosition();
            if (pt.x >= 0 && pt.x < gridSize && pt.y >= 0 && pt.y < gridSize) {
                // Solo colocar en nodos (par,par)
                if (pt.x % 2 == 0 && pt.y % 2 == 0) {
                    grid[pt.y][pt.x] = p.getSymbol().charAt(0);
                }
            }
        }
    }

    /**
     * Colocar muro centrado en 'center'. isHorizontal determina orientación.
     * Validaciones:
     *  - segmentos deben caer en slots correctos (horizontal slots: row even, col odd;
     *                                         vertical slots: row odd, col even)
     *  - segmentos deben estar dentro de límites y no colisionar con WALL_SOLID
     */
    public boolean placeWall(Point center, boolean isHorizontal) {
        int cx = center.x;
        int cy = center.y;

        if (isHorizontal) {
            // segmentos: (cx-2,cy), (cx,cy), (cx+2,cy)
            Point[] segs = { new Point(cx - 2, cy), new Point(cx, cy), new Point(cx + 2, cy) };
            for (Point s : segs) {
                if (s.x < 0 || s.x >= gridSize || s.y < 0 || s.y >= gridSize) return false;
                if (grid[s.y][s.x] == WALL_SOLID) return false;
                // horizontal slots are at even row (y % 2 == 0) and odd col (x % 2 == 1)
                if (!(s.y % 2 == 0 && s.x % 2 == 1)) return false;
            }
            for (Point s : segs) grid[s.y][s.x] = WALL_SOLID;
            return true;
        } else {
            // vertical: (cx,cy-2),(cx,cy),(cx,cy+2)
            Point[] segs = { new Point(cx, cy - 2), new Point(cx, cy), new Point(cx, cy + 2) };
            for (Point s : segs) {
                if (s.x < 0 || s.x >= gridSize || s.y < 0 || s.y >= gridSize) return false;
                if (grid[s.y][s.x] == WALL_SOLID) return false;
                // vertical slots: odd row (y % 2 == 1) and even col (x % 2 == 0)
                if (!(s.y % 2 == 1 && s.x % 2 == 0)) return false;
            }
            for (Point s : segs) grid[s.y][s.x] = WALL_SOLID;
            return true;
        }
    }

}
