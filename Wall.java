import java.awt.Point;

public class Wall {
    private final Point center; // Posición (x, y) del slot central en la grilla 17x17
    private final boolean isHorizontal; // true para horizontal, false para vertical

    /**
     * Constructor que inicializa un muro.
     * @param center La posición (x, y) del slot central del muro en la grilla.
     * @param isHorizontal true si el muro es horizontal, false si es vertical.
     */
    public Wall(Point center, boolean isHorizontal) {
        // Hacemos una copia defensiva del punto
        this.center = new Point(center.x, center.y);
        this.isHorizontal = isHorizontal;
    }

    /**
     * Obtiene la posición central del muro.
     * @return El punto central del muro.
     */
    public Point getCenter() {
        // Devolvemos una copia para evitar modificación externa
        return new Point(center.x, center.y);
    }

    /**
     * Verifica si el muro es horizontal.
     * @return true si es horizontal.
     */
    public boolean isHorizontal() {
        return isHorizontal;
    }

    /**
     * Verifica si el muro es vertical.
     * @return true si es vertical.
     */
    public boolean isVertical() {
        return !isHorizontal;
    }

    @Override
    public String toString() {
        String orientationStr = isHorizontal ? "Horizontal" : "Vertical";
        return "Wall " + orientationStr + " centered at (" + center.x + ", " + center.y + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Wall wall = (Wall) obj;
        return isHorizontal == wall.isHorizontal &&
               center.equals(wall.center);
    }

    // No se necesitan setters ya que un muro, una vez colocado, no cambia de posición.
}