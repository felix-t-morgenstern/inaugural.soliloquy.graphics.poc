package render;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Texture {
    private int _id;
    private int _width;
    private int _height;

    public Texture(String filename) {
        BufferedImage bi;
        try {
            bi = ImageIO.read(new File("./res/textures/" + filename));

            _width = bi.getWidth();
            _height = bi.getHeight();

            int[] pixels_raw = new int[_width * _height * 4];
            pixels_raw = bi.getRGB(0, 0, _width, _height, null, 0, _width);

            ByteBuffer pixels = BufferUtils.createByteBuffer(_width * _height * 4);

            for (int i = 0; i < _height; i++) {
                for (int j = 0; j < _width; j++) {
                    int pixel = pixels_raw[i* _width + j];
                    pixels.put((byte)((pixel >> 16) & 0xFF)); // r
                    pixels.put((byte)((pixel >> 8) & 0xFF)); // g
                    pixels.put((byte)(pixel & 0xFF)); // b
                    pixels.put((byte)((pixel >> 24) & 0xFF)); // a
                }
            }

            pixels.flip();

            _id = glGenTextures();

            glBindTexture(GL_TEXTURE_2D, _id);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, _width, _height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    protected void finalize() throws Throwable {
        glDeleteTextures(_id);
        super.finalize();
    }

    public void bind(int sampler) {
        if (sampler < 0 || sampler > 31) {
            throw new IllegalArgumentException("sampler must be between 0 and 31");
        }
        glActiveTexture(GL_TEXTURE0 + sampler);
        glBindTexture(GL_TEXTURE_2D, _id);
    }
}
