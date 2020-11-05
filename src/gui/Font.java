package gui;

import org.joml.Vector2f;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class Font {
    private int _id;
    private BufferedImage _bufferedImage;
    private Vector2f _imageSize;
    private java.awt.Font _font;
    private FontMetrics _fontMetrics;

    private int _i;

    private Map<Character,FontGlyph> _glyphs = new HashMap<>();

    private final static int IMAGE_SIZE_X = 1024;
    private final static int IMAGE_SIZE_Y = 1024;

    private final static int ADDITIONAL_CHARACTER_PADDING = 8;

    private final static int RGBA_BYTES = 4;
    private final static int RED_OFFSET = 16;
    private final static int GREEN_OFFSET = 8;
    private final static int BLUE_OFFSET = 0;
    private final static int ALPHA_OFFSET = 24;

    public Font(String filename, float size) {
        try {
            _font = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT,
                    new File("./res/Font/" + filename + ".ttf"));
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }

        generateFont();
    }

    public Font(java.awt.Font font) {
        _font = font;

        generateFont();
    }

    private void generateFont() {
        GraphicsConfiguration graphicsConfiguration =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                    .getDefaultConfiguration();

        Graphics2D graphics2D = graphicsConfiguration.createCompatibleImage(1, 1,
                Transparency.TRANSLUCENT).createGraphics();

        graphics2D.setFont(_font);

        _fontMetrics = graphics2D.getFontMetrics();

        _imageSize = new Vector2f(IMAGE_SIZE_X, IMAGE_SIZE_Y);

        _bufferedImage = graphics2D.getDeviceConfiguration()
                .createCompatibleImage((int)_imageSize.x(), (int)_imageSize.y(),
                        Transparency.TRANSLUCENT);

        _id = glGenTextures();

        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, _id);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, (int)_imageSize.x(), (int)_imageSize.y(), 0,
                GL_RGBA, GL_UNSIGNED_BYTE, generateImage());
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    }

    private ByteBuffer generateImage() {
        Graphics2D graphics2D = (Graphics2D)_bufferedImage.getGraphics();
        graphics2D.setFont(_font);
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawCharacters(graphics2D);

        return createBuffer();
    }

    private void drawCharacters(Graphics2D graphics2D) {
        int tempX = 0;
        int tempY = 0;
        float height = (float)(_fontMetrics.getMaxAscent() + _fontMetrics.getMaxDescent());

        for (_i = 32; _i < 256; _i++) {
            if (_i == 127) {
                continue;
            }

            char c = (char) _i;

            float charWidth = _fontMetrics.charWidth(c);

            // TODO: Consider customizing the padding as a constructor parameter
            float advance = charWidth + ADDITIONAL_CHARACTER_PADDING;

            if (tempX + advance > _imageSize.x()) {
                tempX = 0;
                tempY++;
            }

            _glyphs.put(c, new FontGlyph(tempX / _imageSize.x(),
                                         (tempY * height) / _imageSize.y(),
                                         charWidth / _imageSize.x(),
                                         height / _imageSize.y(),
                                         charWidth,
                                         height));

            graphics2D.drawString(String.valueOf(c), tempX,
                    _fontMetrics.getMaxAscent() + (height * tempY));

            tempX += advance;
        }
    }

    private ByteBuffer createBuffer() {
        int width = (int)_imageSize.x();
        int height = (int)_imageSize.y();
        int[] pixels = new int[width * height];

        _bufferedImage.getRGB(0, 0, width, height, pixels, 0, width);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(width * height * RGBA_BYTES);

        for(_i = 0; _i < pixels.length; _i++) {
            byteBuffer.put((byte) ((pixels[_i] >> RED_OFFSET) & 0xFF));
            byteBuffer.put((byte) ((pixels[_i] >> GREEN_OFFSET) & 0xFF));
            byteBuffer.put((byte) ((pixels[_i] >> BLUE_OFFSET) & 0xFF));
            byteBuffer.put((byte) ((pixels[_i] >> ALPHA_OFFSET) & 0xFF));
        }

        byteBuffer.flip();

        return byteBuffer;
    }

    public int id() {
        return _id;
    }

    public Map<Character,FontGlyph> glyphs() {
        return Collections.unmodifiableMap(_glyphs);
    }

    public int stringWidth(String s) {
        // TODO: Verify whether the math here is actually correct
        return _fontMetrics.stringWidth(s);
    }
}
