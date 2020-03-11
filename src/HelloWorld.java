import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class HelloWorld {

    // The window handle
    private long window;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
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
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glEnable(GL_TEXTURE_2D);

        // Texture must be created below createCapabilities

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

        //Texture texture = new Texture("./res/tile-png-2.png");

        int id = glGenTextures();

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        float x = 0;

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {

            if (glfwGetKey(window, GLFW_KEY_A) == GL_TRUE) {
                x += 0.001f;
            }

            if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GL_TRUE) {
                System.out.println("clicked!");
            }

            if (glfwGetKey(window, GLFW_KEY_W) == GL_TRUE) {
                glfwSetWindowShouldClose(window, true);
            }

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            //texture.bind();

            shader.bind();

            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

            model.render();

//            glBegin(GL_QUADS);
//
//            glTexCoord2d(0, 0);
//            glVertex2f(0f, 0.5f);
//
//            glTexCoord2d(0, 1);
//            glVertex2f(0.5f, 0.5f);
//
//            glTexCoord2d(1, 1);
//            glVertex2f(0.5f, 0f);
//
//            glTexCoord2d(1, 0);
//            glVertex2f(0f, 0f);
//
//            glEnd();

            glfwSwapBuffers(window); // swap the color buffers
        }
    }

}