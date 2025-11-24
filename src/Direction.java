public enum Direction {
    UP('↑'),
    DOWN('↓'),
    LEFT('←'),
    RIGHT('→');


    private final char symbol;

    Direction(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }

    /**
     * Comprueba si la dirección actual es opuesta a otra.
     */
    public boolean isOpposite(Direction other) {
        return (this == UP && other == DOWN) ||
                (this == DOWN && other == UP) ||
                (this == LEFT && other == RIGHT) ||
                (this == RIGHT && other == LEFT);
    }


}
