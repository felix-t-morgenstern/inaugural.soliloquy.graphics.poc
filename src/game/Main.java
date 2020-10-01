package game;

import entity.Player;
import io.Timer;
import io.Window;
import org.lwjgl.opengl.GL;
import render.Camera;
import render.Model;
import render.Shader;
import render.TrueTypeFont;
import world.TileRenderer;
import world.World;

import java.awt.*;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main implements Runnable {
    private int _width = 1280;
    private int _height = 720;

    private Main() {}

    public static void main(String[] args) {
        new Main().run();
    }

    @Override
    public void run() {
        Window.setCallbacks();

        if (!glfwInit()) {
            // TODO: exception handling
            return;
        }

//        Window window = new Window();
//        window.setFullscreen(true);
        Window window = new Window(_width, _height);
        window.createWindow("Game");

        GL.createCapabilities();

        Camera camera = new Camera(window.getWidth(), window.getHeight());

        glEnable(GL_TEXTURE_2D);

        float[] vertices = new float[] {
                -1f, 1f, 0.0f, // Top Left          0
                1f, 1f, 0.0f, // Top right          1
                1f, -1f, 0.0f, // Bottom right      2
                -1f, -1f, 0.0f, // Bottom left      3
        };

        float[] texCoords = new float[] {
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f
        };

        int[] indices = new int[] {
                0, 1, 2,
                2, 3, 0
        };

        Model model = new Model(vertices, texCoords, indices);

        TileRenderer tileRenderer = new TileRenderer(model);

        Shader shader = new Shader("shader");
        Shader defaultShader = new Shader("defaultShader");

        World world = new World("test_level");

        Player player = new Player(model);

        TrueTypeFont font = new TrueTypeFont(new Font("TimesRoman", Font.PLAIN, 64), model);

        double frameCap = 1.0/60.0;

        double frameTime = 0.0;
        int frames = 0;

        double time = Timer.getTime();
        double unprocessed = 0;

        while (!window.shouldClose()) {
            boolean canRender = false;

            double time2 = Timer.getTime();
            double passed = time2 - time;
            unprocessed += passed;

            frameTime += passed;

            time = time2;

            while (unprocessed >= frameCap) {
                unprocessed -= frameCap;
                canRender = true;

                if (window.input().isKeyReleased(GLFW_KEY_ESCAPE)) {
                    glfwSetWindowShouldClose(window.getWindow(), true);
                }

                player.update((float)frameCap, window, camera, world);

                world.correctCamera(camera, window);

                window.update();

                if (frameTime >= 1.0) {
                    System.out.println("FPS: " + (double)frames / frameTime);
                    frameTime = 0.0;
                    frames = 0;
                }
            }

            if (canRender) {
                glClear(GL_COLOR_BUFFER_BIT);

//                shader.bind();
//                shader.setUniform("sampler", 0);
//                shader.setUniform("projection", camera.getProjection().mul(target));
//                texture.bind(0);
                //model.render();

                world.render(tileRenderer, shader, camera, window);

                player.render(shader, camera);

                font.renderLine("This is the line!", defaultShader, Color.RED, 100, 100);

                window.swapBuffers();
                frames++;
            }
        }

        glfwTerminate();
    }
}
