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

    /**
     * Finds the opposite side.
     *
     * @return  Opposite side.
     */
    public Side opposite()
    {
        switch(this)
        {
        default:
        case FRONT: return BACK;
        case BACK:  return FRONT;
        case UP:    return DOWN;
        case DOWN:  return UP;
        case RIGHT: return LEFT;
        case LEFT:  return RIGHT;
        }
    }

    /**
     * Move side in direction.
     *
     * @param   side    Direction to move.
     *
     * @return          New side.
     */
    public Side move(Side side)
    {
        switch(this)
        {
        case FRONT:
            switch(side)
            {
            case UP: return UP;
            case DOWN: return DOWN;
            case RIGHT: return RIGHT;
            case LEFT: return LEFT;
            }
        case BACK:
            switch(side)
            {
            case UP: return UP;
            case DOWN: return DOWN;
            case RIGHT: return LEFT;
            case LEFT: return RIGHT;
            }
        case UP:
            switch(side)
            {
            case UP: return BACK;
            case DOWN: return FRONT;
            case RIGHT: return RIGHT;
            case LEFT: return LEFT;
            }
        case DOWN:
            switch(side)
            {
            case UP: return FRONT;
            case DOWN: return BACK;
            case RIGHT: return RIGHT;
            case LEFT: return LEFT;
            }
        case RIGHT:
            switch(side)
            {
            case UP: return UP;
            case DOWN: return DOWN;
            case RIGHT: return BACK;
            case LEFT: return FRONT;
            }
        case LEFT:
            switch(side)
            {
            case UP: return UP;
            case DOWN: return DOWN;
            case RIGHT: return FRONT;
            case LEFT: return BACK;
            }
        }

        throw new IllegalStateException("Unknown error occurred");
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
