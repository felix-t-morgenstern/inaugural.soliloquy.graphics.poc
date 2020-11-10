package engine;

import org.joml.Vector2f;

public class Rectangle {
    public float x;
    public float y;
    public float width;
    public float height;

    public Rectangle(float x, float y, float width, float height) {
        set(x, y, width, height);
    }

    public boolean contains(Vector2f position) {
        return position.x >= x && position.x <= (x + width)
                && position.y >= y && position.y <= (y + height);
    }

    public void set(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean intersects(Rectangle r) {
        return !(x > r.x + r.width ||
                x + width < r.x ||
                y > r.y + r.height ||
                y + height < r.y);
    }

    public Rectangle getIntersection(Rectangle r) {
        if (!intersects(r)) {
            return null;
        }

        Vector2f intersectionOrigin = new Vector2f(Math.max(x, r.x), Math.max(y, r.y));
        return new Rectangle(intersectionOrigin.x, intersectionOrigin.y,
                Math.min(x + width, r.x + r.width) - intersectionOrigin.x,
                Math.min(y + height, r.y + r.height) - intersectionOrigin.y);
    }
}
