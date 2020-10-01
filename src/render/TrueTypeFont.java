package render;

import org.joml.Vector2f;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class TrueTypeFont {
    private int _fontId;
    private BufferedImage _bufferedImage;
    private Vector2f _imageSize;
    private final Font _font;
    private FontMetrics _fontMetrics;
    private final Map<Character,TrueTypeFontGlyph> _glyphs;
    private final Model _model;

    private final static Map<String,TrueTypeFont> FONTS = new HashMap<>();

    private final float IMAGE_SIZE_X = 1024;
    private final float IMAGE_SIZE_Y = 1024;
    private final float CHAR_WIDTH_PADDING = 5;
    private final int RGBA_BYTE_SIZE = 4;

    public TrueTypeFont(String fontFilename, float size, Model model) {
        _model = model;

        try {
            _font = Font.createFont(Font.TRUETYPE_FONT, new File(fontFilename))
                .deriveFont(size);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }

        _glyphs = new HashMap<>();

        generateFont();
    }

    public TrueTypeFont(Font font, Model model) {
        _model = model;

        _font = font;

        _glyphs = new HashMap<>();

        generateFont();
    }

    private void generateFont() {
        GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();

        Graphics2D graphics2D = graphicsConfiguration
                .createCompatibleImage(1, 1, Transparency.TRANSLUCENT)
                .createGraphics();

        graphics2D.setFont(_font);

        _fontMetrics = graphics2D.getFontMetrics();

        _imageSize = new Vector2f(IMAGE_SIZE_X, IMAGE_SIZE_Y);

        _bufferedImage = graphics2D
                .getDeviceConfiguration()
                .createCompatibleImage((int) _imageSize.x, (int) _imageSize.y, Transparency.TRANSLUCENT);

        _fontId = glGenTextures();

        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, _fontId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA,
                (int) _imageSize.x, (int) _imageSize.y, 0, GL_RGBA, GL_UNSIGNED_BYTE, generateImage());

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    }

    private ByteBuffer generateImage() {
        Graphics2D graphics2D = (Graphics2D) _bufferedImage.getGraphics();
        graphics2D.setFont(_font);
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawCharacters(graphics2D);
        return createBuffer();
    }

    private void drawCharacters(Graphics2D graphics2D) {
        int tempX = 0;
        int tempY = 0;

        float height = (float) (_fontMetrics.getMaxAscent() + _fontMetrics.getMaxDescent());

        for (int i = 32; i < 256; i++) {
            if (i == 127) {
                continue;
            }

            char c = (char) i;
            float charWidth = _fontMetrics.charWidth(c);

            float advance = charWidth + CHAR_WIDTH_PADDING;

            if (tempX + advance > _imageSize.x) {
                tempX = 0;
                tempY++;
            }

            _glyphs.put(c, new TrueTypeFontGlyph(tempX / _imageSize.x,
                                                 (tempY * height) / _imageSize.y,
                                                 charWidth / _imageSize.x,
                                                 height / _imageSize.y,
                                                 charWidth,
                                                 height));
            graphics2D.drawString(String.valueOf(c), tempX,
                    _fontMetrics.getMaxAscent() + (height * tempY));

            tempX += advance;
        }
    }

    private ByteBuffer createBuffer() {
        int width = (int) _imageSize.x;
        int height = (int) _imageSize.y;
        int[] pixels = new int[width * height];

        _bufferedImage.getRGB(0, 0, width, height, pixels, 0, width);

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(width * height * RGBA_BYTE_SIZE);

        for (int pixel : pixels) {
            byteBuffer.put((byte) ((pixel >> 16) * 0xFF)); // Red
            byteBuffer.put((byte) ((pixel >> 8) * 0xFF)); // Green
            byteBuffer.put((byte) (pixel * 0xFF)); // Blue
            byteBuffer.put((byte) ((pixel >> 24) * 0xFF)); // Alpha
        }

        byteBuffer.flip();

        return byteBuffer;
    }

    public int id() {
        return _fontId;
    }

    // NB: Consider using deep cloning for this method
    public Map<Character, TrueTypeFontGlyph> getCharacters() {
        return _glyphs;
    }

    // NB: Consider including padding in the final width for this
    public int stringWidth(String s) {
        return _fontMetrics.stringWidth(s);
    }

    public void renderLine(String text, Shader shader, Color textColor, float x, float y) {
        glBindTexture(GL_TEXTURE_2D, _fontId);

        float tempX = x;

        char[] characters = text.toCharArray();

        shader.bind();

        shader.setUniform("matColor", textColor);
        for (char character : characters) {
            TrueTypeFont.TrueTypeFontGlyph glyph = _glyphs.get(character);
            shader.setUniform("screenPos", tempX, y);
            shader.setUniform("offset", glyph._x, glyph._y, glyph._width, glyph._height);
            shader.setUniform("pixelScale", glyph._scaleX, glyph._scaleY);

            _model.render();

            tempX += glyph._scaleX;
        }
    }

    public class TrueTypeFontGlyph {
        public float _x; // uv coordinates
        public float _y;
        public float _width;
        public float _height;
        public float _scaleX; // size in pixels
        public float _scaleY;

        public TrueTypeFontGlyph(float x,
                                 float y,
                                 float width,
                                 float height,
                                 float scaleX,
                                 float scaleY) {
            _x = x;
            _y = y;
            _width = width;
            _height = height;
            _scaleX = scaleX;
            _scaleY = scaleY;
        }
    }

    public static Collection<TrueTypeFont> Fonts() {
        return FONTS.values();
    }

    public static TrueTypeFont Find(String fontFilename)
    {
        return FONTS.getOrDefault(fontFilename, null);
    }
}
