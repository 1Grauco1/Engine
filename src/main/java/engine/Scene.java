package engine;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {
    private boolean isRunning= false;
    private List<GameObject> gameObjects= new ArrayList<>();

    protected Camera camera;
    public Scene(){}

    public abstract void update(float dt);
    public void init(){}
    public void cleanUpScene(){}
    public void addGameObjectToScene(GameObject gO){
        if(!isRunning){
            gameObjects.add(gO);
        }
        else{
            gameObjects.add(gO);
            gO.start();
        }
    };
    public void start(){
        for(GameObject gO : gameObjects){
            gO.start();
        }
    }
}
