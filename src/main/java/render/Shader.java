package render;

import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int shaderProgramID;
    private String vertexSource;
    private String fragmentSource;
    private final String filepath;

    public Shader(String filepath) {
        this.filepath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            // Find first pattern
            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, eol).trim();

            // Find second pattern
            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).trim();

            // Determine which is vertex and which is fragment
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

        // Link shader program
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);
        checkProgramLinking(shaderProgramID);

        // Clean up shaders
        glDetachShader(shaderProgramID, vertexID);
        glDetachShader(shaderProgramID, fragmentID);
        glDeleteShader(vertexID);
        glDeleteShader(fragmentID);
    }

    private void checkShaderCompilation(int shaderID, String shaderType) {
        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            String log = glGetShaderInfoLog(shaderID);
            throw new RuntimeException("""
                %s compilation failed in '%s':
                %s
                """.formatted(shaderType, filepath, log));
        }
    }

    private void checkProgramLinking(int programID) {
        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
            String log = glGetProgramInfoLog(programID);
            throw new RuntimeException("""
                Shader program linking failed in '%s':
                %s
                """.formatted(filepath, log));
        }
    }

    public void use() {
        glUseProgram(shaderProgramID);
    }

    public void detach() {
        glUseProgram(0);
    }

    public void cleanup() {
        glDeleteProgram(shaderProgramID);
    }
}
