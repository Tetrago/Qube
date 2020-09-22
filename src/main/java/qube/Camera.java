package qube;

import processing.core.PApplet;
import processing.core.PVector;

import static processing.core.PApplet.*;

public class Camera
{
    public static final float DRAG_SENSITIVITY = 0.8f;
    public static final float SCROLL_SENSITIVITY = 15.0f;

    private int distanceFromCenter_ = 0;
    private int rotY_, rotZ_;

    /**
     * Notifies the camera about mouse dragging.
     *
     * @param   canvas  Canvas of dragging.
     */
    public void notifyMouseDragged(PApplet canvas)
    {
        rotY_ -= (canvas.mouseX - canvas.pmouseX) * DRAG_SENSITIVITY;
        rotZ_ -= (canvas.mouseY - canvas.pmouseY) * DRAG_SENSITIVITY;
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
        float radY = radians(rotY_);
        float radZ = radians(rotZ_);

        float z = distanceFromCenter_ * cos(radY);
        float x = distanceFromCenter_ * sin(radY);
        float y = distanceFromCenter_ * sin(radZ);

        PVector pos = new PVector(x, y, z);
        pos.add(pos.copy().normalize().mult(distanceFromCenter_ * cos(radZ)));

        canvas.camera(pos.x, pos.y, pos.z, 0, 0, 0, 0, 1, 0);
    }
}
