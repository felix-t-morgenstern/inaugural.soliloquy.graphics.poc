package world;

public class Tile {
    public static Tile TILES[] = new Tile[255];
    public static byte NUMBER_OF_TILES = 0;

    public static final Tile TEST_TILE = new Tile("grass");
    public static final Tile TEST_TILE_2 = new Tile("checker").setSolid();

    private byte _id;
    private boolean _solid;
    private String _texture;

    public Tile(String texture) {
        _id = NUMBER_OF_TILES++;
        _texture = texture;
        _solid = false;
        if (TILES[_id] != null) {
            throw new IllegalStateException("Tiles at " + _id + " is already being used");
        }
        TILES[_id] = this;
    }

    public byte getId() {
        return _id;
    }

    public String getTexture() {
        return _texture;
    }

    public Tile setSolid() {
        _solid = true;
        return this;
    }

    public boolean isSolid() {
        return _solid;
    }
}
