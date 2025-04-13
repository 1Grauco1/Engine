package engine;

import org.lwjgl.BufferUtils;
import render.Shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

public class LevelEditorScene extends Scene {

    private int vaoID, vboID, eboID;
    private Shader testShader;

    // Vertex data
    private final float[] vertexArray = {
            // position         // color (RGBA)
            0.5f, -0.5f, 0.0f,  1.0f, 0.0f, 0.0f, 1.0f, // Bottom right
            -0.5f,  0.5f, 0.0f,  0.0f, 1.0f, 0.0f, 1.0f, // Top left
            0.5f,  0.5f, 0.0f,   0.0f, 0.0f, 1.0f, 1.0f, // Top right
            -0.5f, -0.5f, 0.0f,   1.0f, 1.0f, 0.0f, 1.0f  // Bottom left
    };

    // Index data
    private final int[] elementArray = {
            2, 1, 0, // Top right triangle
            0, 1, 3  // Bottom left triangle
    };

    @Override
    public void init() {
        testShader = new Shader("assets/shaders/default.glsl");
        testShader.compileAndLinkShaders();
        setupVertexData();
    }

    private void setupVertexData() {
        // Generate and bind VAO
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create and upload vertex buffer
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create and upload index buffer
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Set vertex attribute pointers
        int positionSize = 3;  // x, y, z
        int colorSize = 4;     // r, g, b, a
        int floatSize = 4;     // bytes per float
        int vertexSize = (positionSize + colorSize) * floatSize;

        // Position attribute
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSize, 0);
        glEnableVertexAttribArray(0);

        // Color attribute
        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSize, positionSize * floatSize);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float dt) {
        testShader.use();
        glBindVertexArray(vaoID);
        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
        testShader.detach();
    }

    @Override
    public void cleanUpScene() {
        glDeleteBuffers(vboID);
        glDeleteBuffers(eboID);
        glDeleteVertexArrays(vaoID);
        testShader.cleanup();
    }
}