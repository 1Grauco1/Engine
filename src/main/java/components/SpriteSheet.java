// SpriteSheet.java with improvements
package components;

import org.joml.Vector2f;
import render.Texture;
import java.util.ArrayList;
import java.util.List;

public class SpriteSheet {
    private Texture texture;
    private List<Sprite> sprites;

    public SpriteSheet(Texture texture, int spriteWidth, int spriteHeight,
                       int numSprites, int spacing) {
        if (texture == null) {
            throw new IllegalArgumentException("Texture cannot be null");
        }

        this.sprites = new ArrayList<>();
        this.texture = texture;

        int textureWidth = texture.getWidth();
        int textureHeight = texture.getHeight();

        int currentX = 0;
        int currentY = textureHeight - spriteHeight; // Start from top

        for (int i = 0; i < numSprites; i++) {
            // Calculate texture coordinates (normalized)
            float leftX = currentX / (float)textureWidth;
            float rightX = (currentX + spriteWidth) / (float)textureWidth;
            float bottomY = currentY / (float)textureHeight;
            float topY = (currentY + spriteHeight) / (float)textureHeight;

            Vector2f[] texCoords = {
                    new Vector2f(rightX, bottomY),    // Top-right
                    new Vector2f(rightX, topY), // Bottom-right
                    new Vector2f(leftX, topY),   // Bottom-left
                    new Vector2f(leftX, bottomY)       // Top-left
            };

            this.sprites.add(new Sprite(this.texture, texCoords));

            // Move to next sprite
            currentX += spriteWidth + spacing;
            if (currentX + spriteWidth > textureWidth) {
                currentX = 0;
                currentY -= spriteHeight + spacing;

                if (currentY < 0) {
                    break; // Stop if we go past the bottom
                }
            }
        }
    }

    public Sprite getSprite(int index) {
        if (index < 0 || index >= sprites.size()) {
            throw new IndexOutOfBoundsException(
                    String.format("Sprite index %d out of bounds (0-%d)",
                            index, sprites.size()-1));
        }
        return this.sprites.get(index);
    }

    public int size() {
        return sprites.size();
    }
}