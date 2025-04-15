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
    private int shaderProgramID;
    private boolean beingUsed= false;
    private String vertexSource;
    private String fragmentSource;
    private final String filepath;

    // Loads shader source from file and splits into vertex and fragment code
    public Shader(String filepath) {
        this.filepath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            // Extract first shader type
            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, eol).trim();

            // Extract second shader type
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
        int vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);
        checkShaderCompilation(vertexID, "Vertex shader");

        int fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);
        checkShaderCompilation(fragmentID, "Fragment shader");

        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);
        checkProgramLinking(shaderProgramID);

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

    // Activates the shader
    public void use() {
        if(!beingUsed) {
            glUseProgram(shaderProgramID);
            beingUsed= true;
        }
    }

    // Deactivates the shader
    public void detach() {
        glUseProgram(0);
        beingUsed= false;
    }

    // Deletes the shader program
    public void cleanup() {
        glDeleteProgram(shaderProgramID);
    }

    // Uploads a 4x4 matrix to the shader uniform
    public void uploadMat4f(String varName, Matrix4f mat4){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    // Uploads a 3x3 matrix to the shader uniform
    public void uploadMat3f(String varName, Matrix3f mat3){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    // Uploads a 4F vector to the shader uniform
    public void uploadVec4f(String varName, Vector4f vec){
        int varLocation= glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }

    // Uploads a 3F vector to the shader uniform
    public void uploadVec3f(String varName, Vector3f vec){
        int varLocation= glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }

    // Uploads a 2F vector to the shader uniform
    public void uploadVec2f(String varName, Vector2f vec){
        int varLocation= glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform2f(varLocation, vec.x, vec.y);
    }

    // Uploads a float to the shader uniform
    public void uploadFloat(String varName, float val){
        int varLocation= glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation, val);
    }

    // Uploads a int to the shader uniform
    public void uploadInt(String varName, int val){
        int varLocation= glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, val);
    }
}
