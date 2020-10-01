package world;

import collision.Aabb;
import io.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import render.Camera;
import render.Shader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class World {
    private final int VIEW_X = 42;
    private final int VIEW_Y = 24;

    private byte[] _tiles;
    private Aabb[] _boundingBoxes;
    private int _width;
    private int _height;
    private int _scale;
    private Matrix4f _world;

    public World() {
        _width = 64;
        _height = 64;
        _scale = 16;

        _tiles = new byte[_width * _height];
        _boundingBoxes = new Aabb[_width * _height];

        _world = new Matrix4f().setTranslation(new Vector3f(0));
        _world.scale(_scale);
    }

    public World(String world) {
        try {
            BufferedImage tileSheet = ImageIO.read(new File("./res/levels/" + world + "/tiles.png"));
            //BufferedImage entitySheet = ImageIO.read(new File("./levels/" + world + "/entities.png"));

            _width = tileSheet.getWidth();
            _height = tileSheet.getHeight();
            _scale = 16;

            _world = new Matrix4f().setTranslation(new Vector3f(0));
            _world.scale(_scale);

            int[] colorTileSheet = tileSheet.getRGB(0, 0, _width, _height, null, 0, tileSheet.getWidth());

            _tiles = new byte[_width * _height];
            _boundingBoxes = new Aabb[_width * _height];

            for (int y = 0; y < _height; y++) {
                for (int x = 0; x < _width; x++) {
                    int red = (colorTileSheet[x + (y * _width)] >> 16) & 0xFF;

                    Tile t;
                    try {
                        t = Tile.TILES[red];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        t = null;
                    }
                    if (t != null) {
                        setTile(t, x, y);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void render(TileRenderer tileRenderer, Shader shader, Camera camera, Window window) {
        int positionX = ((int) camera.getPosition().x + (window.getWidth()/2)) / (_scale * 2);
        int positionY = ((int) camera.getPosition().y - (window.getHeight()/2)) / (_scale * 2);

        for (int i = 0; i < VIEW_X; i++) {
            for (int j = 0; j < VIEW_Y; j++) {
                Tile t = getTile(i - positionX, j + positionY);
                if (t != null) {
                    tileRenderer.renderTile(t, i - positionX, -j - positionY, shader, _world, camera);
                }
            }
        }
    }

    public void correctCamera(Camera camera, Window window) {
        Vector3f position = camera.getPosition();

        int wPixels = -_width * _scale * 2;
        int hPixels = _height * _scale * 2;

        if (position.x > -(window.getWidth()/2f) + _scale) {
            position.x = -(window.getWidth()/2f) + _scale;
        }
        if (position.x < wPixels + (window.getWidth()/2f) + _scale) {
            position.x = wPixels + (window.getWidth()/2f) + _scale;
        }

        if (position.y < (window.getHeight()/2f) - _scale) {
            position.y = (window.getHeight()/2f) - _scale;
        }
        if (position.y > hPixels - (window.getHeight()/2f) - _scale) {
            position.y = hPixels - (window.getHeight()/2f) - _scale;
        }
    }

    public int getScale() {
        return _scale;
    }

    public void setTile(Tile tile, int x, int y) {
        if (x < 0 || x >= _width) {
            throw new IllegalArgumentException("World.setTile: x out of bounds, " + x);
        }
        if (y < 0 || y >= _height) {
            throw new IllegalArgumentException("World.setTile: y out of bounds, " + y);
        }

        _tiles[x + (y * _height)] = tile.getId();

        if (tile.isSolid()) {
            _boundingBoxes[x + (y * _width)] = new Aabb(new Vector2f(x * 2, -y * 2), new Vector2f(1,1));
        } else {
            _boundingBoxes[x + (y * _width)] = null;
        }
    }

    public Tile getTile(int x, int y) {
        try {
            return Tile.TILES[_tiles[x + (y * _width)]];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public Aabb getTileBoundingBox(int x, int y) {
        try {
            return _boundingBoxes[x + (y * _width)];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
}
