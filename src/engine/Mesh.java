package engine;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {
    private int _verticesId;
    private int _uvId;
    private int _vertexArrayObject;

    private float[] _vertices;
    private float[] _uvCoordinates;

    private final static List<Mesh> MESHES = new ArrayList<>();

    private final int POSITIONS = 0;
    private final int TEXTURE_COORDS = 1;
    private final int NO_BUFFER = 0;
    private final int NO_VERTEX_ARRAY = 0;

    public Mesh(float [] vertices, float[] uvCoordinates) {
        _vertices = vertices;
        _uvCoordinates = uvCoordinates;

        _vertexArrayObject = glGenVertexArrays();
        glBindVertexArray(_vertexArrayObject);

        _verticesId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, _verticesId);
        glBufferData(GL_ARRAY_BUFFER, createBuffer(_vertices), GL_STATIC_DRAW);
        glVertexAttribPointer(0, _vertices.length/3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        _uvId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, _uvId);
        glBufferData(GL_ARRAY_BUFFER, createBuffer(_uvCoordinates), GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        MESHES.add(this);
    }

    private FloatBuffer createBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    public void render() {
        glDrawArrays(GL_TRIANGLES, 0, 6);
    }

    public void bind() {
        glBindVertexArray(_vertexArrayObject);
        glEnableVertexAttribArray(POSITIONS);
        glEnableVertexAttribArray(TEXTURE_COORDS);

        glBindBuffer(GL_ARRAY_BUFFER, _verticesId);
        glVertexAttribPointer(POSITIONS, 2, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, _uvId);
        glVertexAttribPointer(TEXTURE_COORDS, 2, GL_FLOAT, false, 0, 0);
    }

    public void unbind() {
        glBindBuffer(GL_ARRAY_BUFFER, NO_BUFFER);
        glDisableVertexAttribArray(POSITIONS);
        glDisableVertexAttribArray(TEXTURE_COORDS);
        glBindVertexArray(NO_VERTEX_ARRAY);
    }

    private void cleanUp() {
        glDeleteVertexArrays(_vertexArrayObject);
        glDeleteBuffers(_verticesId);
    }

    public static void cleanUpAll() {
        MESHES.forEach(Mesh::cleanUp);
    }
}
