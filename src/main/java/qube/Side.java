package qube;

import processing.core.PApplet;
import processing.core.PVector;

public enum Side
{
    Front(new PVector(0, 0, 1), new PVector(0, 0, 0), 0),
    Back(new PVector(0, 0, -1), new PVector(0, 1, 0), 180),
    Up(new PVector(0, 1, 0), new PVector(1, 0, 0), 90),
    Down(new PVector(0, -1, 0), new PVector(-1, 0, 0), 90),
    Right(new PVector(1, 0, 0), new PVector(0, 1, 0), 90),
    Left(new PVector(-1, 0, 0), new PVector(0, -1, 0), 90);

    private PVector position_;
    private PVector rotation_;
    private float angle_;

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
