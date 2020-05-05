package math;

public abstract class VectorImpl<TVector extends Vector> implements Vector<TVector> {
    protected final float[] COMPONENTS;

    public VectorImpl(float[] components) {
        // TODO: Test for non-zero length
        COMPONENTS = new float[components.length];
        System.arraycopy(components, 0, COMPONENTS, 0, components.length);
    }

    public float[] components() {
        return COMPONENTS;
    }

    @Override
    public float magnitude() {
        float result = 0f;
        for (float component : COMPONENTS) {
            result += component * component;
        }
        return result;
    }

    @Override
    public void normalize() {
        float magnitude = magnitude();
        for(int i = 0; i < COMPONENTS.length; i++) {
            COMPONENTS[i] /= magnitude;
        }
    }

    protected abstract TVector make(float[] components);

    @Override
    public TVector add(TVector toAdd) throws IllegalArgumentException {
        float[] newComponents = new float[COMPONENTS.length];
        for(int i = 0; i < COMPONENTS.length; i++) {
            newComponents[i] = COMPONENTS[i] + toAdd.components()[i];
        }
        return make(newComponents);
    }

    @Override
    public TVector subtract(TVector toSubtract) throws IllegalArgumentException {
        float[] newComponents = new float[COMPONENTS.length];
        for(int i = 0; i < COMPONENTS.length; i++) {
            newComponents[i] = COMPONENTS[i] - toSubtract.components()[i];
        }
        return make(newComponents);
    }

    @Override
    public TVector multiply(float scalar) {
        float[] newComponents = new float[COMPONENTS.length];
        for(int i = 0; i < COMPONENTS.length; i++) {
            newComponents[i] = COMPONENTS[i] * scalar;
        }
        return make(newComponents);
    }

    @Override
    public TVector negate() {
        float[] newComponents = new float[COMPONENTS.length];
        for(int i = 0; i < COMPONENTS.length; i++) {
            newComponents[i] = -COMPONENTS[i];
        }
        return make(newComponents);
    }

    @Override
    public TVector divide(float scalar) throws IllegalArgumentException {
        float[] newComponents = new float[COMPONENTS.length];
        for(int i = 0; i < COMPONENTS.length; i++) {
            newComponents[i] = COMPONENTS[i] * scalar;
        }
        return make(newComponents);
    }

    @Override
    public float distance(TVector comparand) throws IllegalArgumentException {
        return 0;
    }

    @Override
    public float dotProduct(TVector comparand) throws IllegalArgumentException {
        return 0;
    }

    @Override
    public TVector crossProduct(TVector comparand) throws IllegalArgumentException {
        return null;
    }
}
