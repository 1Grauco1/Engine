package engine;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    int width, height;
    String title;
    private long glfwWindow;
    public float r, g, b, a;

    private static Window window= null;

    private static Scene currentScene;

    private Window(){
        this.width= 1980;
        this.height= 1080;
        this.title= "Game";
        r= 1; g= 0; b= 1; a= 1;
    }

    public static void changeScene(int newScene){
        switch (newScene){
            case 0:
                currentScene= new LevelEditorScene();
                currentScene.init();
                currentScene.start();
                break;
            case 1:
                currentScene= new LevelScene();
                currentScene.init();
                currentScene.start();
                break;
            default:
                assert false: "Unknown scene '"+ newScene +"'";
                break;
        }
    }

    public static Window get(){
        if(Window.window == null){
            Window.window= new Window();
        }

        return Window.window;
    }

    public static Scene getScene(){
        return get().currentScene;
    }

    public void run(){
        System.out.println("Hello LWJGL "+ Version.getVersion() +"!");

        init();
        loop();

        //Free the Memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        //Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();

    }

    public void init(){
        //Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        //Initialize GLFW
        if(!glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        //Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        //Create Window;
        glfwWindow= glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        if(glfwWindow == NULL){
            throw new IllegalStateException("Failed to create the GLFW window");
        }

        //Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);

        //Enable Vsync
        glfwSwapInterval(1);

        //Make the window visible;
        glfwShowWindow(glfwWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        Window.changeScene(0);

    }
    public void loop(){

        float beginTime= (float) glfwGetTime();
        float endTime;
        float dt= -1.0f;

        while(!glfwWindowShouldClose(glfwWindow)){
            //Poll Events
            glfwPollEvents();


            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            if(dt >= 0){
                currentScene.update(dt);
            }

            glfwSwapBuffers(glfwWindow);

            endTime= (float) glfwGetTime();
            dt= endTime - beginTime;
            beginTime= endTime;
        }
    }


}
