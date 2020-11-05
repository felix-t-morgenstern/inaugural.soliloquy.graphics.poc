package engine;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

public class Texture {
    private int _id;
    private String _name;
    private int _width;
    private int _height;

    private int _index;

    private static final Map<String,Texture> TEXTURES = new HashMap<>();
    private static Texture TEMP = null;

    private final static int DESIRED_CHANNELS = 4;
    private final static int LEVEL_OF_DETAIL = 0;
    private final static int BORDER = 0;
    private final static int NO_TEXTURE = 0;

    public Texture (String filename) {
        if (TEXTURES.containsKey(filename)) {
            return;
        }

        _name = filename;

        IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer channelsBuffer = BufferUtils.createIntBuffer(1);

        ByteBuffer imageData = stbi_load("./res/textures/" + filename, widthBuffer, heightBuffer,
                channelsBuffer, DESIRED_CHANNELS);
        assert imageData != null;

        _id = glGenTextures();

        _width = widthBuffer.get();
        _height = heightBuffer.get();

        glBindTexture(GL_TEXTURE_2D, _id);

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, LEVEL_OF_DETAIL, GL_RGBA, _width, _height, BORDER, GL_RGBA,
                GL_UNSIGNED_BYTE, imageData);

        stbi_image_free(imageData);

        TEXTURES.put(filename, this);
    }

    public int id() {
        return _id;
    }

    public int width() {
        return _width;
    }

    public int height() {
        return _height;
    }

    public void bind() {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, _id);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, NO_TEXTURE);
    }

    public static Texture find(String name) {
        return TEXTURES.get(name);
    }

    public static void cleanUp() {
        TEXTURES.values().forEach(t -> glDeleteTextures(t.id()));
    }

    public static Map<String,Texture> Textures() {
        return new HashMap<>(TEXTURES);
    }
}
