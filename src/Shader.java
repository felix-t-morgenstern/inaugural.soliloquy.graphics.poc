import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int _program;
    private int _vertexShader;
    private int _fragmentShader;

    public Shader(String filename) {
        _program = glCreateProgram();

        _vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(_vertexShader, readFile(filename + ".vertex"));
        glCompileShader(_vertexShader);
        if (glGetShaderi(_vertexShader, GL_COMPILE_STATUS) != 1) {
            throw new IllegalArgumentException(glGetShaderInfoLog(_vertexShader));
        }

        _fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(_fragmentShader, readFile(filename + ".fragment"));
        glCompileShader(_fragmentShader);
        if (glGetShaderi(_fragmentShader, GL_COMPILE_STATUS) != 1) {
            throw new IllegalArgumentException(glGetShaderInfoLog(_fragmentShader));
        }

        glAttachShader(_program, _vertexShader);
        glAttachShader(_program, _fragmentShader);

        glBindAttribLocation(_program, 0, "vertices");

        glLinkProgram(_program);

        if (glGetProgrami(_program, GL_LINK_STATUS) != 1) {
            throw new IllegalArgumentException(glGetShaderInfoLog(_program));
        }

        glValidateProgram(_program);

        if (glGetProgrami(_program, GL_VALIDATE_STATUS) != 1) {
            throw new IllegalArgumentException(glGetShaderInfoLog(_program));
        }
    }

    public void bind() {
        glUseProgram(_program);
    }

    private String readFile(String filename) {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(new File("./shaders/" + filename)));
            String line;
            while((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
