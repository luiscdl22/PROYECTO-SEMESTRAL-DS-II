import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class QuoridorGame {
    private final GameBoard board;
    private final List<Player> players;
    private int currentPlayerIndex;
    private int turnCount;
    private boolean placementMode = false;
    // interno 17x17
    private final int boardSize = 17;
    private Player winner = null;

    public QuoridorGame(int playerCount, Scanner inputScanner, int nodeSize) {
        this.board = new GameBoard(this.boardSize);
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.turnCount = 1;
        initializePlayers(playerCount, inputScanner);
        board.updatePlayers(players);
    }

    private void initializePlayers(int count, Scanner inputScanner) {
        int nodeSize = 9;
        int center = nodeSize / 2; // 4
        int centerIndex = center * 2; // 8

        // startPositions: (x=col, y=row)
        Point[] startPositions = {
                new Point(centerIndex, boardSize - 1), // abajo centro (col=8,row=16)
                new Point(centerIndex, 0),             // arriba centro  (col=8,row=0)
                new Point(0, centerIndex),
                new Point(boardSize - 1, centerIndex)
        };

        int[] targetRows = { 0, boardSize - 1, -1, -1 };
        String[] symbols = { "①", "②", "③", "④" };
        int initialWalls = (count == 2) ? 10 : 5;

        for (int i = 0; i < count; i++) {
            System.out.print("Ingrese nombre para Jugador " + (i + 1) + " (símbolo " + symbols[i] + "): ");
            String name = inputScanner.nextLine();
            if (name == null || name.trim().isEmpty()) name = "Jugador " + (i + 1);
            Point p = startPositions[i];
            Player player = new Player(name, symbols[i], initialWalls, new Point(p.x, p.y), targetRows[i]);
            players.add(player);
        }
    }

    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        turnCount++;
    }

    public Player getCurrentPlayer() { return players.get(currentPlayerIndex); }
    public List<Player> getPlayers() { return players; }
    public char[][] getGrid() {
        board.updatePlayers(players);
        return board.getGrid();
    }
    public int getBoardSize() { return boardSize; }
    public int getTurnCount() { return turnCount; }
    public boolean getPlacementMode() { return placementMode; }
    public void setPlacementMode(boolean mode) { this.placementMode = mode; }

    public boolean hasWinner() { return winner != null; }
    public Player getWinner() { return winner; }

    public void processInput(char input) {
        if (placementMode) {
            if (input == 'F') {
                placementMode = false;
                System.out.println("Modo Muro cancelado. Ingrese otra acción.");
                return;
            }
            if (attemptWallPlacement(input)) {
                nextTurn();
                placementMode = false;
            }
        } else {
            switch (input) {
                case 'W','S','A','D' -> {
                    boolean moved = attemptMove(getCurrentPlayer(), input);
                    if (moved) {
                        if (checkWin()) return;
                        nextTurn();
                    }
                }
                case 'F' -> {
                    if (getCurrentPlayer().getWallsRemaining() > 0) {
                        placementMode = true;
                        System.out.println(">>> MODO MURO ACTIVADO: Ingrese la posición (WASD) para colocar la esquina/centro del muro (o 'F' para cancelar).");
                    } else {
                        System.out.println("No te quedan muros, ¡debes moverte!");
                    }
                }
                default -> System.out.println("Comando inválido. Usa W, A, S, D, o F.");
            }
        }
    }

    /**
     * Intenta mover al jugador en la dirección especificada.
     * No hay saltos, se aplica la regla de que ambos jugadores pueden ocupar la misma casilla.
     */
    private boolean attemptMove(Player player, char input) {
        Point current = player.getCurrentPosition();
        Point next = new Point(current.x, current.y);
        Direction dir = null;
        switch (input) {
            case 'W' -> dir = Direction.UP;
            case 'S' -> dir = Direction.DOWN;
            case 'A' -> dir = Direction.LEFT;
            case 'D' -> dir = Direction.RIGHT;
        }
        if (dir == null) return false;

        int step = 2;
        switch (dir) {
            case UP -> next.translate(0, -step);
            case DOWN -> next.translate(0, step);
            case LEFT -> next.translate(-step, 0);
            case RIGHT -> next.translate(step, 0);
        }

        // Solo moverse a nodos (índices pares)
        if ((next.x % 2 != 0) || (next.y % 2 != 0)) {
            System.out.println("¡ERROR! Movimiento inválido (solo a casillas).");
            return false;
        }

        if (isValidMove(current, next, dir)) {
            player.setCurrentPosition(next);
            return true;
        }
        return false;
    }

    /** Luis estuvo aqui >:(
     * Valida si un movimiento es legal.
     * Solo valida muros y límites del tablero.
     * NO valida jugadores (pueden compartir casilla).
     */
    private boolean isValidMove(Point current, Point next, Direction direction) {
        // Validar límites del tablero
        if (next.x < 0 || next.x >= boardSize || next.y < 0 || next.y >= boardSize) {
            System.out.println("¡ERROR! Movimiento fuera de los límites del tablero.");
            return false;
        }

        // Calcular posición del muro intermedio
        int wallY = current.y + (next.y - current.y) / 2;
        int wallX = current.x + (next.x - current.x) / 2;

        // Validar que el muro intermedio esté dentro de límites
        if (wallY < 0 || wallY >= boardSize || wallX < 0 || wallX >= boardSize) {
            System.out.println("¡ERROR! Movimiento fuera de los límites del tablero.");
            return false;
        }

        // Validar que NO haya un muro bloqueando el camino
        if (board.getGrid()[wallY][wallX] == GameBoard.WALL_SOLID) {
            System.out.println("¡ERROR! Muro bloqueando el camino.");
            return false;
        }

        // pueden compartir casilla
        return true;
    }

    private boolean attemptWallPlacement(char input) {
        Player current = getCurrentPlayer();
        Point playerPos = current.getCurrentPosition();
        Point wallCenter = new Point(playerPos.x, playerPos.y);
        boolean isHorizontal = true;

        switch (input) {
            case 'W' -> { wallCenter.translate(0, -1); isHorizontal = true; }
            case 'S' -> { wallCenter.translate(0, 1); isHorizontal = true; }
            case 'A' -> { wallCenter.translate(-1, 0); isHorizontal = false; }
            case 'D' -> { wallCenter.translate(1, 0); isHorizontal = false; }
            default -> {
                System.out.println("ERROR: Usa W, A, S, D para la posición del muro.");
                return false;
            }
        }

        if (wallCenter.x < 0 || wallCenter.x >= boardSize || wallCenter.y < 0 || wallCenter.y >= boardSize) {
            System.out.println("ERROR: Posición de muro fuera de los límites.");
            return false;
        }

        if (!current.useWall()) {
            System.out.println("No tienes muros disponibles.");
            return false;
        }

        if (board.placeWall(wallCenter, isHorizontal)) {
            System.out.println("Muro colocado exitosamente.");
            return true;
        } else {
            System.out.println("ERROR: No se pudo colocar el muro (colisión o fuera de límites).");
            current.refundWall();
            return false;
        }
    }

    public boolean checkWin() {
        for (Player p : players) {
            int target = p.getTargetRow();
            if (target != -1) {
                if (p.getCurrentPosition().y == target) {
                    winner = p;
                    return true;
                }
            }
        }
        return false;
    }

    /**Luis estuvo aqui >:(
     * Muestra el resumen completo del juego al finalizar.
     * Incluye posiciones iniciales, recorrido de cada jugador y resultado final.
     */
    public void showGameSummary() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("               RESUMEN DEL JUEGO");
        System.out.println("=".repeat(60));



        // Sección 1: Recorrido de cada jugador
        System.out.println("\nRECORRIDO DE JUGADORES:");
        System.out.println("-".repeat(60));
        for (Player p : players) {
            System.out.println("\n" + p.getSymbol() + " " + p.getName() + ":");
            List<Point> history = p.getMovementHistory();
            for (int i = 0; i < history.size(); i++) {
                Point pos = history.get(i);
                int visualCol = (pos.x / 2) + 1;
                int visualRow = (pos.y / 2) + 1;
                String label = (i == 0) ? "(Inicio)" : "";
                System.out.println("    Movimiento " + i + ": Columna " + visualCol +
                        ", Fila " + visualRow + " " + label);
            }
        }

        // Sección 2: Resultado
        System.out.println("\n" + "=".repeat(60));
        System.out.println("RESULTADO:");
        System.out.println("-".repeat(60));
        if (winner != null) {
            System.out.println("  ¡GANADOR: " + winner.getName() + " (" + winner.getSymbol() + ")!");
        } else {
            System.out.println("  PARTIDA INTERRUMPIDA");
            System.out.println("  Último turno: " + getCurrentPlayer().getName() +
                    " (" + getCurrentPlayer().getSymbol() + ")");
        }
        System.out.println("=".repeat(60) + "\n");
    }

}