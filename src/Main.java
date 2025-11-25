import java.io.IOException;
import java.util.Scanner;

public class Main {

    private static QuoridorGame game;
    private static final Scanner SCANNER = new Scanner(System.in);
    // Tamaño estándar de Quoridor: 9x9 nodos (matriz interna 17x17)
    private static final int GAME_GRID_NODES = 9;

    public static void main(String[] args) {
        try {
            // --- Fase 0: Menú de Inicio Centrado ---
            printTitleScreen();

            // 1. Pedir número de jugadores
            int playerCount = askPlayerCount();

            // 2. Inicializar el juego y pedir nombres
            game = new QuoridorGame(playerCount, SCANNER, GAME_GRID_NODES);

            // 3. Simular animación de "movimiento"
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

                // 2. Capturar y procesar la entrada del usuario (WASD/F/EXIT)
                handleInputBlocking();

                // 3. Comprobar victoria
                if (game.checkWin()) {
                    gameOver = true;
                }
            }

            // Mostrar estado final y resumen
            drawGame(); // Mostrar tablero final una vez más
            game.showGameSummary(); // Mostrar resumen completo

            System.out.println("\nGracias por jugar.");

        } catch (IOException e) {
            System.err.println("Error de entrada/salida: " + e.getMessage());
        } finally {
            SCANNER.close();
        }
    }

    private static void printTitleScreen() throws IOException {
        // Limpiar la consola
        System.out.print("\033[H\033[2J");
        System.out.flush();

        String title = "QUORIDOR(CORRIDOR)";
        String prompt = "Presiona ENTER para Jugar";
        String spaces = "                                      ";

        System.out.println(spaces + "╔════════════════════════════════════╗");
        System.out.println(spaces + "║" + title + " ".repeat(Math.max(0, 38 - title.length())) + "║");
        System.out.println(spaces + "║" + " ".repeat(38) + "║");
        System.out.println(spaces + "║" + prompt + " ".repeat(Math.max(0, 38 - prompt.length())) + "║");
        System.out.println(spaces + "╚════════════════════════════════════╝");
        System.out.println("\n\n");
        System.out.println(spaces + "Usa W, A, S, D (mover) o F (muro).");
        System.out.println(spaces + "Escribe 'EXIT' para salir en cualquier momento.");
        System.out.println(spaces + "\nPresiona ENTER para empezar...");

        // Leer ENTER del usuario y limpiar buffer de entrada
        try {
            System.in.read();
            while (System.in.available() > 0) {
                System.in.read();
            }
        } catch (IOException e) {
            // Si falla la lectura continuar con el juego
            System.err.println("Advertencia: No se pudo leer la entrada correctamente.");
        }

    }

    private static int askPlayerCount() {
        int count = 0;
        while (count < 2 || count > 4) {
            System.out.print("¿Cuántos jugadores (2-4)? ");

            //Luis estuvo Aqui >:(
            // Validar que la entrada sea un número válido
            if (SCANNER.hasNextInt()) {
                count = SCANNER.nextInt();
                SCANNER.nextLine(); // Consumir salto de línea
            } else {
                // Si no es número, limpiar buffer y volver a preguntar
                System.out.println("Entrada inválida. Por favor ingrese un número entre 2 y 4.");
                SCANNER.nextLine(); //Limpiar buffer para evitar bucle infinito
            }

        }
        return count;
    }

    private static void simulateCenteringAnimation() throws InterruptedException {
        int gridWidth = (17 * 2) + 1;
        int steps = 10;
        for (int i = steps; i >= 0; i--) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
            String leadingSpaces = " ".repeat(i * 2);
            System.out.println(leadingSpaces + "Cargando tablero.......................");
            System.out.println(leadingSpaces + "╔" + "═".repeat(gridWidth) + "╗");
            for (int y = 0; y < GAME_GRID_NODES; y++) {
                System.out.println(leadingSpaces + "║" + " ".repeat(gridWidth) + "║");
            }
            System.out.println(leadingSpaces + "╚" + "═".repeat(gridWidth) + "╝");
            Thread.sleep(60);
        }
    }

    /**
     * Captura y procesa la entrada del usuario.
     * Permite comandos: W, A, S, D (movimiento), F (muro), EXIT (salir).
     */
    private static void handleInputBlocking() {
        Player currentPlayer = game.getCurrentPlayer();
        String prompt = game.getPlacementMode()
                ? "Acción (W/A/S/D para posicionar muro, F para cancelar): "
                : "Acción (WASD para mover, F para muro, EXIT para salir): ";

        //Luis estuvo aqui >:(
        // Mostrar información del turno actual
        java.awt.Point pos = currentPlayer.getCurrentPosition();
        int visualCol = (pos.x/2)+1;
        int visualRow = (pos.y/2)+ 1;
        System.out.println("\n*** Turno de " + currentPlayer.getName() + " (" + currentPlayer.getSymbol() + ") ***");
        System.out.println("Posición actual: (" + visualCol + "," + visualRow + ") ");
        System.out.print(prompt);

        //Luis estuvo Aqui >:(
        // Leer y procesar comando del usuario
        if (SCANNER.hasNextLine()) {
            String line = SCANNER.nextLine().trim().toUpperCase();

            // Verificar si el usuario quiere salir
            if (line.equals("EXIT")) {
                System.out.println("\n¡Saliendo del juego...\n");
                game.showGameSummary();
                System.out.println("Gracias por jugar.");
                SCANNER.close();
                System.exit(0);
            }

            // Procesar comandos de un solo carácter (W, A, S, D, F)
            if (line.length() == 1) {
                char input = line.charAt(0);
                game.processInput(input);
            } else if (line.length() > 0) {
                System.out.println("Comando inválido. Usa W, A, S, D, F, o EXIT.");
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
                    // Mostrar el carácter del jugador
                    cellStr = String.format(" %c ", nodeChar);
                }

                // Imprimir celda
                System.out.print(cellStr);

                // Imprimir muro vertical entre nodos j y j+1 (si existe)
                if (j < nodes - 1) {
                    char mid = grid[gy][gx + 1];
                    if (mid == GameBoard.WALL_SOLID) {
                        System.out.print("│");
                    } else {
                        System.out.print(" "); // esta linea es sossspechosaaa mmmmm
                    }
                } else {
                    System.out.print("│");
                }
            }

            // Información lateral de jugadores (turno / muros)
            if (i < game.getPlayers().size()) {
                Player p = game.getPlayers().get(i);
                boolean isCurrent = (game.getCurrentPlayer() == p);
                char turnIndicator = isCurrent ? '●' : '○';
                System.out.printf("  | %c %s %-10s | Muros: %d",
                        turnIndicator, p.getSymbol(), p.getName(), p.getWallsRemaining());
            }
            System.out.println();

            // Línea separadora entre filas de nodos (dibuja muros horizontales si existen)
            if (i < nodes - 1) {
                System.out.print("   ├");
                for (int col = 0; col < nodes; col++) {
                    int gx = col * 2;
                    // Verificar muro horizontal en grid[gy+1][gx]
                    char mid = grid[gy + 1][gx];
                    if (mid == GameBoard.WALL_SOLID) {
                        System.out.print("───");
                    } else {
                        System.out.print("   ");
                    }

                    if (col < nodes - 1) {
                        // Intersección
                        System.out.print("┼");
                    } else {
                        System.out.print("┤");
                    }
                }
                System.out.println();
            } else {
                // Última línea inferior
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