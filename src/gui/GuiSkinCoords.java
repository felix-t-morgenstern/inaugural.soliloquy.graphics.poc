package gui;

public class GuiSkinCoords {
    private float _top;
    private float _right;
    private float _bottom;
    private float _left;

    public GuiSkinCoords(float top, float right, float bottom, float left) {
        _top = top;
        _right = right;
        _bottom = bottom;
        _left = left;
    }

    public float top() {
        return _top;
    }

    public float right() {
        return _right;
    }

    public float bottom() {
        return _bottom;
    }

    public float left() {
        return _left;
    }
}
