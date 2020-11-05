package gui;

import com.google.gson.Gson;
import engine.Texture;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GuiSkin {
    private GuiStyle _guiStyle;

    private final static Map<String,GuiSkin> SKINS = new HashMap<>();

    private final Map<String,GuiStyle> SKIN_STYLES = new HashMap<>();

    public GuiSkin(String name) {
        BufferedReader bufferedReader;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            // TODO: Duplicate code in Shader
            bufferedReader = new BufferedReader(new FileReader(new File("./res/skins/" + name + ".skin")));

            String line;
            while((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            bufferedReader.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        GuiSkinDTO[] dtos = new Gson().fromJson(stringBuilder.toString(), GuiSkinDTO[].class);

        for (GuiSkinDTO dto : dtos) {
            GuiSkinCoords offset = new GuiSkinCoords(dto.top, dto.right, dto.bottom, dto.left);
            GuiSkinCoords padding = new GuiSkinCoords(dto.paddingTop, dto.paddingRight,
                    dto.paddingBottom, dto.paddingLeft);
            _guiStyle = new GuiStyle(dto.name, Texture.find(dto.image), offset, padding);
            SKIN_STYLES.put(dto.name, _guiStyle);
        }

        SKINS.put(name, this);
    }

    public GuiStyle style() {
        return _guiStyle;
    }

    public static GuiSkin getSkin(String skinName) {
        return SKINS.get(skinName);
    }

    public GuiStyle getStyle(String styleName) {
        return SKIN_STYLES.get(styleName);
    }

    public class GuiSkinDTO {
        public String image;
        public String name;
        public String type;
        public float left;
        public float top;
        public float right;
        public float bottom;
        public float paddingTop;
        public float paddingRight;
        public float paddingBottom;
        public float paddingLeft;
    }
}
