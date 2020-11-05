package math;

import java.text.MessageFormat;

public class Color {
    private float _red;
    private float _green;
    private float _blue;
    private float _alpha;

    public Color(float red, float green, float blue) {
        setRed(red);
        setBlue(blue);
        setGreen(green);
        setAlpha(1.0f);
    }

    public Color(float red, float green, float blue, float alpha) {
        setRed(red);
        setBlue(blue);
        setGreen(green);
        setAlpha(alpha);
    }

    public float getRed() {
        return _red;
    }

    public void setRed(float red) {
        _red = clamp(red);
    }

    public float getGreen() {
        return _green;
    }

    public void setGreen(float green) {
        _green = clamp(green);
    }

    public float getBlue() {
        return _blue;
    }

    public void setBlue(float blue) {
        _blue = clamp(blue);
    }

    public float getAlpha() {
        return _alpha;
    }

    public void setAlpha(float alpha) {
        _alpha = clamp(alpha);
    }

    private float clamp(float val) {
        return Math.clamp(val, 0.0f, 1.0f);
    }

    @Override
    public String toString() {
        return MessageFormat.format("({0},{1},{2},{3})", _red, _green, _blue, _alpha);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Color)) {
            return false;
        }
        return ((Color) o)._red == _red
                && ((Color) o)._green == _green
                && ((Color) o)._blue == _blue
                && ((Color) o)._alpha == _alpha;
    }

    public static Color black() {
        return new Color(0, 0, 0);
    }

    public static Color white() {
        return new Color(1, 1, 1);
    }

    public static Color red() {
        return new Color(1, 0, 0);
    }

    public static Color green() {
        return new Color(0, 1, 0);
    }

    public static Color blue() {
        return new Color(0, 0, 1);
    }

    public static Color grey() {
        return new Color(0.5f, 0.5f, 0.5f);
    }

    public static Color wine() {
        return new Color(0.5f, 0, 0);
    }

    public static Color forest() {
        return new Color(0, 0.5f, 0);
    }

    public static Color marine() {
        return new Color(0, 0, 0.5f);
    }

    public static Color yellow() {
        return new Color(1, 1, 0);
    }

    public static Color cyan() {
        return new Color(0, 1, 1);
    }

    public static Color magenta() {
        return new Color(1, 0, 1);
    }
}
