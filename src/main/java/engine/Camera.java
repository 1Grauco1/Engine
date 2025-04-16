package engine;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {
    private Matrix4f projectionMatrix; // Projection matrix
    private Matrix4f viewMatrix;       // View matrix
    private Vector2f position;         // Camera position


    //Creates the camera at the given position
    public Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        adjustProjection();
    }

    //Sets up the orthographic projection (40x21 tiles of 32px).
    public void adjustProjection() {
        projectionMatrix.identity();
        projectionMatrix.ortho(0.0f, 32.0f * 40.0f, 0.0f, 32.0f * 21.0f, 0.0f, 100.0f);
    }


    //Returns the view matrix from the camera's position.
    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f); // Direction forward
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);      // Up direction

        viewMatrix.identity();
        viewMatrix.lookAt(
                new Vector3f(position.x, position.y, 20.0f),              // Camera position
                cameraFront.add(position.x, position.y, 0.0f),            // Target
                cameraUp                                                  // Up
        );

        return this.viewMatrix;
    }

    //Returns the projection matrix.
    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }
}
