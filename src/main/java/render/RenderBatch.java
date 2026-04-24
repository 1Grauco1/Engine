package render;

import components.SpriteRender;
import engine.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class RenderBatch implements Comparable<RenderBatch> {
    // Vertex layout:
    // Pos(2) | Color(4) | TexCoords(2) | TexId(1)
    private final int POS_SIZE        = 2;
    private final int COLOR_SIZE      = 4;
    private final int TEX_COORDS_SIZE = 2;
    private final int TEX_ID_SIZE     = 1;

    private final int POS_OFFSET        = 0;
    private final int COLOR_OFFSET      = POS_OFFSET + POS_SIZE;
    private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE;
    private final int TEX_ID_OFFSET     = TEX_COORDS_OFFSET + TEX_COORDS_SIZE;

    private final int VERTEX_SIZE       = 9;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private SpriteRender[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;
    private int[] texSlots = {0, 1, 2, 3, 4, 5, 6, 7};

    private int vaoID, vboID;
    private int maxBatchSize;
    private Shader shader;
    private List<Texture> textures;
    private int zIndex;

    public RenderBatch(int maxBatchSize, int zIndex) {
        this.shader       = AssetPool.getShader("assets/shaders/default.glsl");
        this.sprites      = new SpriteRender[maxBatchSize];
        this.maxBatchSize = maxBatchSize;
        this.vertices     = new float[maxBatchSize * 4 * VERTEX_SIZE];
        this.numSprites   = 0;
        this.hasRoom      = true;
        this.textures     = new ArrayList<>();
        this.zIndex       = zIndex;
    }

    public void start() {
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        int eboID    = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, POS_SIZE,        GL_FLOAT, false, VERTEX_SIZE_BYTES, (long) POS_OFFSET        * Float.BYTES);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, COLOR_SIZE,      GL_FLOAT, false, VERTEX_SIZE_BYTES, (long) COLOR_OFFSET      * Float.BYTES);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, (long) TEX_COORDS_OFFSET * Float.BYTES);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(3, TEX_ID_SIZE,     GL_FLOAT, false, VERTEX_SIZE_BYTES, (long) TEX_ID_OFFSET     * Float.BYTES);
        glEnableVertexAttribArray(3);
    }

    public void addSprite(SpriteRender spr) {
        int index = this.numSprites;
        this.sprites[index] = spr;
        this.numSprites++;

        if (spr.getTexture() != null && !textures.contains(spr.getTexture())) {
            textures.add(spr.getTexture());
        }

        loadVertexProperties(index);

        if (numSprites >= this.maxBatchSize) {
            this.hasRoom = false;
        }
    }

    private void loadVertexProperties(int index) {
        SpriteRender sprite = this.sprites[index];
        int offset          = index * 4 * VERTEX_SIZE;
        Vector4f color      = sprite.getColor();
        Vector2f[] texCoords = sprite.getTexCoords();

        int texId = 0;
        if (sprite.getTexture() != null) {
            texId = textures.indexOf(sprite.getTexture()) + 1;
        }

        float xAdd = 1.0f;
        float yAdd = 1.0f;
        for (int i = 0; i < 4; i++) {
            if (i == 1)      yAdd = 0.0f;
            else if (i == 2) xAdd = 0.0f;
            else if (i == 3) yAdd = 1.0f;

            vertices[offset]     = sprite.gameObject.transform.position.x + (xAdd * sprite.gameObject.transform.scale.x);
            vertices[offset + 1] = sprite.gameObject.transform.position.y + (yAdd * sprite.gameObject.transform.scale.y);
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;
            vertices[offset + 6] = texCoords[i].x;
            vertices[offset + 7] = texCoords[i].y;
            vertices[offset + 8] = texId;

            offset += VERTEX_SIZE;
        }
    }

    public void render() {
        boolean rebufferData = false;
        for (int i = 0; i < numSprites; i++) {
            if (sprites[i].isDirty()) {
                loadVertexProperties(i);
                sprites[i].setClean();
                rebufferData = true;
            }
        }

        if (rebufferData) {
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }

        shader.use();
        shader.uploadMat4f("uProj", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());

        for (int i = 0; i < textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }
        shader.uploadIntArray("uTextures", texSlots);

        glBindVertexArray(vaoID);
        glDrawElements(GL_TRIANGLES, numSprites * 6, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);

        for (int i = 0; i < textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).unbind();
        }

        shader.detach();
    }

    private int[] generateIndices() {
        int[] elements = new int[6 * maxBatchSize];
        for (int i = 0; i < maxBatchSize; i++) {
            loadElementIndices(elements, i);
        }
        return elements;
    }

    private void loadElementIndices(int[] elements, int index) {
        int offsetArrayIndex = 6 * index;
        int offset           = 4 * index;

        elements[offsetArrayIndex]     = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset + 0;
        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }

    public boolean hasRoom() {
        return hasRoom && textures.size() < 8;
    }

    public boolean hasTextureRoom() {
        return textures.size() < 8;
    }

    public boolean hasTexture(Texture tex) {
        return textures.contains(tex);
    }

    public int getzIndex() {
        return this.zIndex;
    }

    public void cleanup() {
        glDeleteBuffers(vboID);
        glDeleteVertexArrays(vaoID);
    }

    // BUG FIX: removed @NotNull from org.jetbrains.annotations (not in build.gradle → compile error)
    @Override
    public int compareTo(RenderBatch o) {
        return Integer.compare(this.zIndex, o.getzIndex());
    }
}
