package math;

import org.joml.Matrix4f;

import java.nio.FloatBuffer;

public class Quaternion {
    public static final int QUATERNION_SIZE = 16;

    private Matrix4f _matrix4f;

    public Quaternion() {
        _matrix4f = new Matrix4f();
        _matrix4f.identity();
    }

    public void identity() {
        _matrix4f.identity();
    }

    public void writeToBuffer(FloatBuffer floatBuffer) {
        floatBuffer.put(_matrix4f.m00());
        floatBuffer.put(_matrix4f.m01());
        floatBuffer.put(_matrix4f.m02());
        floatBuffer.put(_matrix4f.m03());

        floatBuffer.put(_matrix4f.m10());
        floatBuffer.put(_matrix4f.m11());
        floatBuffer.put(_matrix4f.m12());
        floatBuffer.put(_matrix4f.m13());

        floatBuffer.put(_matrix4f.m20());
        floatBuffer.put(_matrix4f.m21());
        floatBuffer.put(_matrix4f.m22());
        floatBuffer.put(_matrix4f.m23());

        floatBuffer.put(_matrix4f.m30());
        floatBuffer.put(_matrix4f.m31());
        floatBuffer.put(_matrix4f.m32());
        floatBuffer.put(_matrix4f.m33());

        floatBuffer.flip();
    }

    public static Quaternion orthographic(float left,
                                          float right,
                                          float bottom,
                                          float top,
                                          float near,
                                          float far) {
        Quaternion quaternion = new Quaternion();

        float width = right - left;
        float height = top - bottom;
        float depth = far - near;

        quaternion._matrix4f.m00(2f / width);
        quaternion._matrix4f.m11(2f / height);
        quaternion._matrix4f.m22(2f / depth);

        quaternion._matrix4f.m30(-(right + left) / width);
        quaternion._matrix4f.m31(-(top + bottom) / height);
        quaternion._matrix4f.m32(-(far + near) / depth);

        return quaternion;
    }
}
