package render;

import components.SpriteRender;
import engine.GameObject;

import java.util.*;

public class Renderer {

    private final int MAX_BATCH_SIZE= 1000;
    private List<RenderBatch> batches;

    public Renderer(){
        this.batches= new ArrayList<>();
    }

    public void add(GameObject go){
        SpriteRender spr= go.getComponent(SpriteRender.class);
        if(spr != null){
            addSpr(spr);
        }
    }

    private void addSpr(SpriteRender sprite){
        boolean added= false;
        for(RenderBatch batch : batches){
            if(batch.hasRoom() && batch.getzIndex() == sprite.gameObject.getZIndex()){
                Texture tex= sprite.getTexture();
                if(tex == null || (batch.hasTexture(tex) || batch.hasTextureRoom())){
                    batch.addSprite(sprite);
                    added= true;
                    break;
                }

            }
        }

        if(!added){
            RenderBatch newBatch= new RenderBatch(MAX_BATCH_SIZE, sprite.gameObject.getZIndex());
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sprite);
            Collections.sort(batches);
        }
    }

    public void render(){
        for(RenderBatch batch : batches){
            batch.render();
        }
    }

}
