package qube;

import qube.algorithm3x3.ICube;
import qube.algorithm3x3.Location;

public class LocationSpace
{
    private final Side side_;
    private final Location location_;
    private final Color color_;

    public LocationSpace(Side side, Location location, Color color)
    {
        side_ = side;
        location_ = location;
        color_ = color;
    }

    /**
     * Finds the color on a cube rather then getting a stored color.
     * Useful after calling the {@code rotateCorner} method.
     *
     * <p>Does not update internal color.</p>
     *
     * @param   cube    Cube to use.
     *
     * @return          Color of location.
     */
    public Color determineColor(ICube cube)
    {
        return cube.getFace(side_).getColor(location_);
    }

    /**
     * Rotates a corner clockwise around the cube.
     *
     * <p>Color is not determined, and remains; most likely invalid.</p>
     *
     * @return  Location of rotated corner.
     */
    public LocationSpace rotateCorner()
    {
        switch(side_)
        {
        case FRONT:
            switch(location_)
            {
            case TOP_LEFT: return new LocationSpace(Side.LEFT, Location.TOP_RIGHT, color_);
            case TOP_RIGHT: return new LocationSpace(Side.UP, Location.BOTTOM_RIGHT, color_);
            case BOTTOM_RIGHT: return new LocationSpace(Side.RIGHT, Location.BOTTOM_LEFT, color_);
            case BOTTOM_LEFT: return new LocationSpace(Side.DOWN, Location.TOP_LEFT, color_);
            }
        case BACK:
            switch(location_)
            {
            case TOP_LEFT: return new LocationSpace(Side.RIGHT, Location.TOP_RIGHT, color_);
            case TOP_RIGHT: return new LocationSpace(Side.UP, Location.TOP_LEFT, color_);
            case BOTTOM_RIGHT: return new LocationSpace(Side.LEFT, Location.BOTTOM_LEFT, color_);
            case BOTTOM_LEFT: return new LocationSpace(Side.DOWN, Location.BOTTOM_RIGHT, color_);
            }
        case UP:
            switch(location_)
            {
            case TOP_LEFT: return new LocationSpace(Side.LEFT, Location.TOP_LEFT, color_);
            case TOP_RIGHT: return new LocationSpace(Side.BACK, Location.TOP_LEFT, color_);
            case BOTTOM_RIGHT: return new LocationSpace(Side.RIGHT, Location.TOP_LEFT, color_);
            case BOTTOM_LEFT: return new LocationSpace(Side.FRONT, Location.TOP_LEFT, color_);
            }
        case DOWN:
            switch(location_)
            {
            case TOP_LEFT: return new LocationSpace(Side.LEFT, Location.BOTTOM_RIGHT, color_);
            case TOP_RIGHT: return new LocationSpace(Side.FRONT, Location.BOTTOM_RIGHT, color_);
            case BOTTOM_RIGHT: return new LocationSpace(Side.RIGHT, Location.BOTTOM_RIGHT, color_);
            case BOTTOM_LEFT: return new LocationSpace(Side.BACK, Location.BOTTOM_RIGHT, color_);
            }
        case RIGHT:
            switch(location_)
            {
            case TOP_LEFT: return new LocationSpace(Side.FRONT, Location.TOP_RIGHT, color_);
            case TOP_RIGHT: return new LocationSpace(Side.UP, Location.TOP_RIGHT, color_);
            case BOTTOM_RIGHT: return new LocationSpace(Side.BACK, Location.BOTTOM_LEFT, color_);
            case BOTTOM_LEFT: return new LocationSpace(Side.DOWN, Location.TOP_RIGHT, color_);
            }
        case LEFT:
            switch(location_)
            {
            case TOP_LEFT: return new LocationSpace(Side.BACK, Location.TOP_RIGHT, color_);
            case TOP_RIGHT: return new LocationSpace(Side.UP, Location.BOTTOM_LEFT, color_);
            case BOTTOM_RIGHT: return new LocationSpace(Side.FRONT, Location.BOTTOM_LEFT, color_);
            case BOTTOM_LEFT: return new LocationSpace(Side.DOWN, Location.BOTTOM_LEFT, color_);
            }
        }

        throw new IllegalStateException("Unknown error occurred");
    }

    /**
     * Flips location along edge.
     *
     * <p>Color is not determined, and remains; most likely invalid.</p>
     *
     * @return  Flipped location.
     */
    public LocationSpace flipEdge()
    {
        switch(side_)
        {
        case FRONT:
            switch(location_)
            {
            case TOP: return new LocationSpace(Side.UP, Location.BOTTOM, color_);
            case RIGHT: return new LocationSpace(Side.RIGHT, Location.LEFT, color_);
            case BOTTOM: return new LocationSpace(Side.DOWN, Location.TOP, color_);
            case LEFT: return new LocationSpace(Side.LEFT, Location.RIGHT, color_);
            }
        case BACK:
            switch(location_)
            {
            case TOP: return new LocationSpace(Side.UP, Location.TOP, color_);
            case RIGHT: return new LocationSpace(Side.LEFT, Location.LEFT, color_);
            case BOTTOM: return new LocationSpace(Side.DOWN, Location.BOTTOM, color_);
            case LEFT: return new LocationSpace(Side.RIGHT, Location.RIGHT, color_);
            }
        case UP:
            switch(location_)
            {
            case TOP: return new LocationSpace(Side.BACK, Location.TOP, color_);
            case RIGHT: return new LocationSpace(Side.RIGHT, Location.TOP, color_);
            case BOTTOM: return new LocationSpace(Side.FRONT, Location.TOP, color_);
            case LEFT: return new LocationSpace(Side.LEFT, Location.TOP, color_);
            }
        case DOWN:
            switch(location_)
            {
            case TOP: return new LocationSpace(Side.FRONT, Location.BOTTOM, color_);
            case RIGHT: return new LocationSpace(Side.RIGHT, Location.BOTTOM, color_);
            case BOTTOM: return new LocationSpace(Side.BACK, Location.BOTTOM, color_);
            case LEFT: return new LocationSpace(Side.LEFT, Location.BOTTOM, color_);
            }
        case RIGHT:
            switch(location_)
            {
            case TOP: return new LocationSpace(Side.UP, Location.RIGHT, color_);
            case RIGHT: return new LocationSpace(Side.BACK, Location.LEFT, color_);
            case BOTTOM: return new LocationSpace(Side.DOWN, Location.RIGHT, color_);
            case LEFT: return new LocationSpace(Side.FRONT, Location.RIGHT, color_);
            }
        case LEFT:
            switch(location_)
            {
            case TOP: return new LocationSpace(Side.UP, Location.LEFT, color_);
            case RIGHT: return new LocationSpace(Side.FRONT, Location.LEFT, color_);
            case BOTTOM: return new LocationSpace(Side.DOWN, Location.LEFT, color_);
            case LEFT: return new LocationSpace(Side.BACK, Location.RIGHT, color_);
            }
        }

        throw new IllegalStateException("Unknown error occurred");
    }

    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof LocationSpace))
        {
            return false;
        }

        LocationSpace other = (LocationSpace)obj;

        return side_ == other.side_
                && location_ == other.location_
                && color_ == other.color_;
    }

    public Side getSide() { return side_; }
    public Location getLocation() { return location_; }
    public Color getColor() { return color_; }
}
