package gui;

public class FontGlyph {
    private float _x;
    private float _y;
    private float _width;
    private float _height;
    private float _scaleX;
    private float _scaleY;

    public FontGlyph(float x, float y, float width, float height, float scaleX, float scaleY) {
        _x = x;
        _y = y;
        _width = width;
        _height = height;
        _scaleX = scaleX;
        _scaleY = scaleY;
    }

    public float getX() {
        return _x;
    }

    public void setX(float x) {
        _x = x;
    }

    public float getY() {
        return _y;
    }

    public void setY(float y) {
        _y = y;
    }

    public float getWidth() {
        return _width;
    }

    public void setWidth(float width) {
        _width = width;
    }

    public float getHeight() {
        return _height;
    }

    public void setHeight(float height) {
        _height = height;
    }

    public float getScaleX() {
        return _scaleX;
    }

    public void setScaleX(float scaleX) {
        _scaleX = scaleX;
    }

    public float getScaleY() {
        return _scaleY;
    }

    public void setScaleY(float scaleY) {
        _scaleY = scaleY;
    }
}
