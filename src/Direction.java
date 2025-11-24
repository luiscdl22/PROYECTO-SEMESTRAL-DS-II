public enum Direction {
    UP('↑'),
    DOWN('↓'),
    LEFT('←'),
    RIGHT('→');

    /*Almacena el caracter Unicode de cada direccion*/
    private final char symbol;

    Direction(char symbol) {
        this.symbol = symbol;
    }

    /* Devuelve el simbolo de la Direccion */
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
