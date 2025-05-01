package engine;

import org.joml.Vector2f;

import javax.swing.tree.TreeNode;
import java.beans.VetoableChangeListener;

public class Transform {

    public Vector2f position;
    public Vector2f scale;

    public Transform(){
        init(new Vector2f(), new Vector2f());
    }

    public Transform(Vector2f position){ init(position, new Vector2f());
    }

    public Transform(Vector2f position, Vector2f scale){
        init(position, scale);
    }

    public void init(Vector2f position, Vector2f scale){
        this.position= position;
        this.scale= scale;
    }

    public Transform copy(){
        return new Transform(new Vector2f(this.position), new Vector2f(scale));
    }

    public void copy(Transform to){
        to.position.set(this.position);
        to.scale.set(this.scale);
    }

    public boolean equals(Object o){
        if(o == null) return false;
        if(!(o instanceof Transform)) return false;

        Transform t = (Transform)o;
        return this.position.equals(t.position) && this.scale.equals(t.scale);
    }
}
