package math;

public class Vector2d extends VectorImpl<Vector2d> {
    private final int X_INDEX = 0;
    private final int Y_INDEX = 1;

    public Vector2d(float x, float y) {
        super(new float[]{x, y});
    }

    @Override
    protected Vector2d make(float[] components) {
        return new Vector2d(components[X_INDEX], components[Y_INDEX]);
    }

    public float getX() {
        return COMPONENTS[X_INDEX];
    }

    public void setX(float x) {
        COMPONENTS[X_INDEX] = x;
    }

    public float getY() {
        return COMPONENTS[Y_INDEX];
    }

    public void setY(float y) {
        COMPONENTS[Y_INDEX] = y;
    }
}
