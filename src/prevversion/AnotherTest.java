package prevversion;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class AnotherTest {
    public void run() {
        long window = init();

        loop(window);

//        float[] vertices = {
//                -0.5f, -0.5f, 0.0f,
//                0.5f, -0.5f, 0.0f,
//                0.0f,  0.5f, 0.0f
//        };
//
//        float[] textureCoords = new float[] {
//                0f, 0f,
//                1f, 0f,
//                1f, 1f,
//                0f, 1f
//        };
//
//        int[] indices = new int[] {
//                0, 1, 2,
//                2, 3, 0
//        };
//
//        prevversion.Model model = new prevversion.Model(vertices, textureCoords, indices);
//
//        prevversion.Shader shader = new prevversion.Shader("shader");
//
//        //prevversion.Texture texture = new prevversion.Texture("./res/tile-png-2.png");
//
//        int id = glGenTextures();
//
//        glfwShowWindow(window);
//
//        while(!glfwWindowShouldClose(window))
//        {
//            processInput(window);
//
//            glfwSwapBuffers(window);
//            glfwPollEvents();
//
//            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
//
//            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
//
//            //texture.bind();
//
//            shader.bind();
//
//            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
//
//            model.render();
//
//            glfwSwapBuffers(window); // swap the color buffers
//        }
//
//        // Free the window callbacks and destroy the window
//        glfwFreeCallbacks(window);
//        glfwDestroyWindow(window);
//
//        // Terminate GLFW and free the error callback
//        glfwTerminate();
//        glfwSetErrorCallback(null).free();
    }

    private long init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        if ( !glfwInit() ) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_SAMPLES, 4); // 4x antialiasing
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE); // To make MacOS happy; should not be needed
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE); // We don't want the old OpenGL

        long window = glfwCreateWindow(800, 600, "LearnOpenGL", NULL, NULL);

        if (window == NULL) {
            glfwTerminate();
            throw new IllegalStateException("OpenGL failed to create window");
        }

        glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_W && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(w, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window); // Initialize GLEW
        // Enable v-sync
        glfwSwapInterval(1);

        GL.createCapabilities();

        glViewport(0, 0, 800, 600);

        glfwSetFramebufferSizeCallback(window, this::framebufferSizeCallback);

        glEnable(GL_TEXTURE_2D);

        // Make the window visible
        glfwShowWindow(window);

        return window;
    }

    private void framebufferSizeCallback(long window, int width, int height)
    {
        glViewport(0, 0, width, height);
    }

    private void processInput(long window)
    {
        if(glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)
            glfwSetWindowShouldClose(window, true);
    }

    private void loop(long window) {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        //GL.createCapabilities();

        glEnable(GL_TEXTURE_2D);

        // prevversion.Texture must be created below createCapabilities

        float[] vertices = new float[] {
                -0.5f, 0.5f, 0, //TOP LEFT 0
                0.5f, 0.5f, 0, // TOP RIGHT 1
                0.5f, -0.5f, 0, // BOTTOM RIGHT 2
                -0.5f, -0.5f, 0, // BOTTOM LEFT 3

        };

        float[] textureCoords = new float[] {
                0f, 0f,
                1f, 0f,
                1f, 1f,
                0f, 1f
        };

        int[] indices = new int[] {
                0, 1, 2,
                2, 3, 0
        };

        Model model = new Model(vertices, textureCoords, indices);

        Shader shader = new Shader("shader");

        int id = glGenTextures();

        glClearColor(0.0f, 0.5f, 0.25f, 1.0f);

        float x = 0;

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            shader.bind();

            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

            model.render();

            glfwSwapBuffers(window); // swap the color buffers
        }
    }

    void drawTriangle() {
        float[] vertices = {
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.0f,  0.5f, 0.0f
        };

        float[] textureCoords = new float[] {
                0f, 0f,
                1f, 0f,
                1f, 1f,
                0f, 1f
        };

        int[] indices = new int[] {
                0, 1, 2,
                2, 3, 0
        };

        Model model = new Model(vertices, textureCoords, indices);

        Shader shader = new Shader("shader");

        int id = glGenTextures();

        shader.bind();

        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        model.render();
    }
}
