package render;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Vector3f _position;
    private Matrix4f _projection;

    public Camera(int width, int height) {
        _position = new Vector3f(0,0,0);
        _projection = new Matrix4f().setOrtho2D(-width/2f, width/2f, -height/2f, height/2f);
    }

    public void setPosition(Vector3f position) {
        _position = position;
    }

    public void addPosition(Vector3f position) {
        _position.add(position);
    }

    public Vector3f getPosition() {
        return _position;
    }

    public Matrix4f getProjection() {
        return _projection.translate(_position, new Matrix4f());
    }
}
