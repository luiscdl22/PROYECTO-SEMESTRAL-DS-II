import java.io.IOException;
import java.util.Scanner;

public class Main {

    private static QuoridorGame game;
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final int GAME_GRID_NODES = 9;

    public static void main(String[] args) {
        try {
            // --- Pantalla de inicio ---
            printTitleScreen();

            // Solo permitir **2 jugadores** (reducción solicitada)
            int playerCount = 2;

            // Crear juego con 2 jugadores
            game = new QuoridorGame( SCANNER, GAME_GRID_NODES);

            // --- Animación de centrado ---
            try {
                simulateCenteringAnimation();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Animación interrumpida.");
            }

            // --- Bucle del juego ---
            boolean gameOver = false;
            while (!gameOver) {
                drawGame();
                handleInputBlocking();

                if (game.checkWin()) {
                    gameOver = true;
                }
            }

            // Mostrar tablero final + resumen
            drawGame();
            game.showGameSummary();
            System.out.println("\nGracias por jugar.");

        } catch (IOException e) {
            System.err.println("Error de E/S: " + e.getMessage());
        } finally {
            SCANNER.close();
        }
    }

    private static void printTitleScreen() throws IOException {
        System.out.print("\033[H\033[2J");
        System.out.flush();

        String title = "QUORIDOR (CORRIDOR)";
        String prompt = "Presiona ENTER para Jugar";
        String spaces = "                                      ";

        System.out.println(spaces + "╔════════════════════════════════════╗");
        System.out.println(spaces + "║" + title + " ".repeat(Math.max(0, 38 - title.length())) + "║");
        System.out.println(spaces + "║" + " ".repeat(38) + "║");
        System.out.println(spaces + "║" + prompt + " ".repeat(Math.max(0, 38 - prompt.length())) + "║");
        System.out.println(spaces + "╚════════════════════════════════════╝");
        System.out.println("\n\n");
        System.out.println(spaces + "Usa WASD para mover, F para muro.");
        System.out.println(spaces + "Escribe EXIT o Q para salir.");
        System.out.println(spaces + "Presiona ENTER para empezar...");

        System.in.read();
        while (System.in.available() > 0) System.in.read();
    }

    private static void simulateCenteringAnimation() throws InterruptedException {
        int gridWidth = (17 * 2) + 1;
        int steps = 10;
        for (int i = steps; i >= 0; i--) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
            String leadingSpaces = " ".repeat(i * 2);
            System.out.println(leadingSpaces + "Cargando tablero...");
            System.out.println(leadingSpaces + "╔" + "═".repeat(gridWidth) + "╗");
            for (int y = 0; y < GAME_GRID_NODES; y++) {
                System.out.println(leadingSpaces + "║" + " ".repeat(gridWidth) + "║");
            }
            System.out.println(leadingSpaces + "╚" + "═".repeat(gridWidth) + "╝");
            Thread.sleep(60);
        }
    }

    private static void handleInputBlocking() {
        Player currentPlayer = game.getCurrentPlayer();
        String prompt = game.getPlacementMode()
                ? "Acción (W/A/S/D para posicionar muro, F para cancelar): "
                : "Acción (WASD para mover, F para muro, EXIT para salir): ";

        var pos = currentPlayer.getCurrentPosition();
        int visualCol = (pos.x / 2) + 1;
        int visualRow = (pos.y / 2) + 1;

        System.out.println("\n*** Turno de " + currentPlayer.getName() +
                " (" + currentPlayer.getSymbol() + ") ***");
        System.out.println("Posición actual: (" + visualCol + "," + visualRow + ")");
        System.out.print(prompt);

        if (SCANNER.hasNextLine()) {
            String line = SCANNER.nextLine().trim().toUpperCase();

            if (line.equals("EXIT")) {
                System.out.println("\nSaliendo del juego...\n");
                game.showGameSummary();
                System.exit(0);
            }

            if (line.length() == 1) {
                game.processInput(line.charAt(0));
            } else if (!line.isEmpty()) {
                System.out.println("Comando inválido.");
            }
        }
    }

    private static void drawGame() {
        System.out.print("\033[H\033[2J");
        System.out.flush();

        char[][] grid = game.getGrid();
        final int nodes = 9;

        // Encabezado columnas
        System.out.print("    ");
        for (int c = 1; c <= nodes; c++) {
            System.out.print(String.format(" %2d ", c));
        }
        System.out.println();

        // Línea superior
        System.out.print("   ┌");
        for (int col = 0; col < nodes; col++) {
            System.out.print("───" + (col < nodes - 1 ? "┬" : "┐"));
        }
        System.out.println();

        // Filas
        for (int i = 0; i < nodes; i++) {
            int gy = i * 2;

            System.out.printf("%2d │", i + 1);
            for (int j = 0; j < nodes; j++) {
                int gx = j * 2;

                char nodeChar = grid[gy][gx];
                String cell = (nodeChar == GameBoard.NODE_EMPTY)
                        ? "   "
                        : " " + nodeChar + " ";

                System.out.print(cell);

                if (j < nodes - 1) {
                    System.out.print(grid[gy][gx + 1] == GameBoard.WALL_SOLID ? "│" : " ");
                } else {
                    System.out.print("│");
                }
            }

            // Info lateral (solo 2 jugadores)
            if (i < game.getPlayers().size()) {
                Player p = game.getPlayers().get(i);
                boolean actual = (p == game.getCurrentPlayer());
                System.out.printf("  | %c %s %-10s | Muros: %d",
                        actual ? '●' : '○',
                        p.getSymbol(),
                        p.getName(),
                        p.getWallsRemaining());
            }
            System.out.println();

            if (i < nodes - 1) {
                System.out.print("   ├");
                for (int col = 0; col < nodes; col++) {
                    char mid = grid[gy + 1][col * 2];
                    System.out.print(mid == GameBoard.WALL_SOLID ? "───" : "   ");
                    System.out.print(col < nodes - 1 ? "┼" : "┤");
                }
                System.out.println();
            } else {
                System.out.print("   └");
                for (int col = 0; col < nodes; col++) {
                    System.out.print("───" + (col < nodes - 1 ? "┴" : "┘"));
                }
                System.out.println();
            }
        }

        System.out.println();
    }
}
