package render;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int shaderProgramID;      // OpenGL shader program ID
    private boolean beingUsed = false; // Tracks if shader is currently bound
    private String vertexSource;      // Vertex shader source code
    private String fragmentSource;    // Fragment shader source code
    private final String filepath;    // Path to shader source file

    // Constructor - loads and parses shader file into vertex/fragment parts
    public Shader(String filepath) {
        this.filepath = filepath;
        try {
            // Read entire shader file
            String source = new String(Files.readAllBytes(Paths.get(filepath)));

            // Split source by #type declarations
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            // Parse first shader type (vertex/fragment)
            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, eol).trim();

            // Parse second shader type
            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).trim();

            // Assign sources based on type order
            if (firstPattern.equals("vertex")) {
                vertexSource = splitString[1];
            } else if (firstPattern.equals("fragment")) {
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Unexpected token '" + firstPattern + "'");
            }

            if (secondPattern.equals("vertex")) {
                if (vertexSource != null) throw new IOException("Multiple vertex shaders detected");
                vertexSource = splitString[2];
            } else if (secondPattern.equals("fragment")) {
                if (fragmentSource != null) throw new IOException("Multiple fragment shaders detected");
                fragmentSource = splitString[2];
            } else {
                throw new IOException("Unexpected token '" + secondPattern + "'");
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error: Could not open file for shader: '" + filepath + "'");
        }
    }

    // Compiles shaders and links them into a program
    public void compileAndLinkShaders() {
        // Compile vertex shader
        int vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);
        checkShaderCompilation(vertexID, "Vertex shader");

        // Compile fragment shader
        int fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);
        checkShaderCompilation(fragmentID, "Fragment shader");

        // Create program and attach shaders
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);
        checkProgramLinking(shaderProgramID);

        // Clean up individual shaders
        glDetachShader(shaderProgramID, vertexID);
        glDetachShader(shaderProgramID, fragmentID);
        glDeleteShader(vertexID);
        glDeleteShader(fragmentID);
    }

    // Checks if shader compiled successfully
    private void checkShaderCompilation(int shaderID, String shaderType) {
        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            String log = glGetShaderInfoLog(shaderID);
            throw new RuntimeException("""
                %s compilation failed in '%s':
                %s
                """.formatted(shaderType, filepath, log));
        }
    }

    // Checks if shader program linked successfully
    private void checkProgramLinking(int programID) {
        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
            String log = glGetProgramInfoLog(programID);
            throw new RuntimeException("""
                Shader program linking failed in '%s':
                %s
                """.formatted(filepath, log));
        }
    }

    // Activates the shader program (only if not already in use)
    public void use() {
        if (!beingUsed) {
            glUseProgram(shaderProgramID);
            beingUsed = true;
        }
    }

    // Deactivates the shader program
    public void detach() {
        glUseProgram(0);
        beingUsed = false;
    }

    // Deletes the shader program
    public void cleanup() {
        glDeleteProgram(shaderProgramID);
    }

    /* Uniform Upload Methods */

    // Uploads 4x4 matrix to shader
    public void uploadMat4f(String varName, Matrix4f mat4) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    // Uploads 3x3 matrix to shader
    public void uploadMat3f(String varName, Matrix3f mat3) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    // Uploads 4D vector to shader
    public void uploadVec4f(String varName, Vector4f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }

    // Uploads 3D vector to shader
    public void uploadVec3f(String varName, Vector3f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }

    // Uploads 2D vector to shader
    public void uploadVec2f(String varName, Vector2f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform2f(varLocation, vec.x, vec.y);
    }

    // Uploads float value to shader
    public void uploadFloat(String varName, float val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation, val);
    }

    // Uploads integer value to shader
    public void uploadInt(String varName, int val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, val);
    }

    // Uploads texture slot to sampler uniform
    public void uploadTexture(String varName, int slot) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, slot);
    }

    public void uploadIntArray(String varName, int[] array) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1iv(varLocation, array);
    }
}