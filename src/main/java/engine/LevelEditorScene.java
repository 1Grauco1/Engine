package engine;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import render.Shader;
import util.Time;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

public class LevelEditorScene extends Scene {

    private int vaoID, vboID, eboID;
    private Shader defaultShader;

    // Vertex and index arrays
    private final float[] vertexArray = {
            100.5f, -0.5f, 0.0f,  1.0f, 0.0f, 0.0f, 1.0f,
            -0.5f,  100.5f, 0.0f,  0.0f, 1.0f, 0.0f, 1.0f,
            100.5f,  100.5f, 0.0f,  0.0f, 0.0f, 1.0f, 1.0f,
            -0.5f, -0.5f, 0.0f,  1.0f, 1.0f, 0.0f, 1.0f
    };

    private final int[] elementArray = {
            2, 1, 0,
            0, 1, 3
    };

    @Override
    public void init() {
        // Create camera
        this.camera = new Camera(new Vector2f());

        // Load and compile shader
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compileAndLinkShaders();

        // Set up buffers and attributes
        setupVertexData();
    }

    private void setupVertexData() {
        // Create and bind VAO
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Upload vertex data to GPU
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Upload index data to GPU
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Define layout of vertex data (position and color)
        int positionSize = 3;
        int colorSize = 4;
        int floatSize = 4;
        int vertexSize = (positionSize + colorSize) * floatSize;

        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSize, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSize, positionSize * floatSize);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float dt) {
        // Use shader
        defaultShader.use();

        // Upload camera matrices
        defaultShader.uploadMat4f("uProj", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTime());

        // Render the shape
        glBindVertexArray(vaoID);
        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);

        // Stop using shader
        defaultShader.detach();
    }

    @Override
    public void cleanUpScene() {
        // Delete OpenGL resources
        glDeleteBuffers(vboID);
        glDeleteBuffers(eboID);
        glDeleteVertexArrays(vaoID);
        defaultShader.cleanup();
    }
}
