package engine;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    private static MouseListener instance;

    private double scrollX, scrollY;
    private double xPos, yPos;
    private double lastX, lastY;

    private final boolean[] mouseButtonPressed = new boolean[3];

    private boolean isDragging;

    // Private constructor (singleton)
    private MouseListener(){
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
    }

    // Returns the only instance of MouseListener
    public static MouseListener get(){
        if(instance == null){
            instance = new MouseListener();
        }
        return instance;
    }

    // Called when mouse moves
    public static void mousePosCallback(long window, double xpos, double ypos) {
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = xpos;
        get().yPos = ypos;
        get().isDragging = get().mouseButtonPressed[0] || get().mouseButtonPressed[1] || get().mouseButtonPressed[2];
    }

    // Called when mouse button is pressed or released
    public static void mouseButtonCallback(long window, int button, int action, int mods){
        if(action == GLFW_PRESS) {
            if(button < get().mouseButtonPressed.length){
                get().mouseButtonPressed[button] = true;
            }
        } else if (action == GLFW_RELEASE) {
            if(button < get().mouseButtonPressed.length){
                get().mouseButtonPressed[button] = false;
                get().isDragging = false;
            }
        }
    }

    // Called when mouse scrolls
    public static void mouseScrollCallback(long window, double xOffset, double yOffset){
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    // Called at the end of each frame
    public static void endFrame(){
        get().scrollX = 0;
        get().scrollY = 0;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
    }

    // Get current mouse X position
    public static float getX(){
        return (float) get().xPos;
    }

    // Get current mouse Y position
    public static float getY(){
        return (float) get().yPos;
    }

    // Get change in X since last frame
    public static float getDx(){
        return (float)(get().lastX - get().xPos);
    }

    // Get change in Y since last frame
    public static float getDy(){
        return (float)(get().lastY - get().yPos);
    }

    // Get scroll X movement
    public static float getScrollX(){
        return (float) get().scrollX;
    }

    // Get scroll Y movement
    public static float getScrollY(){
        return (float) get().scrollY;
    }

    // Check if mouse is dragging
    public static boolean isDragging(){
        return get().isDragging;
    }

    // Check if a mouse button is pressed
    public static boolean mouseButtonDown(int button){
        if(button < get().mouseButtonPressed.length){
            return get().mouseButtonPressed[button];
        } else {
            return false;
        }
    }
}
