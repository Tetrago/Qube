package qube;

import processing.core.PApplet;
import processing.core.PVector;

public class Cube
{
    public static final int TILE_SIZE = 100;

    private int dimensions_;
    private Face[] faces_;

    /**
     * Constructs a cube with faces.
     *
     * @param   dimensions  Width and height of each face of the cube.
     */
    public Cube(int dimensions)
    {
        dimensions_ = dimensions;
        faces_ = new Face[6];

        Color[] colors = Color.values();
        for(int i = 0; i < 6; ++i)
        {
            faces_[i] = new Face(dimensions, colors[i]);
        }
    }

    /**
     * Draws cube.
     *
     * @param   canvas    Canvas to draw on.
     */
    public void draw(PApplet canvas)
    {
        for(Side side : Side.values())
        {
            PVector pos = side.getPosition();
            PVector rot = side.getRotation();

            canvas.pushMatrix();
            canvas.translate(pos.x * TILE_SIZE * (dimensions_ * 0.5f), pos.y * TILE_SIZE * (dimensions_ * 0.5f), pos.z * TILE_SIZE * (dimensions_ * 0.5f));
            canvas.rotate(side.getAngle(), rot.x, rot.y, rot.z);

            faces_[side.ordinal()].draw(canvas);

            canvas.popMatrix();
        }
    }

    public int getDimensions() { return dimensions_; }
}
