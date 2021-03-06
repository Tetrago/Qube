package qube;

import processing.core.PApplet;
import processing.core.PVector;

import static processing.core.PApplet.*;

public class Camera
{
    public static final float DRAG_SENSITIVITY = 0.8f;
    public static final float SCROLL_SENSITIVITY = 15.0f;
    public static final float DEGREE_LIMIT = 90;

    private final float minDistanceFromCenter_;
    private float distanceFromCenter_ = 1000;
    private float mdx_ = -45, mdy_ = -45;

    public Camera()
    {
        minDistanceFromCenter_ = PVector.dist(new PVector(0, 0, 0), new PVector(Face.TARGET_FACE_SIDE, Face.TARGET_FACE_SIDE, Face.TARGET_FACE_SIDE).mult(0.5f)) + 10;
    }

    /**
     * Notifies the camera about mouse dragging.
     *
     * @param   canvas  Canvas of dragging.
     */
    public void notifyMouseDragged(PApplet canvas)
    {
        mdx_ -= (canvas.mouseX - canvas.pmouseX) * DRAG_SENSITIVITY;
        mdy_ -= (canvas.mouseY - canvas.pmouseY) * DRAG_SENSITIVITY;

        mdy_ = Math.min(DEGREE_LIMIT, Math.max(-DEGREE_LIMIT, mdy_));
    }

    /**
     * Notifies the camera about mouse wheel movement.
     *
     * @param   delta   Change in mouse wheel.
     */
    public void notifyMouseWheel(int delta)
    {
        distanceFromCenter_ += delta * SCROLL_SENSITIVITY;
    }

    /**
     * Draws camera.
     *
     * @param   canvas  Canvas to draw on.
     */
    public void draw(PApplet canvas)
    {
        if(distanceFromCenter_ < minDistanceFromCenter_)
        {
            distanceFromCenter_ = minDistanceFromCenter_;
        }

        float rx = radians(mdx_);
        float ry = radians(mdy_);

        float z = distanceFromCenter_ * cos(rx);
        float x = distanceFromCenter_ * sin(rx);
        float y = distanceFromCenter_ * sin(ry);

        canvas.camera(x, y, z, 0, 0, 0, 0, 1, 0);
    }
}
