package engine;

import org.joml.Vector2f;

public class Rectangle {
    public float _x;
    public float _y;
    public float _width;
    public float _height;

    public Rectangle(float x, float y, float width, float height) {
        _x = x;
        _y = y;
        _width = width;
        _height = height;
    }

    public boolean contains(Vector2f position) {
        return position.x >= _x && position.x <= (_x + _width)
                && position.y >= _y && position.y <= (_y + _height);
    }
}
