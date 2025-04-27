package components;

import engine.Component;
import engine.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;
import render.Texture;

public class SpriteRender extends Component {
    private Vector4f color;
    private boolean dirty = true;
    private Sprite sprite;

    private Transform lastTransform;

    public SpriteRender(Vector4f color) {
        this.color = color;
        this.sprite= new Sprite(null);
    }

   public SpriteRender(Sprite sprite) {
        this.sprite = sprite;
        this.color = new Vector4f(1, 1, 1,1);
   }

    @Override
    public void start() {
        this.lastTransform= gameObject.transform.copy();
    }

    @Override
    public void update(float dt) {
        if(!this.lastTransform.equals(this.gameObject.transform)) {
            this.gameObject.transform.copy(this.lastTransform);
            dirty= true;
        }
    }

    public Texture getTexture() {
        return sprite.getTexture();
    }

    public Vector2f[] getTexCoords() {
        return sprite.getTexCoords();
    }

    // Dirty flag handling
    public boolean isDirty() {
        return dirty;
    }
    public void setClean() {
        this.dirty = false;
    }

    public Vector4f getColor() {
        return color;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        this.dirty= true;
    }

    public void setColor(Vector4f color) {
        if(!this.color.equals(color)) {
            this.dirty= true;
            this.color.set(color);
        }
    }



}