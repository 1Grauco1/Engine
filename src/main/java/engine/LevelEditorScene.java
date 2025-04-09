package engine;

import static org.lwjgl.opengl.GL30.*;

import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class LevelEditorScene extends Scene {

    // Shader sources
    private static final String VERTEX_SHADER_SRC = """
            #version 330 core
            layout (location = 0) in vec3 aPos;
            layout (location = 1) in vec4 aColor;
            out vec4 fColor;
            void main() {
                fColor = aColor;
                gl_Position = vec4(aPos, 1.0);
            }
            """;

    private static final String FRAGMENT_SHADER_SRC = """
            #version 330 core
            in vec4 fColor;
            out vec4 color;
            void main() {
                color = fColor;
            }
            """;

    // OpenGL IDs
    private int shaderProgram;
    private int vaoID, vboID, eboID;

    // Vertex data
    private final float[] vertexArray = {
        // position         // color (RGBA)
         0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, // Bottom right
        -0.5f,  0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, // Top left
         0.5f,  0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, // Top right
        -0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f  // Bottom left
    };

    // Index data
    private final int[] elementArray = {
        2, 1, 0, // Top right triangle
        0, 1, 3  // Bottom left triangle
    };

    @Override
    public void init() {
        compileAndLinkShaders();
        setupVertexData();
    }

    private void compileAndLinkShaders() {
        // Vertex shader
        int vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID, VERTEX_SHADER_SRC);
        glCompileShader(vertexID);
        checkShaderCompilation(vertexID, "Vertex shader");

        // Fragment shader
        int fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, FRAGMENT_SHADER_SRC);
        glCompileShader(fragmentID);
        checkShaderCompilation(fragmentID, "Fragment shader");

        // Shader program
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);
        checkProgramLinking(shaderProgram);

        // Clean up shaders
        glDetachShader(shaderProgram, vertexID);
        glDetachShader(shaderProgram, fragmentID);
        glDeleteShader(vertexID);
        glDeleteShader(fragmentID);
    }

    private void checkShaderCompilation(int shaderID, String shaderType) {
        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            String log = glGetShaderInfoLog(shaderID);
            throw new RuntimeException("""
                %s compilation failed:
                %s
                """.formatted(shaderType, log));
        }
    }

    private void checkProgramLinking(int programID) {
        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
            String log = glGetProgramInfoLog(programID);
            throw new RuntimeException("""
                Shader program linking failed:
                %s
                """.formatted(log));
        }
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

        // Unbind
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    @Override
    public void update(float dt) {
        glUseProgram(shaderProgram);
        glBindVertexArray(vaoID);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
        glUseProgram(0);
    }                                                                                       

    @Override
    public void cleanup() {
        glDeleteBuffers(vboID);
        glDeleteBuffers(eboID);
        glDeleteVertexArrays(vaoID);
        glDeleteProgram(shaderProgram);
    }
}