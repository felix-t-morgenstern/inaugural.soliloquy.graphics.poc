package engine;

import gui.Gui;
import gui.GuiSkin;
import input.Mouse;
import math.Color;
import org.joml.Vector3f;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;
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
        glEnable(GL_DEPTH_TEST);
        //glEnable(GL_ALPHA_TEST);
        //glDepthFunc(GL_LESS);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBlendEquation(GL_FUNC_ADD);

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

//            Vector3f lineOrigin = new Vector3f(0f, 0f, 0f);
//            Vector3f lineDestination = new Vector3f(100f, 100f, 0f);
//
//            Gui.line(Color.blue(), lineOrigin, lineDestination);

//            Gui.drawRectangle(new Rectangle(0, 0, 50, 100));

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
        Gui.drawTexture(_texture, new Rectangle(0, 0, 200, 200), -0.5f);
        Gui.drawTexture(_texture, new Rectangle(32, 64, 200, 200), -0.25f);
        Gui.label("TextTextTextTextTextText", 150, 200);
    }
}
