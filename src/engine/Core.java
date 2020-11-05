package engine;

import gui.Gui;
import gui.GuiSkin;
import input.Mouse;
import math.Color;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.tinyfd.TinyFileDialogs.tinyfd_messageBox;

public class Core {

    public static void main(String[] args) {
        long window = Application.init();
        Color clear = Color.yellow();

        Texture smwTilesTexture = new Texture("smwTiles.png");

        // TODO: Automatic importing in GuiStyle
        new Texture("greenSheet.png");

        for(int z = 0; z < 50; z++) {
            Debug.log("This is a test: " + z);
        }

        Gui.init();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        while(!glfwWindowShouldClose(window)) {
            Mouse.reset();

            glfwPollEvents();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glClearColor(clear.getRed(), clear.getGreen(), clear.getBlue(), clear.getAlpha());

            Gui.prepare();

            Gui.drawTexture(smwTilesTexture, new Rectangle(0, 0, 100, 100));

            Gui.drawTextureWithTexCoords(smwTilesTexture, new Rectangle(150, 150, 100, 100),
                    new Rectangle(0, 0, 0.125f, 0.125f));

            Gui.label("This is a test", 50, 300);

            GuiSkin skin = GuiSkin.getSkin("defaultGui");

            if (Gui.button(Debug.lastEntry(),
                    new Rectangle(0, Application.height() - 30, Application.width(), 30),
                    "defaultGui", "button", "buttonHover"))
            {
                tinyfd_messageBox("Debug Log", Debug.log(), "okcancel", "", true);
            }

            Rectangle box = Gui.box(new Rectangle(300, 300, 200, 200), "defaultGui");

            Gui.label("How's it going", box._x, box._y);

            Gui.unbind();

            glfwSwapBuffers(window);
        }

        Texture.cleanUp();

        glfwTerminate();
    }
}
