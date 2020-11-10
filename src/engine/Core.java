package engine;

import gui.Gui;
import gui.GuiSkin;
import input.Mouse;
import math.Color;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.tinyfd.TinyFileDialogs.tinyfd_messageBox;

public class Core {
    private static Texture _texture;

    public static void main(String[] args) {
        long window = Application.init();
        Color clear = Color.darkGrey();

        _texture = new Texture("smwTiles.png");

        // TODO: Automatic importing in GuiStyle
        new Texture("darkGreySheet.png");
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

            GuiSkin.getSkin("defaultGui");

            if (Gui.button(Debug.lastEntry(),
                    new Rectangle(0, Application.height() - 30, Application.width(), 30),
                    "defaultGui", "button", "buttonHover"))
            {
                tinyfd_messageBox("Debug Log", Debug.log(), "okcancel", "", true);
            }

            Gui.window(new Rectangle(50, 0, 200, Application.height() - 30), "Some Window",
                    Core::test, "defaultGui", "box");

            Rectangle box = Gui.box(new Rectangle(300, 300, 200, 200), "defaultGui");

            Gui.label("How's it going", box.x, box.y);

            Gui.unbind();

            glfwSwapBuffers(window);
        }

        Texture.cleanUpAll();
        Mesh.cleanUpAll();

        glfwTerminate();
    }

    public static void test(int windowId) {
        Gui.window(new Rectangle(0, 0, 200, Application.height() - 30), "Some Window",
                Core::test2, "defaultGui", "box");
    }

    public static void test2(int windowId) {
        Gui.drawTexture(_texture, new Rectangle(150, 0, 100, 100));
        Gui.label("TextTextTextTextTextText", 150, 200);
    }
}
