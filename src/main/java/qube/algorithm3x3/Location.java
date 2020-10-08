package qube.algorithm3x3;

import qube.Side;

public enum Location
{
    CENTER(Minor.CENTER),

    TOP(Minor.EDGE),
    RIGHT(Minor.EDGE),
    BOTTOM(Minor.EDGE),
    LEFT(Minor.EDGE),

    TOP_LEFT(Minor.CORNER),
    TOP_RIGHT(Minor.CORNER),
    BOTTOM_RIGHT(Minor.CORNER),
    BOTTOM_LEFT(Minor.CORNER);

    public enum Minor
    {
        CENTER,
        EDGE,
        CORNER
    }

    private Minor minor_;

    Location(Minor minor)
    {
        minor_ = minor;
    }

    /**
     * Finds the location on the opposite side.
     *
     * @param   from    The side of the location.
     *
     * @return  Opposite location.
     */
    Location opposite(Side from)
    {
        if(from == Side.UP || from == Side.DOWN)   // Top and bottom are arranged differently.
        {
            switch(this)
            {
            case TOP: return BOTTOM;
            case BOTTOM: return TOP;
            default: return this;
            }
        }
        else
        {
            switch(this)
            {
            case RIGHT: return LEFT;
            case LEFT: return RIGHT;
            default:
                return this;
            }
        }
    }

    /**
     * Gets the side with the center directly below the edge's color.
     *
     * @param   side        Side of edge.
     * @param   location    Location of edge.
     *
     * @return              Outer side.
     */
    public static Side getEdgeOuterSide(Side side, Location location)
    {
        switch(side)
        {
        case FRONT:
            switch(location)
            {
            case TOP: return Side.UP;
            case RIGHT: return Side.RIGHT;
            case BOTTOM: return Side.DOWN;
            case LEFT: return Side.LEFT;
            }
        case BACK:
            switch(location)
            {
            case TOP: return Side.UP;
            case RIGHT: return Side.LEFT;
            case BOTTOM: return Side.DOWN;
            case LEFT: return Side.RIGHT;
            }
        case UP:
            switch(location)
            {
            case TOP: return Side.BACK;
            case RIGHT: return Side.RIGHT;
            case BOTTOM: return Side.FRONT;
            case LEFT: return Side.LEFT;
            }
        case DOWN:
            switch(location)
            {
            case TOP: return Side.FRONT;
            case RIGHT: return Side.RIGHT;
            case BOTTOM: return Side.BACK;
            case LEFT: return Side.LEFT;
            }
        case RIGHT:
            switch(location)
            {
            case TOP: return Side.UP;
            case RIGHT: return Side.BACK;
            case BOTTOM: return Side.DOWN;
            case LEFT: return Side.FRONT;
            }
        case LEFT:
            switch(location)
            {
            case TOP: return Side.UP;
            case RIGHT: return Side.FRONT;
            case BOTTOM: return Side.DOWN;
            case LEFT: return Side.BACK;
            }
        }

        throw new IllegalStateException("Unknown error occurred");
    }

    public Minor getMinor() { return minor_; }
}
