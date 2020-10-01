package world;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import render.Camera;
import render.Model;
import render.Shader;
import render.Texture;

import java.util.HashMap;

public class TileRenderer {
    private HashMap<String, Texture> _tileTextures;
    private Model _model;

    public TileRenderer(Model model) {
        _tileTextures = new HashMap<>();

        _model = model;

        for(int i = 0; i < Tile.TILES.length; i++) {
            if (Tile.TILES[i] != null) {
                if (!_tileTextures.containsKey(Tile.TILES[i].getTexture())) {
                    String texture = Tile.TILES[i].getTexture();
                    _tileTextures.put(texture, new Texture(texture + ".png"));
                }
            }
        }
    }

    public void renderTile(Tile tile, int x, int y, Shader shader, Matrix4f world, Camera camera) {
        shader.bind();
        if (_tileTextures.containsKey(tile.getTexture())) {
            _tileTextures.get(tile.getTexture()).bind(0);
        }

        Matrix4f tilePosition = new Matrix4f().translate(new Vector3f(x*2f, y*2f, 0));

        Matrix4f target = new Matrix4f();
        camera.getProjection().mul(world, target);

        target.mul(tilePosition);

        shader.setUniform("sampler", 0);
        shader.setUniform("projection", target);

        _model.render();
    }
}
