package utils;

import static org.lwjgl.opengl.GL20.*;

public class ShaderUtils {

    private ShaderUtils() {
    }

    public static int load(String vertexPath, String fragPath) {
        String vertex = FileUtils.loadAsString(vertexPath);
        String frag = FileUtils.loadAsString(fragPath);

        return create(vertex, frag);
    }

    public static int create(String vertex, String frag) {
        int program = glCreateProgram();
        int vertexId = glCreateShader(GL_VERTEX_SHADER);
        int fragId = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(vertexId, vertex);
        glShaderSource(fragId, frag);

        glCompileShader(vertexId);
        if(glGetShaderi(vertexId, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Failed to compile vertex shader");
            System.err.println(glGetShaderInfoLog(vertexId));
            return -1;
        }

        glCompileShader(fragId);
        if(glGetShaderi(fragId, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Failed to compile fragment shader");
            System.err.println(glGetShaderInfoLog(fragId));
            return -1;
        }

        glAttachShader(program, vertexId);
        glAttachShader(program, fragId);
        glLinkProgram(program);
        glValidateProgram(program);

        glDeleteShader(vertexId);
        glDeleteShader(fragId);

        return program;
    }
}
