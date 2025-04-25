package util;

import render.Shader;
import render.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();

    public static Shader getShader(String resourceName) {
        File file = new File(resourceName);
        String filePath = file.getAbsolutePath();

        if (shaders.containsKey(filePath)) {
            return shaders.get(filePath);
        } else {
            Shader shader = new Shader(resourceName);
            shader.compileAndLinkShaders();
            shaders.put(filePath, shader);
            return shader;
        }
    }

    public static Texture getTexture(String resourceName) {
        File file = new File(resourceName);
        String filePath = file.getAbsolutePath();

        if (textures.containsKey(filePath)) {
            return textures.get(filePath);
        } else {
            Texture texture = new Texture(resourceName);
            textures.put(filePath, texture);
            return texture;
        }
    }
}