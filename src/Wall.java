import java.awt.Point;

public class Wall {
    private final Point center;
    private final boolean isHorizontal;

    public Wall(Point center, boolean isHorizontal) {
        this.center = new Point(center.x, center.y);
        this.isHorizontal = isHorizontal;
    }

    public Point getCenter() {
        return new Point(center.x, center.y);
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

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
        if (!(obj instanceof Wall)) return false;
        Wall w = (Wall) obj;
        return isHorizontal == w.isHorizontal && center.equals(w.center);
    }
}
