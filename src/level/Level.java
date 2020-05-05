package level;

import graphics.Shader;
import graphics.Texture;
import graphics.VertexArray;

public class Level {
    private VertexArray background;
    private Texture backgroundTexture;

    public Level() {
        float[] backgroundVertices = new float[] {
                -10.0f, -10.0f * 9.0f / 16.0f, 0.0f,
                -10.0f, 10.0f * 9.0f / 16.0f, 0.0f,
                0.0f, 10.0f * 9.0f / 16.0f, 0.0f,
                0.0f, -10.0f * 9.0f / 16.0f, 0.0f,
        };

        byte[] indices = new byte[] {
                0, 1, 2,
                2, 3, 0
        };

        float[] textureCoordinates = new float[] {
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f
        };

        background = new VertexArray(backgroundVertices, indices, textureCoordinates);
        backgroundTexture = new Texture("res/bg.jpeg");
    }

    public void render() {
        backgroundTexture.bind();
        Shader.BG.enable();
        background.render();
        Shader.BG.disable();
        backgroundTexture.unbind();
    }
}
