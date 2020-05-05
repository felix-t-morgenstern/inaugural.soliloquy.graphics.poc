package math;

public interface Vector<TVector extends Vector> {
    float[] components();

    float magnitude();

    void normalize();

    TVector add(TVector toAdd) throws IllegalArgumentException;

    TVector subtract(TVector toSubtract) throws IllegalArgumentException;

    TVector multiply(float scalar);

    TVector negate();

    TVector divide(float scalar) throws IllegalArgumentException;

    float distance(TVector comparand) throws IllegalArgumentException;

    float dotProduct(TVector comparand) throws IllegalArgumentException;

    TVector crossProduct(TVector comparand)  throws IllegalArgumentException;
}
