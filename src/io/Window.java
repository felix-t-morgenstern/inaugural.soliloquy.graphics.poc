package io;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;

import static org.lwjgl.glfw.GLFW.*;

public class Window {
    private long _window;
    private int _width, _height;
    private boolean _fullscreen;
    private Input _input;

    // Consider moving this to another class
    public static void setCallbacks() {
        glfwSetErrorCallback(new GLFWErrorCallback() {
            @Override
            public void invoke(int error, long description) {
                throw new IllegalStateException(GLFWErrorCallback.getDescription(description));
            }
        });
    }

    public Window() {
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        assert vidMode != null;
        setSize(vidMode.width(), vidMode.height());

        setFullscreen(false);
    }

    public Window(int width, int height) {
        setSize(width, height);
    }

    public void createWindow(String title) {
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        assert vidMode != null;

        glfwWindowHint(GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
        glfwWindowHint(GLFW.GLFW_FLOATING, GLFW.GLFW_TRUE);
        glfwWindowHint(GLFW_RED_BITS, vidMode.redBits());
        glfwWindowHint(GLFW_GREEN_BITS, vidMode.greenBits());
        glfwWindowHint(GLFW_BLUE_BITS, vidMode.blueBits());
        glfwWindowHint(GLFW_REFRESH_RATE, vidMode.refreshRate());

        _window = glfwCreateWindow(_width, _height, title, _fullscreen ? glfwGetPrimaryMonitor() : 0, 0);

        if (_window == 0) {
            throw new IllegalStateException("Failed to create window");
        }

        if (!_fullscreen) {
            glfwSetWindowPos(_window, (vidMode.width() - _width) / 2, (vidMode.height() - _height) / 2);

            glfwShowWindow(_window);
        }

        glfwMakeContextCurrent(_window);

        _input = new Input(_window);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(_window);
    }

    public void swapBuffers() {
        glfwSwapBuffers(_window);
    }

    public void setSize(int width, int height) {
        // TODO: Add sanity checks
        _width = width;
        _height = height;
    }

    public void setFullscreen(boolean fullscreen) {
        _fullscreen = fullscreen;
    }

    public void update() {
        _input.update();
        glfwPollEvents();
    }

    public int getWidth() {
        return _width;
    }

    public int getHeight() {
        return _height;
    }

    public boolean isFullscreen() {
        return _fullscreen;
    }

    public long getWindow() {
        return _window;
    }

    public Input input() {
        return _input;
    }
}
