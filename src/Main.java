import java.io.IOException;
import java.util.Scanner;

public class Main {


    private static QuoridorGame game;
    private static final Scanner SCANNER = new Scanner(System.in);
    // Tamaño estándar de Quoridor: 9x9 nodos (matriz interna 17x17)
    private static final int GAME_GRID_NODES = 9;

    public static void main(String[] args) throws IOException {

        // --- Fase 0: Menú de Inicio Centrado ---
        printTitleScreen();

        // 1. Inicializar el juego y pedir nombres
        game = new QuoridorGame(SCANNER, GAME_GRID_NODES);


        // 2. Simular animación de "movimiento"
        try {
            simulateCenteringAnimation();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("La animación de carga fue interrumpida.");
        }

        // --- Bucle principal del juego (POR TURNOS) ---
        boolean gameOver = false;
        while (!gameOver) {
            // 1. Dibujar el estado actual (Tablero + Info de Jugadores)
            drawGame();

            // 2. Capturar y procesar la entrada del usuario (WASD/F)
            handleInputBlocking();

            // 3. Comprobar victoria
            if (game.checkWin()) {
                gameOver = true;
            }
        }

        // Fin del juego
        System.out.println("\n--- ¡JUEGO TERMINADO! ---");
        if (game.hasWinner()) {
            System.out.println("¡Felicitaciones al ganador: " + game.getWinner().getName() + "!");
        } else {
            System.out.println("Juego terminado sin ganador declarado.");
        }
        System.out.println("Gracias por jugar, Roseman.");
        SCANNER.close();
    }

    private static void printTitleScreen() throws IOException {
        // Limpiar la consola (muchos terminales lo usan)
        System.out.print("\033[H\033[2J");
        System.out.flush();

        String title = "QUORIDOR(CORRIDOR)";
        String prompt = "Presiona ENTER al Jugar?";
        String spaces = "                                      "; // Aprox.

        System.out.println(spaces + "╔════════════════════════════════════╗");
        System.out.println(spaces + "║" + title + " ".repeat(Math.max(0, 38 - title.length())) + "║");
        System.out.println(spaces + "║" + " ".repeat(38) + "║");
        System.out.println(spaces + "║" + prompt + " ".repeat(Math.max(0, 38 - prompt.length())) + "║");
        System.out.println(spaces + "╚════════════════════════════════════╝");
        System.out.println("\n\n\n\n");
        System.out.println(spaces + "Usa W, A, S, D (mover) o F (muro). Presiona ENTER después de cada comando.");
        System.out.println(spaces + "Presiona 'Q' para salir. Presiona ENTER para empezar...");

        System.in.read();
        while (System.in.available() > 0) {
            System.in.read();
        }
    }

    private static void simulateCenteringAnimation() throws InterruptedException {
        int gridWidth = (17 * 2) + 1;
        int steps = 10;
        for (int i = steps; i >= 0; i--) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
            String leadingSpaces = " ".repeat(i * 2);
            System.out.println(leadingSpaces + "Simulando carga y centrado...");
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
                ? "Acción (W/A/S/D para muro, F para cancelar): "
                : "Acción (WASD para mover, F para muro): ";

        System.out.println("\n*** Turno de " + currentPlayer.getName() + " (" + currentPlayer.getSymbol() + ") ***");
        System.out.print(prompt);

        if (SCANNER.hasNextLine()) {
            String line = SCANNER.nextLine();
            if (line.length() > 0) {
                char input = Character.toUpperCase(line.charAt(0));
                if (input == 'Q') {
                    System.exit(0);
                }
                game.processInput(input);
            }
        }
    }

    /**
     * Dibuja el tablero en estilo 9x9 visual usando la matriz interna 17x17 del GameBoard.
     * Cada casilla se representa con ancho fijo de 3 caracteres (para alinear con muros).
     */
    private static void drawGame() {
        // Limpiar consola
        System.out.print("\033[H\033[2J");
        System.out.flush();

        char[][] grid = game.getGrid(); // 17x17
        int internal = game.getBoardSize(); // 17
        final int nodes = 9; // 9 nodos por fila

        // Encabezado de columnas
        System.out.print("    ");
        for (int c = 1; c <= nodes; c++) {
            System.out.print(String.format(" %2d ", c));
        }
        System.out.println();

        // Línea superior del cuadro
        System.out.print("   ┌");
        for (int col = 0; col < nodes; col++) {
            System.out.print("───");
            if (col < nodes - 1) System.out.print("┬");
            else System.out.print("┐");
        }
        System.out.println();

        // Por cada fila de nodos (i)
        for (int i = 0; i < nodes; i++) {
            int gy = i * 2; // coordenada Y en grid (0,2,4,...,16)

            // Fila de contenido (jugadores y muros verticales)
            System.out.printf("%2d │", i + 1);
            for (int j = 0; j < nodes; j++) {
                int gx = j * 2; // coordenada X en grid
                // Nodo: grid[gy][gx]
                char nodeChar = grid[gy][gx];
                String cellStr;
                if (nodeChar == GameBoard.NODE_EMPTY) {
                    cellStr = "   ";
                } else {
                    // usamos el char (si es símbolo como '1' o primer char de '①')
                    cellStr = String.format(" %c ", nodeChar);
                }

                // imprimir celda
                System.out.print(cellStr);

                // imprimir muro vertical entre nodos j and j+1 (si existe)
                if (j < nodes - 1) {
                    char mid = grid[gy][gx + 1]; // posición de muro horizontal? for vertical walls mid is at odd x
                    if (mid == GameBoard.WALL_SOLID) {
                        System.out.print("│");
                    } else {
                        System.out.print(" ");
                    }
                } else {
                    System.out.print("│");
                }
            }
            // Información lateral de jugadores (turno / muros)
            // Mostrar hasta 2 jugadores (evitar overflow)
            if (i < game.getPlayers().size()) {
                Player p = game.getPlayers().get(i);
                boolean isCurrent = (game.getCurrentPlayer() == p);
                char turnIndicator = isCurrent ? '●' : '○';
                System.out.printf("  | %c %s %-10s | Muros: %d", turnIndicator, p.getSymbol(), p.getName(), p.getWallsRemaining());
            }
            System.out.println();

            // Línea separadora entre filas de nodos (dibuja muros horizontales si existen)
            if (i < nodes - 1) {
                System.out.print("   ├");
                for (int col = 0; col < nodes; col++) {
                    int gx = col * 2;
                    // Check horizontal wall at grid[gy+1][gx] (odd row, even col? Actually horizontal walls sit at even row, odd col in earlier representation;
                    // with our grid convention horizontal wall midpoints are at row = gy+1, col = gx)
                    char mid = grid[gy + 1][gx];
                    if (mid == GameBoard.WALL_SOLID) {
                        System.out.print("───");
                    } else {
                        System.out.print("   ");
                    }

                    if (col < nodes - 1) {
                        // intersection: check if corner (grid[gy+1][gx+1]) is a solid wall corner - not strictly needed
                        char corner = grid[gy + 1][gx + 1];
                        if (corner == GameBoard.WALL_SOLID) System.out.print("┼");
                        else System.out.print("┼");
                    } else {
                        System.out.print("┤");
                    }
                }
                System.out.println();
            } else {
                // última línea inferior
                System.out.print("   └");
                for (int col = 0; col < nodes; col++) {
                    System.out.print("───");
                    if (col < nodes - 1) System.out.print("┴");
                    else System.out.print("┘");
                }
                System.out.println();
            }
        }

        System.out.println();
    }

}
