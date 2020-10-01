package collision;

import org.joml.Vector2f;

public class Collision {
    public Vector2f _distance;
    public boolean _isIntersecting;

    public Collision(Vector2f distance, boolean isIntersecting) {
        _distance = distance;
        _isIntersecting = isIntersecting;
    }
}
