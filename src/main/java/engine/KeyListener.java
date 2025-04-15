package engine;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener {
    private static KeyListener instance;
    private static boolean[] keyPressed = new boolean[350];

    // Private constructor for singleton
    private KeyListener(){}

    // Returns the single instance of KeyListener
    public static KeyListener get(){
        if(instance == null) {
            instance = new KeyListener();
        }
        return instance;
    }

    // Called whenever a key is pressed or released
    public static void keyCallback(long window, int key, int scancode, int action, int mods){
        if(action == GLFW_PRESS){
            get().keyPressed[key] = true;
        } else if(action == GLFW_RELEASE){
            get().keyPressed[key] = false;
        }
    }

    // Checks if a specific key is currently pressed
    public static boolean isKeyPressed(int keyCode) {
        return get().keyPressed[keyCode];
    }
}
