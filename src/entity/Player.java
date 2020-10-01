package entity;

import collision.Aabb;
import collision.Collision;
import io.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import render.*;
import world.World;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;

public class Player {
    private Model _model;
    private Aabb _boundingBox;
    //private Texture _texture;
    private Animation _texture;
    private Transform _transform;

    public Player(Model model) {
        _model = model;
        //_texture = new Texture("test.png");
        _texture = new Animation(5, 10, "bloop");
        _transform = new Transform();
        _transform._scale = new Vector3f(16, 16, 1);

        _boundingBox = new Aabb(new Vector2f(_transform._position.x, _transform._position.y), new Vector2f(1,1));
    }

    public void update(float delta, Window window, Camera camera, World world) {

        if (window.input().isKeyDown(GLFW_KEY_A)) {
            _transform._position.add(new Vector3f(-10.0f * delta, 0.0f, 0.0f));
        }

        if (window.input().isKeyDown(GLFW_KEY_D)) {
            _transform._position.add(new Vector3f(10.0f * delta, 0.0f, 0.0f));
        }

        if (window.input().isKeyDown(GLFW_KEY_W)) {
            _transform._position.add(new Vector3f(0.0f, 10.0f * delta, 0.0f));
        }

        if (window.input().isKeyDown(GLFW_KEY_S)) {
            _transform._position.add(new Vector3f(0.0f, -10.0f * delta, 0.0f));
        }

        _boundingBox.getCenter().set(_transform._position.x, _transform._position.y);

        final int boundingBoxDiameter = 5;
        Aabb[] boxes = new Aabb[boundingBoxDiameter * boundingBoxDiameter];
        for (int i = 0; i < boundingBoxDiameter; i++) {
            for (int j = 0; j < boundingBoxDiameter; j++) {
                boxes[i + (j * 5)] = world.getTileBoundingBox(
                        (int)(((_transform._position.x / 2) + 0.5f) - (boundingBoxDiameter / 2)) + i,
                        (int)(((-_transform._position.y / 2) + 0.5f) - (boundingBoxDiameter / 2)) + j
                );
            }
        }

        Aabb box = null;

        for (Aabb aabb : boxes) {
            if (aabb != null) {
                if (box == null) {
                    box = aabb;
                }

                Vector2f length1 = box.getCenter().sub(_transform._position.x, _transform._position.y, new Vector2f());
                Vector2f length2 = aabb.getCenter().sub(_transform._position.x, _transform._position.y, new Vector2f());

                if (length1.lengthSquared() > length2.lengthSquared()) {
                    box = aabb;
                }
            }
        }

        if (box != null) {
            Collision collisionData = _boundingBox.getCollision(box);
            if (collisionData._isIntersecting) {
                _boundingBox.correctPosition(box, collisionData);
                _transform._position.set(_boundingBox.getCenter(), 0);
            }
        }

        box = null;
        for (Aabb aabb : boxes) {
            if (aabb != null) {
                if (box == null) {
                    box = aabb;
                }

                Vector2f length1 = box.getCenter().sub(_transform._position.x, _transform._position.y, new Vector2f());
                Vector2f length2 = aabb.getCenter().sub(_transform._position.x, _transform._position.y, new Vector2f());

                if (length1.lengthSquared() > length2.lengthSquared()) {
                    box = aabb;
                }
            }
        }

        if (box != null) {
            Collision collisionData = _boundingBox.getCollision(box);
            if (collisionData._isIntersecting) {
                _boundingBox.correctPosition(box, collisionData);
                _transform._position.set(_boundingBox.getCenter(), 0);
            }
        }

        camera.getPosition().lerp(_transform._position.mul(-world.getScale(), new Vector3f()), 0.05f);
        //camera.setPosition(_transform._position.mul(-world.getScale(), new Vector3f()));
    }

    public void render(Shader shader, Camera camera) {
        shader.bind();
        shader.setUniform("sampler", 0);
        shader.setUniform("projection", _transform.getProjection(camera.getProjection()));
        _texture.bind(0);
        _model.render();
    }
}
