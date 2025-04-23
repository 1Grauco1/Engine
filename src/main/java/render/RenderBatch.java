package render;

import components.SpriteRender;
import engine.Window;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch {
    //Vertex
    //======
    //Pos                 Color
    //float, float,       float, float, float, float
    private final int POS_SIZE= 2;
    private final int COLOR_SIZE= 4;


    private final int POS_OFFSET= 0;
    private final int COLOR_OFFSET= POS_OFFSET + POS_SIZE + Float.BYTES;
    private final int VERTEX_SIZE= 6;
    private final int VERTEX_SIZE_BYTES= VERTEX_SIZE * Float.BYTES;

    private SpriteRender[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;

    private int vaoID, vboID;
    private int maxBatchSize;
    private Shader shader;

    public RenderBatch(int maxBatchSize){
        shader= new Shader("assets/shaders/default.glsl");
        shader.compileAndLinkShaders();

        this.sprites= new SpriteRender[maxBatchSize];
        this.maxBatchSize= maxBatchSize;

        //4 vertices quads
        vertices= new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numSprites= 0;
        this.hasRoom= true;
    }

    public void start(){
        // Generate and bind a Vertex Array Object
        vaoID= glGenVertexArrays();
        glBindVertexArray(vaoID);

        //Alocate space for vertices

        vboID= glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        //Create and upload indices buffer
        int eboID= glGenBuffers();
        int[] indices= generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        //Enable the buffer attribute pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);
    }

    public void addSprite(SpriteRender spr){
        //Get index and add renderObject
        int index= this.numSprites;
        this.sprites[index]= spr;
        this.numSprites++;

        //ADD properties to local vertices array
        loadVertexProperties(index);

        if(numSprites >= this.maxBatchSize){
            this.hasRoom= false;
        }
    }

    private void loadVertexProperties(int index){
        SpriteRender sprite= this.sprites[index];

        //Find offset within array(4 vertices per sprite)
        int offset= index * 4 * VERTEX_SIZE;

        Vector4f color= sprite.getColor();

        //Add vertices with the appropriate properties
        float xAdd= 1.0f;
        float yAdd= 1.0f;
        for(int i= 0; i < 4; i++){
            if(i == 1){
                yAdd= 0.0f;
            }else if(i == 2){
                xAdd= 0.0f;
            }else if(i == 3){
                yAdd= 1.0f;
            }

            //Load position
            vertices[offset]= sprite.gameObject.transform.position.x + (xAdd * sprite.gameObject.transform.scale.x);
            vertices[offset + 1]= sprite.gameObject.transform.position.y + (yAdd * sprite.gameObject.transform.scale.y);

            //Load color
            vertices[offset + 2]= color.x;
            vertices[offset + 3]= color.y;
            vertices[offset + 4]= color.z;
            vertices[offset + 5]= color.w;

            offset += VERTEX_SIZE;
        }
    }

    public void render(){
        //Rebuffer all data EVERY FRAME

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        //Use Shader
        shader.use();
        shader.uploadMat4f("uProj", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        shader.detach();

    }

    private int[] generateIndices(){
        //6 indices per quad (3 per triangle)
        int[] elements= new int[6 * maxBatchSize];
        for(int i= 0; i < maxBatchSize; i++){
            loadElementIndices(elements, i);
        }

        return elements;
    }

    private void loadElementIndices(int[] elements, int index){
        int offSetArrayIndex= 6 * index;
        int offset= 4 * index;

        // 3, 2, 0, 0, 2, 1        7, 6, 4, 4, 6, 5
        //Triangle 1
        elements[offSetArrayIndex]= offset + 3;
        elements[offSetArrayIndex + 1]= offset + 2;
        elements[offSetArrayIndex + 2]= offset + 0;

        //Triangle 2
        elements[offSetArrayIndex + 3]= offset + 0;
        elements[offSetArrayIndex + 4]= offset + 2;
        elements[offSetArrayIndex + 5]= offset + 1;

    }

    public boolean hasRoom(){
        return this.hasRoom;
    }
}
