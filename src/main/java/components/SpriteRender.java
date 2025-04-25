package components;

import engine.Component;
import org.joml.Vector2f;
import org.joml.Vector4f;
import render.Texture;

public class SpriteRender extends Component {
    private Vector4f color;
    private Vector2f[] texCoords;
    private Texture texture;
    private boolean dirty = true;

    // Constructors
    public SpriteRender(Vector4f color) {
        this.color = new Vector4f(color);
        this.texCoords = generateDefaultTexCoords();
    }

    public SpriteRender(Texture texture) {
        this.texture = texture;
        this.color = new Vector4f(1, 1, 1, 1);
        this.texCoords = generateDefaultTexCoords();
    }

    public SpriteRender(Texture texture, Vector2f[] texCoords) {
        this.texture = texture;
        this.color = new Vector4f(1, 1, 1, 1);
        this.texCoords = new Vector2f[4];
        System.arraycopy(texCoords, 0, this.texCoords, 0, 4);
    }

    // Default texture coordinates (full texture)
    private Vector2f[] generateDefaultTexCoords() {
        return new Vector2f[] {
                new Vector2f(1, 1),
                new Vector2f(1, 0),
                new Vector2f(0, 0),
                new Vector2f(0, 1)
        };
    }

    @Override
    public void start() {
        // Initialization if needed
    }

    @Override
    public void update(float dt) {
        // Update logic if needed
    }

    // Getters and setters
    public Vector4f getColor() {
        return new Vector4f(this.color);
    }

    public void setColor(Vector4f color) {
        this.color.set(color);
        this.dirty = true;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
        this.dirty = true;
    }

    public Vector2f[] getTexCoords() {
        Vector2f[] copy = new Vector2f[4];
        System.arraycopy(texCoords, 0, copy, 0, 4);
        return copy;
    }

    public void setTexCoords(Vector2f[] texCoords) {
        System.arraycopy(texCoords, 0, this.texCoords, 0, 4);
        this.dirty = true;
    }

    // Dirty flag handling
    public boolean isDirty() {
        return dirty;
    }

    public void setClean() {
        this.dirty = false;
    }

    public void setDirty() {
        this.dirty = true;
    }
}