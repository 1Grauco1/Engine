// LevelEditorScene.java with improvements
package engine;

import components.SpriteRender;
import components.SpriteSheet;
import org.joml.Vector2f;
import util.AssetPool;

public class LevelEditorScene extends Scene {

    private GameObject obj1;

    public LevelEditorScene() {}

    @Override
    public void init() {
        loadResources();
        this.camera = new Camera(new Vector2f());

        SpriteSheet sprites = AssetPool.getSpriteSheet("assets/images/Sprite-0003.png");

        obj1 = new GameObject("Test Object",
                new Transform(new Vector2f(100, 100), new Vector2f(32, 32)));
        obj1.addComponent(new SpriteRender(sprites.getSprite(0)));
        this.addGameObjectToScene(obj1);
    }

    private void loadResources() {
        // Load shader
        AssetPool.getShader("assets/shaders/default.glsl");

        // Load sprite sheet
        AssetPool.addSpriteSheet("assets/images/Sprite-0003.png",
                new SpriteSheet(
                        AssetPool.getTexture("assets/images/Sprite-0003.png"),
                        16, 16, 14, 0
                ));
    }

    @Override
    public void update(float dt) {
        obj1.transform.position.x += 10 * dt;
        // Update all game objects
        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

        // Render scene
        this.renderer.render();
    }
}