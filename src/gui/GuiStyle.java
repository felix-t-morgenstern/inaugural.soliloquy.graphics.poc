package gui;

import engine.Rectangle;
import engine.Texture;

public class GuiStyle {
    private String _name;
    private String _type;
    private Texture _texture;
    private GuiSkinCoords _offset;
    private GuiSkinCoords _padding;
    private GuiSkinCoords _offsetUv;
    private GuiSkinCoords _paddingUv;

    public GuiStyle(String name, Texture texture, GuiSkinCoords offset, GuiSkinCoords padding) {
        _name = name;
        _texture = texture;
        _offset = offset;
        _padding = padding;

        float textureWidth = texture.width();
        float textureHeight = texture.height();

        _offsetUv = new GuiSkinCoords(offset.top() / textureHeight, offset.right() / textureWidth,
                offset.bottom() / textureHeight, offset.left() / textureWidth);

        _paddingUv = new GuiSkinCoords(padding.top() / textureHeight,
                padding.right() / textureWidth, padding.bottom() / textureHeight,
                padding.left() / textureWidth);
    }

    public GuiSkinCoords offset() {
        return _offset;
    }

    public GuiSkinCoords padding() {
        return _padding;
    }

    public GuiSkinCoords offsetUv() {
        return _offsetUv;
    }

    public GuiSkinCoords paddingUv() {
        return _paddingUv;
    }

    public Texture texture() {
        return _texture;
    }
}
