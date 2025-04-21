    package engine;

    import org.joml.Vector2f;
    import org.lwjgl.BufferUtils;
    import render.Shader;
    import render.Texture;
    import util.Time;

    import java.nio.FloatBuffer;
    import java.nio.IntBuffer;

    import static org.lwjgl.opengl.GL30.*;

    public class LevelEditorScene extends Scene {

        private int vaoID, vboID, eboID;
        private Shader defaultShader;
        private Texture testImage;

        GameObject testOBJ;

        // Vertex and index arrays
        private final float[] vertexArray = {
                //position              //color                     //UV coordinates
                100f,   0f, 0.0f,       1.0f, 0.0f, 0.0f, 1.0f,     1,1, //Bottom right 0
                  0f, 100f, 0.0f,       0.0f, 1.0f, 0.0f, 1.0f,     0,0, //Top left     1
                100f, 100f, 0.0f,       0.0f, 0.0f, 1.0f, 1.0f,     1,0, //Top right    2
                  0f,   0f, 0.0f,       1.0f, 1.0f, 0.0f, 1.0f,     0,1  //Bottom left   3
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

            this.testImage= new Texture("assets/images/testImage.png");

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
            int uvSize= 2;
            int floatSize = 4;
            int vertexSizeBytes = (positionSize + colorSize + uvSize) * Float.BYTES;

            glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
            glEnableVertexAttribArray(0);

            glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize * Float.BYTES  );
            glEnableVertexAttribArray(1);

            glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionSize + colorSize) * Float.BYTES);
            glEnableVertexAttribArray(2);
        }

        @Override
        public void update(float dt) {
            // Use shader
            defaultShader.use();

            defaultShader.uploadTexture("TEXT_SAMPLE", 0);
            glActiveTexture(GL_TEXTURE0);
            testImage.bind();

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
