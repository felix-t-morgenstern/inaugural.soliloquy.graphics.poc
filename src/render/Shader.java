package render;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int _program;
    private int _vertexShader;
    private int _fragmentShader;
    private FloatBuffer _buffer;

    private static Integer BOUND_PROGRAM;

    public Shader(String filename) {
        _program = glCreateProgram();

        _vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(_vertexShader, readFile(filename + ".vertex"));
        glCompileShader(_vertexShader);
        if (glGetShaderi(_vertexShader, GL_COMPILE_STATUS) != 1) {
            System.err.println(glGetShaderInfoLog(_vertexShader));
            throw new IllegalArgumentException(glGetShaderInfoLog(_vertexShader));
        }

        _fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(_fragmentShader, readFile(filename + ".fragment"));
        glCompileShader(_fragmentShader);
        if (glGetShaderi(_fragmentShader, GL_COMPILE_STATUS) != 1) {
            System.err.println(glGetShaderInfoLog(_fragmentShader));
            throw new IllegalArgumentException(glGetShaderInfoLog(_fragmentShader));
        }

        glAttachShader(_program, _vertexShader);
        glAttachShader(_program, _fragmentShader);

        glBindAttribLocation(_program, 0, "vertices");
        glBindAttribLocation(_program, 1, "textures");

        glLinkProgram(_program);
        if (glGetProgrami(_program, GL_LINK_STATUS) != 1) {
            System.err.println(glGetProgramInfoLog(_program));
            throw new IllegalArgumentException(glGetProgramInfoLog(_program));
        }
        glValidateProgram(_program);
        if (glGetProgrami(_program, GL_VALIDATE_STATUS) != 1) {
            System.err.println(glGetProgramInfoLog(_program));
            throw new IllegalArgumentException(glGetProgramInfoLog(_program));
        }

        _buffer = BufferUtils.createFloatBuffer(16);
    }

    protected void finalize() throws Throwable {
        glDetachShader(_program, _vertexShader);
        glDetachShader(_program, _fragmentShader);
        glDeleteShader(_vertexShader);
        glDeleteShader(_fragmentShader);
        glDeleteProgram(_program);

        super.finalize();
    }

    public void setUniform(String name, int value) {
        int location = glGetUniformLocation(_program, name);
        if (location != -1) {
            glUniform1i(location, value);
        }
    }

    public void setUniform(String name, Matrix4f value) {
        int location = glGetUniformLocation(_program, name);
        FloatBuffer buffer = _buffer;
        value.get(buffer);
        if (location != -1) {
            glUniformMatrix4fv(location, false, buffer);
        }
    }

    public void setUniform(String name, float f1, float f2) {
        int location = glGetUniformLocation(_program, name);
        if (location != -1) {
            glUniform2f(location, f1, f2);
        }
    }

    public void setUniform(String name, float x, float y, float z, float w)
    {
        //Get the location of the uniform and if it exists, pass in the values
        int location = glGetUniformLocation(_program, name);
        if(location != -1) glUniform4f(location, x, y, z, w);
    }

    public void setUniform(String name, Color color) {
        int location = glGetUniformLocation(_program, name);
        if (location != 1) {
            glUniform4f(location, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        }
    }

    public void bind() {
        glUseProgram(_program);
    }

    private String readFile(String filename) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File("./shaders/" + filename)));
            String line;
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
