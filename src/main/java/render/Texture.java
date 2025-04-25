package render;

import org.lwjgl.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
    private String filePath;  // Path to the texture image file
    private int texID;       // OpenGL texture ID

    // Constructor - loads texture from file and sets up OpenGL texture
    public Texture(String filePath) {
        this.filePath = filePath;

        // Generate and bind OpenGL texture
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);

        // Set texture wrapping parameters (repeat texture if UVs exceed 1.0)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // Set texture filtering (use nearest neighbor for pixelated look)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Load image using STB library
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        ByteBuffer image = stbi_load(filePath, width, height, channels, 0);
        stbi_set_flip_vertically_on_load(true);

        if (image != null) {
            // Upload image data to GPU based on number of channels
            if (channels.get(0) == 3) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0,
                        GL_RGB, GL_UNSIGNED_BYTE, image);
            } else if (channels.get(0) == 4) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0,
                        GL_RGBA, GL_UNSIGNED_BYTE, image);
            }
            else {
                assert false : "Error: (Texture) Unknown number of channels '" + channels.get(0) + "'";
            }
        }
        else {
            assert false : "Error: (Texture) Could not load image '" + filePath + "'";
        }

        // Free the image memory
        stbi_image_free(image);
    }

    // Bind this texture for use in rendering
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, texID);
    }

    // Unbind any texture
    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}