package qube;

import processing.core.PApplet;
import processing.core.PVector;

public enum Side
{
    FRONT(new PVector(0, 0, 1), new PVector(0, 0, 0), 0),
    BACK(new PVector(0, 0, -1), new PVector(0, 1, 0), 180),
    UP(new PVector(0, -1, 0), new PVector(1, 0, 0), 90),
    DOWN(new PVector(0, 1, 0), new PVector(-1, 0, 0), 90),
    RIGHT(new PVector(1, 0, 0), new PVector(0, 1, 0), 90),
    LEFT(new PVector(-1, 0, 0), new PVector(0, -1, 0), 90);

    private final PVector position_;
    private final PVector rotation_;
    private final float angle_;

    /**
     * Constructs a side.
     *
     * @param   pos     Relative position of the side.
     * @param   rot     Rotation of side.
     * @param   angle   Value of rotation in degrees.
     */
    Side(PVector pos, PVector rot, float angle)
    {
        position_ = pos;
        rotation_ = rot;
        angle_ = PApplet.radians(angle);
    }

    public PVector getPosition() { return position_; }
    public PVector getRotation() { return rotation_; }

    /**
     * Returns the angle.
     *
     * @return  Angle in radians.
     */
    public float getAngle() { return angle_; }
}
