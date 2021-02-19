package gui;

import engine.*;
import input.Mouse;
import math.Color;
import math.Quaternion;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Gui {
    public static Color BLEND_COLOR = Color.white();
    public static Color TEXT_COLOR = Color.white();

    private static Mesh _mesh;
    private static Shader _shader;
    private static Quaternion _orthographic;

    private static int _iterator;
    private static char[] _characters;
    private static float _xTemp;
    private static GuiSkin _guiSkin;
    private static Font _font;
    private static int _boundTextureId = -1;
    private static Color _boundColor = Color.white();

    // TODO: Consider optimization here
    private static Stack<Rectangle> _drawingAreasStack = new Stack<>();

    private static Rectangle UV_COORDS = new Rectangle(0, 0, 1, 1);

    public static void init() {
        float[] meshData = new float[] {0f, 1f, 1f, 1f, 1f, 0f, 1f, 0f, 0f, 0f, 0f, 1f};
        _mesh = new Mesh(meshData, meshData);
        _shader = new Shader("defaultShader");

        _guiSkin = new GuiSkin("defaultGui");

        _font = new Font("Trajan Pro Regular", 16);
    }

    public static void prepare() {
        _drawingAreasStack.clear();
        _drawingAreasStack.add(Application.dimensions());

        //glDisable(GL_DEPTH_TEST);

        _shader.bind();
        _shader.setUniform("matColor", BLEND_COLOR);

        _orthographic = Quaternion.orthographic(0, Application.width(),
                Application.height(), 0, -1, 1);
        _shader.setUniform("projection", _orthographic);
        _mesh.bind();
    }

    public static void drawRectangle(Rectangle rectangle) {
        glBegin(GL_QUADS);

        glVertex2f(rectangle.x, rectangle.y);
        glVertex2f(rectangle.x + rectangle.width, rectangle.y);
        glVertex2f(rectangle.x + rectangle.width, rectangle.y + rectangle.height);
        glVertex2f(rectangle.x, rectangle.y + rectangle.height);

        glEnd();
    }

    public static void line(Color color, Vector3f origin, Vector3f destination) {
        glColor4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

        glBegin(GL_TRIANGLES);

        glVertex2f(origin.x, origin.y);
        glVertex2f(destination.x, destination.y);
        glVertex2f(origin.x, origin.y);

        glEnd();
    }

    public static boolean button(String text, Rectangle buttonDimensions, String guiSkinName,
                                 String normalStyle, String hoverStyle) {
        GuiSkin skin = GuiSkin.getSkin(guiSkinName);
        return button(text, buttonDimensions, skin.getStyle(normalStyle),
                skin.getStyle(hoverStyle));
    }

    public static boolean button(String text, Rectangle buttonDimensions, GuiStyle normalStyle,
                                 GuiStyle hoverStyle) {
        if (buttonDimensions.contains(Mouse.position())) {
            Rectangle container = box(buttonDimensions, hoverStyle);
            if (container != null) {
                label(text, container.x, container.y);
            }
            else {
                label(text, buttonDimensions.x, buttonDimensions.y);
            }

            return Mouse.buttonIsPressed(GLFW_MOUSE_BUTTON_LEFT);
        }
        else {
            // TODO: Eliminate redundancies here
            Rectangle container = box(buttonDimensions, normalStyle);
            if (container != null) {
                label(text, container.x, container.y);
            }
            else {
                label(text, buttonDimensions.x, buttonDimensions.y);
            }

            return false;
        }
    }

    public static Rectangle box(Rectangle boxDimensions, String guiSkinName) {
        return box(boxDimensions, GuiSkin.getSkin(guiSkinName).getStyle("box"));
    }

    // TODO: Consider renaming function
    // TODO: Consider returning different class; current return is size of inside
    // TODO: Consider refactoring this into having different textures for corners, borders, etc.
    public static Rectangle box(Rectangle boxDimensions, GuiStyle style) {
        // TODO: Consider whether this is, in fact, appropriate (and probably reconsider whether the box should be *returned*, and how it is calculated)
        if (style == null) {
            return null;
        }

        Texture texture = style.texture();

        float rightMinusRightPadding = boxDimensions.x + boxDimensions.width
                - style.padding().right();
        float rightMinusRightPaddingUv = style.offsetUv().right() - style.paddingUv().right();

        float leftPlusLeftPadding = boxDimensions.x + style.padding().left();
        float leftPlusLeftPaddingUv = style.offsetUv().left() + style.paddingUv().left();

        float topPlusTopPadding = boxDimensions.y + style.padding().top();
        float topPlusTopPaddingUv = style.offsetUv().top() + style.paddingUv().top();

        float bottomMinusBottomPadding = boxDimensions.y + boxDimensions.height
                - style.padding().bottom();
        float bottomMinusBottomPaddingUv = style.offsetUv().bottom() - style.paddingUv().bottom();

        float sidebarHeight = boxDimensions.height
                - style.padding().top() - style.padding().bottom();
        float sidebarHeightUv = (style.offsetUv().bottom() - style.paddingUv().bottom())
                - (style.offsetUv().top() + style.paddingUv().top());

        float sidebarY = boxDimensions.y + style.padding().top();
        float sidebarYUv = style.offsetUv().top() + style.paddingUv().top();

        float horizontalBarWidth = boxDimensions.width
                - style.padding().left() - style.padding().right();
        float horizontalBarWidthUv = (style.offsetUv().right() - style.paddingUv().right())
                - (style.offsetUv().left() + style.paddingUv().left());

        Rectangle topLeft = new Rectangle(
                boxDimensions.x,
                boxDimensions.y,
                style.padding().left(),
                style.padding().top());
        Rectangle topLeftUv = new Rectangle(
                style.offsetUv().left(),
                style.offsetUv().top(),
                style.paddingUv().left(),
                style.paddingUv().top());
        drawTextureWithTexCoords(texture, topLeft, topLeftUv);

        Rectangle topRight = new Rectangle(
                rightMinusRightPadding,
                boxDimensions.y,
                style.padding().right(),
                style.padding().top());
        Rectangle topRightUv = new Rectangle(
                rightMinusRightPaddingUv,
                style.offsetUv().top(),
                style.paddingUv().right(),
                style.paddingUv().top());
        drawTextureWithTexCoords(texture, topRight, topRightUv);

        Rectangle bottomLeft = new Rectangle(
                boxDimensions.x,
                bottomMinusBottomPadding,
                style.padding().left(),
                style.padding().bottom());
        Rectangle bottomLeftUv = new Rectangle(
                style.offsetUv().left(),
                bottomMinusBottomPaddingUv,
                style.paddingUv().left(),
                style.paddingUv().bottom());
        drawTextureWithTexCoords(texture, bottomLeft, bottomLeftUv);

        Rectangle bottomRight = new Rectangle(
                rightMinusRightPadding,
                bottomMinusBottomPadding,
                style.padding().right(),
                style.padding().bottom());
        Rectangle bottomRightUv = new Rectangle(
                rightMinusRightPaddingUv,
                bottomMinusBottomPaddingUv,
                style.paddingUv().right(),
                style.paddingUv().bottom());
        drawTextureWithTexCoords(texture, bottomRight, bottomRightUv);

        Rectangle leftSidebar = new Rectangle(
                boxDimensions.x,
                sidebarY,
                style.padding().left(),
                sidebarHeight);
        Rectangle leftSidebarUv = new Rectangle(
                style.offsetUv().left(),
                sidebarYUv,
                style.paddingUv().left(),
                sidebarHeightUv);
        drawTextureWithTexCoords(texture, leftSidebar, leftSidebarUv);

        Rectangle rightSidebar = new Rectangle(
                rightMinusRightPadding,
                sidebarY,
                style.padding().right(),
                sidebarHeight);
        Rectangle rightSidebarUv = new Rectangle(
                rightMinusRightPaddingUv,
                sidebarYUv,
                style.paddingUv().right(),
                sidebarHeightUv);
        drawTextureWithTexCoords(texture, rightSidebar, rightSidebarUv);

        Rectangle topHorizontalBar = new Rectangle(
                leftPlusLeftPadding,
                boxDimensions.y,
                horizontalBarWidth,
                style.padding().top());
        Rectangle topHorizontalBarUv = new Rectangle(
                leftPlusLeftPaddingUv,
                style.offsetUv().top(),
                horizontalBarWidthUv,
                style.paddingUv().top());
        drawTextureWithTexCoords(texture, topHorizontalBar, topHorizontalBarUv);

        Rectangle bottomHorizontalBar = new Rectangle(
                leftPlusLeftPadding,
                bottomMinusBottomPadding,
                horizontalBarWidth,
                style.padding().bottom());
        Rectangle bottomHorizontalBarUv = new Rectangle(
                leftPlusLeftPaddingUv,
                bottomMinusBottomPaddingUv,
                horizontalBarWidthUv,
                style.paddingUv().bottom());
        drawTextureWithTexCoords(texture, bottomHorizontalBar, bottomHorizontalBarUv);

        Rectangle center = new Rectangle(
                leftPlusLeftPadding,
                topPlusTopPadding,
                horizontalBarWidth,
                sidebarHeight);
        Rectangle centerUv = new Rectangle(
                leftPlusLeftPaddingUv,
                topPlusTopPaddingUv,
                horizontalBarWidthUv,
                sidebarHeightUv);
        drawTextureWithTexCoords(texture, center, centerUv);

        return center;
    }

    public static int label(String text, float x, float y) {
        return label(text, x, y, 0);
    }

    public static int label(String text, float x, float y, float z) {
        Map<Character,FontGlyph> glyphs = _font.glyphs();
        _xTemp = x;

        _characters = text.toCharArray();

        for(_iterator = 0; _iterator < _characters.length; _iterator++) {
            FontGlyph glyph = glyphs.get(_characters[_iterator]);

            drawTextureWithTexCoords(_font.texture(),
                    new Rectangle(_xTemp, y, glyph.getScaleX(), glyph.getScaleY()),
                    new Rectangle(glyph.getX(), glyph.getY(), glyph.getWidth(), glyph.getHeight()),
                    z,
                    TEXT_COLOR);

            _xTemp += glyph.getScaleX();
        }

        return _font.lineHeight();
    }

    public static void drawTexture(Texture texture, Rectangle rectangle) {
        drawTexture(texture, rectangle, 0f);
    }

    public static void drawTexture(Texture texture, Rectangle rectangle, float z) {
        drawTextureWithTexCoords(texture, rectangle, z, UV_COORDS);
    }

    public static void drawTextureWithTexCoords(Texture texture, Rectangle rectangle,
                                                Rectangle uvRectangle) {
        drawTextureWithTexCoords(texture, rectangle, uvRectangle, 0f, BLEND_COLOR);
    }

    public static void drawTextureWithTexCoords(Texture texture, Rectangle rectangle, float z,
                                                Rectangle uvRectangle) {
        drawTextureWithTexCoords(texture, rectangle, uvRectangle, z, BLEND_COLOR);
    }

    public static void drawTextureWithTexCoords(Texture texture, Rectangle drawDimensions,
                                                Rectangle uvRectangle, float zIndex, Color color) {
        Rectangle currentDrawingArea = _drawingAreasStack.peek();

        if (currentDrawingArea == null) {
            return;
        }

        // TODO: Add Rectangle.addPosition(x,y) and use here; look for other uses
        Rectangle intersectingArea = currentDrawingArea.getIntersection(new Rectangle(
                drawDimensions.x + currentDrawingArea.x,
                drawDimensions.y + currentDrawingArea.y,
                drawDimensions.width, drawDimensions.height
        ));

        if (intersectingArea == null) {
            return;
        }

        float xUv = uvRectangle.x + ((((intersectingArea.x - drawDimensions.x) - currentDrawingArea.x) / drawDimensions.width) * uvRectangle.width);
        float yUv = uvRectangle.y + ((((intersectingArea.y - drawDimensions.y) - currentDrawingArea.y) / drawDimensions.height) * uvRectangle.height);
        Rectangle intersectingAreaUv = new Rectangle(xUv, yUv,
                (intersectingArea.width / drawDimensions.width) * uvRectangle.width,
                (intersectingArea.height / drawDimensions.height) * uvRectangle.height);

        if (texture.id() != _boundTextureId) {
            glBindTexture(GL_TEXTURE_2D, texture.id());
        }

        _shader.setUniform("zIndex", zIndex);
        _shader.setUniform("offset", intersectingAreaUv.x, intersectingAreaUv.y,
                intersectingAreaUv.width, intersectingAreaUv.height);
        _shader.setUniform("pixelScale", intersectingArea.width, intersectingArea.height);

        // draw within the set drawing area
        _shader.setUniform("screenPosition", intersectingArea.x, intersectingArea.y);

        if (!_boundColor.equals(color)) {
            _shader.setUniform("matColor", color);
            _boundColor = color;
        }

        _mesh.render();
    }

    public static void window(Rectangle dimensions, String title, Consumer<Integer> hook,
                              String skin, String style) {
        window(dimensions, title, hook, GuiSkin.getSkin(skin).getStyle(style));
    }

    public static void window(Rectangle dimensions, String title,
                              Consumer<Integer> windowIdConsumer, GuiStyle style) {
        if (style != null) {
            Rectangle innerDimensions = box(dimensions, style);
            // TODO: Ensure that when labels wrap, that label height comes back accordingly
            int labelHeight = label(title, innerDimensions.x, innerDimensions.y);
            beginArea(new Rectangle(innerDimensions.x, innerDimensions.y + labelHeight,
                    innerDimensions.width, innerDimensions.height - labelHeight));
        }
        else {
            label(title, dimensions.x, dimensions.y);
            beginArea(dimensions);
        }

        // passes through window Id
        windowIdConsumer.accept(0);

        endArea();
    }

    public static void beginArea(Rectangle areaDimensions) {
        Rectangle currentDrawingArea = _drawingAreasStack.peek();
        _drawingAreasStack.add(currentDrawingArea.getIntersection(new Rectangle(
                currentDrawingArea.x + areaDimensions.x,
                currentDrawingArea.y + areaDimensions.y,
                areaDimensions.width,
                areaDimensions.height
        )));
    }

    public static void endArea() {
        if (_drawingAreasStack.size() > 1) {
            _drawingAreasStack.pop();
        }
    }

    public static void unbind() {
        _mesh.unbind();
        _shader.unbind();
    }
}
