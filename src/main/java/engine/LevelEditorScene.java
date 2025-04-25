package engine;

import components.SpriteRender;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

public class LevelEditorScene extends Scene {

    public LevelEditorScene() {}

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f());

        GameObject obj= new GameObject("obj",new  Transform(new Vector2f(100, 100), new Vector2f(256, 256)));
        obj.addComponent(new SpriteRender(AssetPool.getTexture("assets/images/testImage.png")));
        this.addGameObjectToScene(obj);

        loadResources();
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
    }

    @Override
    public void update(float dt) {
        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();
    }
}