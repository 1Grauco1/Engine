package components;

import engine.Component;

public class FontRenderer extends Component {

    @Override
    public void start(){
        if(gameObject.getComponent(SpriteRender.class) != null){
            System.out.println("Found Font Renderer");
        }
    }

    @Override
    public void update(float dt) {

    }
}
