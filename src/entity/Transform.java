package entity;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform {
    public Vector3f _position;
    public Vector3f _scale;

    public Transform() {
        _position = new Vector3f();
        _scale = new Vector3f();
    }

    public Matrix4f getProjection(Matrix4f target) {
        target.scale(_scale);
        target.translate(_position);
        return target;
    }
}
