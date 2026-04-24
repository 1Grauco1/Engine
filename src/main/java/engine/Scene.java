package engine;

import render.Renderer;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    protected Renderer renderer = new Renderer();
    protected Camera camera;
    // BUG FIX: was never set to true, so runtime-added objects never got start() or renderer.add()
    private boolean isRunning = false;
    public List<GameObject> gameObjects = new ArrayList<>();

    public Scene() {}

    public abstract void update(float dt);
    public void init() {}

    public void start() {
        for (GameObject go : gameObjects) {
            go.start();
            this.renderer.add(go);
        }
        isRunning = true; // BUG FIX: mark scene as running after start
    }

    public void addGameObjectToScene(GameObject go) {
        if (!isRunning) {
            gameObjects.add(go);
        } else {
            gameObjects.add(go);
            go.start();
            this.renderer.add(go);
        }
    }

    public Camera camera() {
        return this.camera;
    }
}
