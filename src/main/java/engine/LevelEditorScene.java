// LevelEditorScene.java with improvements
package engine;

import components.SpriteRender;
import components.SpriteSheet;
import org.joml.Vector2f;
import util.AssetPool;

import java.awt.*;

public class LevelEditorScene extends Scene {

    private SpriteSheet sprites;
    private GameObject obj1;

    public LevelEditorScene() {}

    @Override
    public void init() {
        loadResources();
        this.camera = new Camera(new Vector2f());

        this.sprites = AssetPool.getSpriteSheet("assets/images/Sprite-0003.png");

        obj1 = new GameObject("Test Object",
                new Transform(new Vector2f(250, 250), new Vector2f(150, 150)));
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

    private int spriteIndex = 0;
    private float spriteFlipTimeLeft = 0.0f;
    private final float TIME_PER_FRAME = 0.2f;

    @Override
    public void update(float dt) {

        spriteFlipTimeLeft -= 3 * dt;

        if(spriteFlipTimeLeft <= 0) {
            spriteFlipTimeLeft = TIME_PER_FRAME;
            spriteIndex++;

            if(spriteIndex > 8) {
                spriteIndex = 0;
            }

            obj1.getComponent(SpriteRender.class).setSprite(sprites.getSprite(spriteIndex));

        }

        obj1.transform.position.x += 10 * dt;

        // Update all game objects
        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

        // Render scene
        this.renderer.render();
    }
}