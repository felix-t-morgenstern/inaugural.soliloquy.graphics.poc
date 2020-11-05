package gui;

import engine.*;
import input.Mouse;
import math.Color;
import math.Quaternion;

import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Gui {
    public static Color BACKGROUND_COLOR = Color.white();
    public static Color TEXT_COLOR = Color.black();

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

    private static Rectangle UV_COORDS = new Rectangle(0, 0, 1, 1);

    public static void init() {
        float[] meshData = new float[] {0f, 1f, 1f, 1f, 1f, 0f, 1f, 0f, 0f, 0f, 0f, 1f};
        _mesh = new Mesh(meshData, meshData);
        _shader = new Shader("defaultShader");

        _guiSkin = new GuiSkin("defaultGui");

        _font = new Font(new java.awt.Font("Times New Roman", java.awt.Font.PLAIN, 16));
    }

    public static void prepare() {
        glDisable(GL_DEPTH_TEST);

        _shader.bind();
        _shader.setUniform("matColor", BACKGROUND_COLOR);

        _orthographic = Quaternion.orthographic(0, Application.width(),
                Application.height(), 0, -1, 1);
        _shader.setUniform("projection", _orthographic);
        _mesh.bind();
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
                label(text, container._x, container._y);
            }
            else {
                label(text, buttonDimensions._x, buttonDimensions._y);
            }

            return Mouse.buttonIsPressed(GLFW_MOUSE_BUTTON_LEFT);
        }
        else {
            // TODO: Eliminate redundancies here
            Rectangle container = box(buttonDimensions, normalStyle);
            if (container != null) {
                label(text, container._x, container._y);
            }
            else {
                label(text, buttonDimensions._x, buttonDimensions._y);
            }

            return Mouse.buttonIsPressed(GLFW_MOUSE_BUTTON_LEFT);
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

        float rightMinusRightPadding = boxDimensions._x + boxDimensions._width
                - style.padding().right();
        float rightMinusRightPaddingUv = style.offsetUv().right() - style.paddingUv().right();

        float leftPlusLeftPadding = boxDimensions._x + style.padding().left();
        float leftPlusLeftPaddingUv = style.offsetUv().left() + style.paddingUv().left();

        float topPlusTopPadding = boxDimensions._y + style.padding().top();
        float topPlusTopPaddingUv = style.offsetUv().top() + style.paddingUv().top();

        float bottomMinusBottomPadding = boxDimensions._y + boxDimensions._height
                - style.padding().bottom();
        float bottomMinusBottomPaddingUv = style.offsetUv().bottom() - style.paddingUv().bottom();

        float sidebarHeight = boxDimensions._height
                - style.padding().top() - style.padding().bottom();
        float sidebarHeightUv = (style.offsetUv().bottom() - style.paddingUv().bottom())
                - (style.offsetUv().top() + style.paddingUv().top());

        float sidebarY = boxDimensions._y + style.padding().top();
        float sidebarYUv = style.offsetUv().top() + style.paddingUv().top();

        float horizontalBarWidth = boxDimensions._width
                - style.padding().left() - style.padding().right();
        float horizontalBarWidthUv = (style.offsetUv().right() - style.paddingUv().right())
                - (style.offsetUv().left() + style.paddingUv().left());

        Rectangle topLeft = new Rectangle(
                boxDimensions._x,
                boxDimensions._y,
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
                boxDimensions._y,
                style.padding().right(),
                style.padding().top());
        Rectangle topRightUv = new Rectangle(
                rightMinusRightPaddingUv,
                style.offsetUv().top(),
                style.paddingUv().right(),
                style.paddingUv().top());
        drawTextureWithTexCoords(texture, topRight, topRightUv);

        Rectangle bottomLeft = new Rectangle(
                boxDimensions._x,
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
                boxDimensions._x,
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
                boxDimensions._y,
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

    public static void label(String text, float x, float y) {
        glBindTexture(GL_TEXTURE_2D, _font.id());
        Map<Character,FontGlyph> glyphs = _font.glyphs();
        _xTemp = x;

        _characters = text.toCharArray();

        _shader.setUniform("matColor", TEXT_COLOR);
        _boundColor = TEXT_COLOR;

        for(_iterator = 0; _iterator < _characters.length; _iterator++) {
            FontGlyph glyph = glyphs.get(_characters[_iterator]);

            _shader.setUniform("screenPosition", _xTemp, y);
            _shader.setUniform("offset", glyph.getX(), glyph.getY(), glyph.getWidth(),
                    glyph.getHeight());
            _shader.setUniform("pixelScale", glyph.getScaleX(), glyph.getScaleY());

            _mesh.render();

            _xTemp += glyph.getScaleX();
        }
    }

    public static void drawTexture(Texture texture, Rectangle rectangle) {
        drawTextureWithTexCoords(texture, rectangle, UV_COORDS);
    }

    public static void drawTextureWithTexCoords(Texture texture, Rectangle rectangle,
                                                Rectangle uvRectangle) {
        drawTextureWithTexCoords(texture, rectangle, uvRectangle, BACKGROUND_COLOR);
    }

    public static void drawTextureWithTexCoords(Texture texture, Rectangle rectangle,
                                                Rectangle uvRectangle, Color color) {
        if (_boundTextureId != texture.id()) {
            glBindTexture(GL_TEXTURE_2D, texture.id());
        }

        _shader.setUniform("offset", uvRectangle._x, uvRectangle._y,
                uvRectangle._width, uvRectangle._height);
        _shader.setUniform("pixelScale", rectangle._width, rectangle._height);
        _shader.setUniform("screenPosition", rectangle._x, rectangle._y);
        if (!_boundColor.equals(color)) {
            _shader.setUniform("matColor", color);
            _boundColor = color;
        }

        _mesh.render();
    }

    public static void unbind() {
        _mesh.unbind();
        _shader.unbind();
    }
}
