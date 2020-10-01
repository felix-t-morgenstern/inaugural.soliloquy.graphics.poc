package collision;

import org.joml.Vector2f;

public class Aabb {
    private Vector2f _center, _halfExtent;

    public Aabb(Vector2f center, Vector2f halfExtent) {
        _center = center;
        _halfExtent = halfExtent;
    }

    public Vector2f getCenter() {
        return _center;
    }

    public Vector2f getHalfExtent() {
        return _halfExtent;
    }

    public Collision getCollision(Aabb box2) {
        Vector2f distance = box2._center.sub(_center, new Vector2f());
        distance.x = Math.abs(distance.x);
        distance.y = Math.abs(distance.y);

        distance.sub(_halfExtent.add(box2._halfExtent, new Vector2f()));

        return new Collision(distance, distance.x < 0 && distance.y < 0);
    }

    public void correctPosition(Aabb box2, Collision data) {
        Vector2f correction = box2._center.sub(_center, new Vector2f());
        if (data._distance.x > data._distance.y) {
            if (correction.x > 0) {
                _center.add(data._distance.x, 0);
            } else {
                _center.add(-data._distance.x, 0);
            }
        } else {
            if (correction.y > 0) {
                _center.add(0, data._distance.y);
            } else {
                _center.add(0, -data._distance.y);
            }
        }
    }
}
