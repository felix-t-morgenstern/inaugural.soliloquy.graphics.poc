package math;

public class Vector3f {
    public float _x, _y, _z;

    public Vector3f() {
        _x = 0.0f;
        _y = 0.0f;
        _z = 0.0f;
    }

    public Vector3f(float x, float y, float z) {
        _x = x;
        _y = y;
        _z = z;
    }

    public final float getX() {
        return _x;
    }

    public final void setX(float x) {
        _x = x;
    }

    public final float getY() {
        return _y;
    }

    public final void setY(float y) {
        _y = y;
    }

    public final float getZ() {
        return _z;
    }

    public final void setZ(float z) {
        _z = z;
    }


}
