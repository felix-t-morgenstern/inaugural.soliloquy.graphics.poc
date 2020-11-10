package engine;

import input.Mouse;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;

public class Application {
    private static final String NAME = "Soliloquy LWJGL POC App";

    private static int Width = 800;
    private static int Height = 600;

    private static long Window;

    private static Rectangle Dimensions;

    private static GLFWWindowSizeCallback WindowResizeCallback;

    public static long init() {
        if (!glfwInit()) {
            throw new RuntimeException("GLFW failed to initialize");
        }

        Dimensions = new Rectangle(0, 0, Width, Height);

        Window = glfwCreateWindow(Width, Height, NAME, 0, 0);
        glfwShowWindow(Window);
        glfwMakeContextCurrent(Window);

        GL.createCapabilities();
        glClearColor(0, 0, 0, 1);

        glfwSetMouseButtonCallback(Window, new Mouse());

        WindowResizeCallback = GLFWWindowSizeCallback.create(Application::onWindowResize);
        glfwSetWindowSizeCallback(Window, WindowResizeCallback);

        return Window;
    }

    public static void onWindowResize(long window, int width, int height) {
        Dimensions.set(0, 0, Width = width, Height = height);

        glViewport(0, 0, Width, Height);
    }

    public static int width() {
        return Width;
    }

    public static int height() {
        return Height;
    }

    public static Rectangle dimensions() {
        return Dimensions;
    }

    public static long window() {
        return Window;
    }
}
