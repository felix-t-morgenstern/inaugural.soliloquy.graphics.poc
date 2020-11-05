package engine;

import math.Color;
import math.Quaternion;
import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int _program;
    private int _vertexShader;
    private int _fragmentShader;

    private final int NO_PROGRAM = 0;

    private final int STATUS_OK = 1;

    private final int INVALID_LOCATION = -1;

    private final int ATTRIB_LOCATION_VERTICES = 0;
    private final int ATTRIB_LOCATION_UV_COORDS = 1;

    public Shader (String filename) {
        _program = glCreateProgram();

        _vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(_vertexShader, createShader(filename + ".vs"));
        glCompileShader(_vertexShader);
        if (glGetShaderi(_vertexShader, GL_COMPILE_STATUS) != STATUS_OK) {
            throw new RuntimeException(glGetShaderInfoLog(_vertexShader));
        }

        _fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(_fragmentShader, createShader(filename + ".fs"));
        glCompileShader(_fragmentShader);
        if (glGetShaderi(_fragmentShader, GL_COMPILE_STATUS) != STATUS_OK) {
            throw new RuntimeException(glGetShaderInfoLog(_fragmentShader));
        }

        glAttachShader(_program, _vertexShader);
        glAttachShader(_program, _fragmentShader);

        glBindAttribLocation(_program, ATTRIB_LOCATION_VERTICES, "vertices");
        glBindAttribLocation(_program, ATTRIB_LOCATION_UV_COORDS, "uvCoords");

        glLinkProgram(_program);
        if (glGetProgrami(_program, GL_LINK_STATUS) != STATUS_OK) {
            throw new RuntimeException(glGetProgramInfoLog(_program));
        }

        glValidateProgram(_program);
        if (glGetProgrami(_program, GL_VALIDATE_STATUS) != STATUS_OK)
        {
            throw new RuntimeException(glGetProgramInfoLog(_program));
        }
    }

    private String createShader(String filename) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(new File("res/shaders/" + filename)));
            String line;
            while((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            bufferedReader.close();
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
        return stringBuilder.toString();
    }

    public void bind() {
        glUseProgram(_program);
    }

    public void unbind() {
        glUseProgram(NO_PROGRAM);
    }

    private void setUniform(String name, Consumer<Integer> action) {
        int location = glGetUniformLocation(_program, name);
        if (location != INVALID_LOCATION) {
            action.accept(location);
        }
    }

    public void setUniform(String name, float f1, float f2) {
        setUniform(name, location -> glUniform2f(location, f1, f2));
    }

    public void setUniform(String name, float f1, float f2, float f3, float f4) {
        setUniform(name, location -> glUniform4f(location, f1, f2, f3, f4));
    }

    public void setUniform(String name, Color color) {
        setUniform(name,
                location -> glUniform4f(location, color.getRed(), color.getGreen(),
                        color.getBlue(), color.getAlpha()));
    }

    public void setUniform(String name, Quaternion quaternion) {
        setUniform(name, location -> {
            FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(Quaternion.QUATERNION_SIZE);
            quaternion.writeToBuffer(floatBuffer);

            glUniformMatrix4fv(location, false, floatBuffer);

            floatBuffer.flip();
        });
    }
}
